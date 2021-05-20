package com.nsoft.laundromat.controller.shop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.shop.scan.CaptureActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;

public class AddShopActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private EditText edtShopName;
    private EditText edtMachineId;
    private EditText edtBranch;
    private Button btnAddShop;
    private ImageView imgCapture;

    private static final int CAMERA_PERMISSION = 300;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shop);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_add_shop);


        initBasicUI();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.getInt("scan_flag") == 1){
                edtShopName.setText(bundle.getString("shop_name"));
                edtMachineId.setText(bundle.getString("machine_id"));
                if (bundle.getString("branch_name").equals("N/A")){
                    edtBranch.setText("");
                }
                else {
                    edtBranch.setText(bundle.getString("branch_name"));
                }
            }
        }

        showBottomInformation();

        Global.isMainActivity = false;
        Global.strAddShopActivity = "AddShopActivity";
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initBasicUI(){
        imgTopLeft = findViewById(R.id.img_top_left);
        imgTopLeft.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                finish();
            }
        });

        txtTopTitle = findViewById(R.id.txt_top_title);
        txtTopTitle.setText(R.string.title_add_shop);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(AddShopActivity.this, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
//                finish();
            }
        });
        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);
        edtShopName = findViewById(R.id.edt_shop_name);
        edtMachineId = findViewById(R.id.edt_machine_id);
        edtBranch = findViewById(R.id.edt_branch);
        btnAddShop = findViewById(R.id.btn_add_shop);
        btnAddShop.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                addNewShop();
            }
        });
        imgCapture = findViewById(R.id.img_capture);
        imgCapture.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (confirmCameraPermission()){
                    gotoCaptureActivity();
                }
            }
        });

        mSlidingUpPanelLayout = findViewById(R.id.activity_add_shop);
        mSlidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED && previousState != SlidingUpPanelLayout.PanelState.EXPANDED) {
                    sendNoticeRequest(NOTICE_GET, EMPTY_STRING);
                }
            }
        });
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void gotoCaptureActivity(){
        Intent intent = new Intent(AddShopActivity.this, CaptureActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisActivity.startActivity(intent);
        finish();
    }

    private void addNewShop(){
        String shopName = edtShopName.getText().toString();
        String machineId = edtMachineId.getText().toString();
        String branch = edtBranch.getText().toString();

        if (checkInputValue(shopName, machineId, branch)){
            showProgressDialog(getString(R.string.message_content_loading));
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_add_shop);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("shop_name", shopName);
            paramsPost.put("machine_id", machineId);
            paramsPost.put("branch", branch);
            paramsPost.put("user_id", Global.userId + "");
            httpCallPost.setParams(paramsPost);
            new HttpRequest(){
                @Override
                public void onResponse(String str) {
                    super.onResponse(str);
                    try {
                        JSONObject response = new JSONObject(str);
                        int responseCode = (int) response.get("code");
                        if (responseCode == Global.RESULT_SUCCESS) {
                            CustomProgress.dismissDialog();
                            showToast("New shop successfully added!");
                        }
                        else if (responseCode == Global.RESULT_MACHINE_ID_EXIST) {
                            CustomProgress.dismissDialog();
                            showToast("This shop already registered");
                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("Failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.dismissDialog();
                        showToast("Network error");
                    }
                }
            }.execute(httpCallPost);
        }
    }

    private boolean checkInputValue(String shoptName, String machineId, String branch){
        boolean isValue = true;
        if(machineId.equals("")){
            showToast("Please input Machine ID");
            isValue = false;
        }
        else if (shoptName.equals("")){
            showToast("Please input Shop description");
            isValue = false;
        }
        return isValue;
    }


    public boolean confirmCameraPermission() {
        if (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    gotoCaptureActivity();
                }else{
                    showToast("Please open camera permission");
                }
                break;
            default:
                break;
        }
    }
}
