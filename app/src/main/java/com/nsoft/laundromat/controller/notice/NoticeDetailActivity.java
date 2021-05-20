package com.nsoft.laundromat.controller.notice;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.DATA_REQUESTED;
import static com.nsoft.laundromat.common.Global.NOTICE_ACTED;
import static com.nsoft.laundromat.common.Global.NOTICE_HIDDEN;
import static com.nsoft.laundromat.common.Global.NOTICE_MESSAGE;
import static com.nsoft.laundromat.common.Global.NOTICE_NOTICE;
import static com.nsoft.laundromat.common.Global.NOTICE_REQUEST;
import static com.nsoft.laundromat.common.Global.NOTICE_VIEWED;
import static com.nsoft.laundromat.common.Global.NOTICE_WARNING;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.currentActivityName;
import static com.nsoft.laundromat.common.Global.currentNoticeActionStatus;
import static com.nsoft.laundromat.common.Global.currentNoticeContent;
import static com.nsoft.laundromat.common.Global.currentNoticeNo;
import static com.nsoft.laundromat.common.Global.currentNoticeTitle;
import static com.nsoft.laundromat.common.Global.currentNoticeType;
import static com.nsoft.laundromat.common.Global.currentNoticeViewStatus;
import static com.nsoft.laundromat.common.Global.pastActivityName;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;

public class NoticeDetailActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private ImageView imgNoticeType;
    private TextView txtNoticeTitle;
    private TextView txtNoticeContent;
    private Button btnFirst;
    private Button btnSecond;

    private String strUUID = "";
    private boolean isFirst = true;

    private static Timer connectionTimer;
    private static int connectCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_notice_detail);
        currentActivityName = "NoticeDetailActivity";

        initBasicUI();
        showBottomInformation();
        showNoticeInformation();

        updateNoticeAsViewed();

        pastActivityName = getClass().getSimpleName();
        strNoticeDetailActivity = "NoticeDetailActivity";
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
        txtTopTitle.setText(R.string.title_notice_detail);
        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);

        imgNoticeType = findViewById(R.id.img_notice_type);
        txtNoticeTitle = findViewById(R.id.txt_notice_title);
        txtNoticeContent = findViewById(R.id.txt_notice_content);
        btnFirst = findViewById(R.id.btn_first);
        btnFirst.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                isFirst = false;
                String strKey = currentNoticeNo + "_" + 1;
                sendNoticeRequest(NOTICE_ACTED, strKey);
            }
        });
        btnSecond = findViewById(R.id.btn_second);
        btnSecond.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                isFirst = false;
                if (currentNoticeType.equals(NOTICE_REQUEST)){
                    if (btnSecond.getText().toString().equals("No")){
                        String strKey = currentNoticeNo + "_" + 0;
                        sendNoticeRequest(NOTICE_ACTED, strKey);
                    }
                    else {
                        String strKey = currentNoticeNo + "";
                        sendNoticeRequest(NOTICE_HIDDEN, strKey);
                    }
                }
                else if (currentNoticeType.equals(NOTICE_NOTICE)){
                    String strKey = currentNoticeNo + "";
                    sendNoticeRequest(NOTICE_HIDDEN, strKey);
                }
                else if (currentNoticeType.equals(NOTICE_MESSAGE)){
                    String strKey = currentNoticeNo + "";
                    sendNoticeRequest(NOTICE_HIDDEN, strKey);
                }
                else if (currentNoticeType.equals(NOTICE_WARNING)){
                    String strKey = currentNoticeNo + "";
                    sendNoticeRequest(NOTICE_HIDDEN, strKey);
                }
            }
        });
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void showNoticeInformation(){
        if (currentNoticeType.equals(NOTICE_REQUEST)){
            imgNoticeType.setImageDrawable(getResources().getDrawable(R.drawable.icon_notice_question));
            if (!currentNoticeActionStatus.equals("True")){
                btnFirst.setVisibility(View.VISIBLE);
                btnFirst.setText("Yes");
                btnSecond.setText("No");
            }
            else {
                btnFirst.setVisibility(View.GONE);
                btnSecond.setText("HIDE");
            }
        }
        else if (currentNoticeType.equals(NOTICE_NOTICE)){
            imgNoticeType.setImageDrawable(getResources().getDrawable(R.drawable.icon_notice_bell));
            btnFirst.setVisibility(View.GONE);
            btnSecond.setText("HIDE");
        }
        else if (currentNoticeType.equals(NOTICE_MESSAGE)){
            imgNoticeType.setImageDrawable(getResources().getDrawable(R.drawable.icon_notice_post));
            btnFirst.setVisibility(View.GONE);
            btnSecond.setText("HIDE");
        }
        else if (currentNoticeType.equals(NOTICE_WARNING)){
            imgNoticeType.setImageDrawable(getResources().getDrawable(R.drawable.icon_notice_remark));
            btnFirst.setVisibility(View.GONE);
            btnSecond.setText("HIDE");
        }
        txtNoticeTitle.setText(currentNoticeTitle);
        txtNoticeContent.setText(currentNoticeContent);
    }

    private void updateNoticeAsViewed(){
        if (!currentNoticeViewStatus.equals("True")){
            isFirst = true;
            String strKey = currentNoticeNo + "";
            sendNoticeRequest(NOTICE_VIEWED, strKey);
        }
        else {
            isFirst = false;
        }
    }

    // region ----- notice  -----
    public void sendNoticeRequest(int sqlNo, String searchKey){
        showProgressDialog(getResources().getString(R.string.loading_press));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_request_uuid);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", Global.userId + "");
        paramsPost.put("unique_id", Global.userUniqueId);
        paramsPost.put("machine_id", Global.MACHINE_ID);
        paramsPost.put("request_by", Global.userEmail);
        paramsPost.put("sql_no", sqlNo + "");
        paramsPost.put("status_id", DATA_REQUESTED + "");
        paramsPost.put("search_key", searchKey);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONObject responseData =  response.getJSONObject("data");
                        strUUID = responseData.getString("uuid");
                        startConnection();
                    }
                    else if (responseCode == Global.RESULT_INCORRECT_UUID){
                        CustomProgress.dismissDialog();
                        tryLogOut();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("getting uuid failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }

    private void getNoticeData(String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_notice_data);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("unique_id", uniqueId);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {

                        connectionSuccess();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        if (connectCount == Global.SERVER_CONNECTION_COUNT){
                            showConnectFailedUI();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (connectCount == Global.SERVER_CONNECTION_COUNT){
                        showConnectFailedUI();
                    }
                }

            }
        }.execute(httpCallPost);
    }

    private void startConnection() {
        initConnectionTimer();
    }

    private void stopConnection(){
        stopConnectionTimer();
    }

    public void initConnectionTimer() {
        stopConnection();
        connectCount = 0;
        connectionTimer = new Timer();
        connectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectCount++;
                if (connectCount > Global.SERVER_CONNECTION_COUNT){
                    stopConnection();
                }
                else{
                    getNoticeData(strUUID);
                }
            }
        }, 1000, 1000);
    }

    public void stopConnectionTimer(){
        if(connectionTimer != null){
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
    }
    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        if (!isFirst){
            if (currentNoticeType.equals(NOTICE_REQUEST)){
                if (!btnSecond.getText().toString().equals("No")){
                    btnFirst.setVisibility(View.GONE);
                    btnSecond.setVisibility(View.INVISIBLE);
                }
                else {
                    btnSecond.setText("HIDE");
                    btnFirst.setVisibility(View.GONE);
                }
            }
            else {
                btnSecond.setVisibility(View.INVISIBLE);
            }
        }
        else {
            isFirst = false;
        }
    }

    private void connectionSuccess(){
        stopConnectionTimer();
        showConnectSuccessUI();
    }
    // endregion
}
