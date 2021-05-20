package com.nsoft.laundromat.controller.offer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.model.OfferDetailObject;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.OFFERS_GET_DETAIL;
import static com.nsoft.laundromat.common.Global.OFFERS_SAVE_DETAIL;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentOfferCategory;
import static com.nsoft.laundromat.common.Global.currentOfferCode;
import static com.nsoft.laundromat.common.Global.currentOfferDescription;
import static com.nsoft.laundromat.common.Global.currentOfferPrice;
import static com.nsoft.laundromat.common.Global.isMainActivity;
import static com.nsoft.laundromat.common.Global.offerDetailObjectArrayList;

public class OfferDetailActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgHome;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtOfferDescription;
    private TextView txtOfferCode;
    private TextView txtOfferCategory;
    private EditText txtOfferPrice;
    private ListView lstOfferDetail;
    private LinearLayout laySave;

    private String requestUUID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_detail);
        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_offer_detail);

        initBasicUI();
        sendRequestToServer(OFFERS_GET_DETAIL, currentOfferCode);
        isMainActivity = false;
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
        txtTopTitle.setText(getString(R.string.title_offer_detail));
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(OfferDetailActivity.this, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
//                finish();
            }
        });
        layMainContent = findViewById(R.id.lay_main);
        layDisconnect = findViewById(R.id.lay_disconnect);
        txtError = findViewById(R.id.txt_error);
        layLoading = findViewById(R.id.lay_loading);
        btnTryAgain = findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startConnection();
            }
        });
        txtOfferDescription = findViewById(R.id.txt_description);
        txtOfferCode = findViewById(R.id.txt_code);
        txtOfferCategory= findViewById(R.id.txt_category);
        txtOfferPrice = findViewById(R.id.edt_price);
        lstOfferDetail = findViewById(R.id.lst_offer_detail);

        laySave = findViewById(R.id.lay_save);
        laySave.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                saveOfferDetail();
            }
        });
    }

    private void showOfferDetailList(){
        OfferDetailAdapter reportAdapter = new OfferDetailAdapter(thisContext, R.layout.item_offer_detail, Global.offerDetailObjectArrayList);
        lstOfferDetail.setAdapter(reportAdapter);
    }

    private void showOfferDetailInformation(){
        txtOfferCategory.setText(Global.currentOfferCategory);
        txtOfferCode.setText(Global.currentOfferCode);
        txtOfferDescription.setText(Global.currentOfferDescription);
        txtOfferPrice.setText(standardDecimalFormat(Global.currentOfferPrice));
    }

    private void saveOfferDetail(){
        String price = "";
        price = txtOfferPrice.getText().toString();
        if (price.equals("")){
            showToast("Please input price");
        }
        else{
            sendRequestToServer(OFFERS_SAVE_DETAIL, price + "_" + currentOfferCode);
        }
    }

    private void sendRequestToServer(final int sqlNo, String searchKey){
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
        paramsPost.put("status_id", Global.DATA_REQUESTED + "");
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
                        if (sqlNo == OFFERS_SAVE_DETAIL){
                            showToast("successfully saved");
                        }
                        else if (sqlNo == OFFERS_GET_DETAIL){
                            JSONObject responseData =  response.getJSONObject("data");
                            requestUUID = responseData.getString("uuid");
                            startConnection();
                        }
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
    private void getRequestData(final String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_offer_detail);
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
                        JSONObject responseData =  response.getJSONObject("data");
                        JSONArray tempContent = responseData.getJSONArray("content");
                        offerDetailObjectArrayList = new ArrayList<>();
                        for (int i = 0; i < tempContent.length(); i++){
                            JSONArray temp = tempContent.getJSONArray(i);
                            OfferDetailObject offerDetailObject = new OfferDetailObject();
                            offerDetailObject.setNo(i + 1);
                            offerDetailObject.setDescription(temp.getString(1));
                            offerDetailObject.setCount(temp.getString(2));
                            offerDetailObject.setUnit(temp.getString(3));
                            offerDetailObjectArrayList.add(offerDetailObject);
                        }
                        JSONArray tempDetail = responseData.getJSONArray("detail");
                        JSONArray offerDetailInfo = tempDetail.getJSONArray(0);
                        currentOfferCode = offerDetailInfo.getString(0);
                        currentOfferCategory = offerDetailInfo.getString(1);
                        currentOfferDescription = offerDetailInfo.getString(2);
                        currentOfferPrice = offerDetailInfo.getString(3);

                        connectionSuccess();

                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        if (connectCount == Global.SERVER_CONNECTION_COUNT){
                            showConnectFailedUI();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("network exception");
                }

            }
        }.execute(httpCallPost);
    }

    private void showConnectingUI(){
        showProgressDialog(getString(R.string.message_content_loading));
        layMainContent.setVisibility(View.GONE);
        layLoading.setVisibility(View.VISIBLE);
        layDisconnect.setVisibility(View.GONE);
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.GONE);
        layLoading.setVisibility(View.VISIBLE);
        layDisconnect.setVisibility(View.VISIBLE);
        txtError.setText(getResources().getString(R.string.service_connection_failed_shop) + " " + SHOP_NAME + " " + SHOP_BRANCH);
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);
        layDisconnect.setVisibility(View.GONE);
    }

    public void initConnectionTimer() {
        final int[] currentBEWSTimerTime = {0};
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
                    getRequestData(requestUUID);
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

    private void stopConnection(){
        stopConnectionTimer();
    }

    private void startConnection() {
        showConnectingUI();
        initConnectionTimer();
    }

    private void connectionSuccess(){
        stopConnectionTimer();
        showConnectSuccessUI();
        showOfferDetailInformation();
        showOfferDetailList();
    }
}
