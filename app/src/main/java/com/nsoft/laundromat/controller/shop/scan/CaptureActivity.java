package com.nsoft.laundromat.controller.shop.scan;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.shop.AddShopActivity;
import com.nsoft.laundromat.controller.shop.scan.camera.CameraManager;
import com.nsoft.laundromat.controller.shop.scan.decoding.CaptureActivityHandler;
import com.nsoft.laundromat.controller.shop.scan.decoding.InactivityTimer;
import com.nsoft.laundromat.controller.shop.scan.decoding.RGBLuminanceSource;
import com.nsoft.laundromat.controller.shop.scan.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback {
    private static final int REQUEST_CODE_SCAN_GALLERY = 100;

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private String STR_UUID = "";
    private int DEVICE_TYPE = 0;
    private String branchName = "";
    private String shopName = "";
    private String machineId = "";


    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;

    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private boolean vibrate;
    private static final long VIBRATE_DURATION = 200L;
    private static final float BEEP_VOLUME = 0.10f;
    private String photo_path;

    private Bitmap scanBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        thisContext = this;
        thisActivity = this;
        thisView = this.findViewById(R.id.activity_capture);

        CameraManager.init(getApplication());

        initBasicUI();

        STR_UUID = "";
        DEVICE_TYPE = 0;
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.scanner_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                gotoAddShopActivity();
            }
        });

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_capture);

        viewfinderView = findViewById(R.id.viewfinder_content);
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        } else {
            String[] strSplit = resultString.split(",");
            if (strSplit.length == 3) {
                branchName = strSplit[0];
                shopName = strSplit[1];
                machineId = strSplit[2];
                Intent intent = new Intent(thisContext, AddShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("scan_flag", 1);
                intent.putExtra("branch_name", branchName);
                intent.putExtra("shop_name", shopName);
                intent.putExtra("machine_id", machineId);
                finish();
                thisContext.startActivity(intent);
            }
             else {
                showToast(getResources().getString(R.string.str_scan_incorrect));
                gotoAddShopActivity();
            }
        }
    }

    private void gotoAddShopActivity(){
        Intent intent = new Intent(thisContext, AddShopActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        thisContext.startActivity(intent);
    }

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    public Result scanningImage(String path) {
        if(TextUtils.isEmpty(path)){
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (requestCode==RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SCAN_GALLERY:
                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                    if (cursor.moveToFirst()) {
                        photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    }
                    cursor.close();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                String resultString = result.getText();
                                if (resultString.indexOf("strUUID") > -1 && resultString.indexOf("deviceType") > -1 ) {
                                    String[] strSplit = resultString.split("&");
                                    if (strSplit.length > 1) {
                                        DEVICE_TYPE = Integer.parseInt(strSplit[1].split("=")[1]);
                                        STR_UUID = strSplit[0].split("=")[1];

                                        Intent intent = new Intent(thisContext, AddShopActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        intent.putExtra("urlParam", resultString);

                                        finish();

                                        thisContext.startActivity(intent);
                                    } else {
                                        onPause();
                                    }
                                } else {
                                    onPause();
                                }
                            } else {
                                Message m = handler.obtainMessage();
                                m.what = R.id.decode_failed;
                                m.obj = getString(R.string.str_scan_failed);
                                handler.sendMessage(m);
                            }
                        }
                    }).start();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };


}
