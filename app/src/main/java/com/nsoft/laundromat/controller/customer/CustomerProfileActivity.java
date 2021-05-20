package com.nsoft.laundromat.controller.customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.CUSTOMERS_NEXT_DETAIL;
import static com.nsoft.laundromat.common.Global.CUSTOMERS_PREV_DETAIL;
import static com.nsoft.laundromat.common.Global.CUSTOMERS_SELECTED_DETAIL;
import static com.nsoft.laundromat.common.Global.CUSTOMER_REGULAR;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REFRESH_TIME;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentCustomerAddress;
import static com.nsoft.laundromat.common.Global.currentCustomerBalance;
import static com.nsoft.laundromat.common.Global.currentCustomerEmail;
import static com.nsoft.laundromat.common.Global.currentCustomerId;
import static com.nsoft.laundromat.common.Global.currentCustomerMobile;
import static com.nsoft.laundromat.common.Global.currentCustomerName;
import static com.nsoft.laundromat.common.Global.currentCustomerType;
import static com.nsoft.laundromat.common.Global.customerTransactionViewArrayList;

public class CustomerProfileActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;

    private TextView txtShopName;
    private TextView txtShopBranch;
    private ListView lstTransaction;
    private ImageView imgHome;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtName;
    private TextView txtCustomerName;
    private TextView txtAddress;
    private TextView txtMobileNumber;
    private ImageView imgPhone;
    private ImageView imgMessage;
    private TextView txtEmail;
    private TextView txtBalance;
    private ImageView imgPremiumRegular;
    private LinearLayout layNext;
    private LinearLayout layPrevious;

    private String requestUUID = "";

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private PullToRefreshView mPullToRefreshView;

    private static final int CALL_PERMISSION = 600;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_customer_profile);

        initBasicUI();
        showBottomInformation();
        sendRequestToServer(CUSTOMERS_SELECTED_DETAIL);
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
        txtTopTitle.setText(R.string.title_customer_profile);
        lstTransaction = findViewById(R.id.lst_customer_transaction);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(CustomerProfileActivity.this, MenuActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                thisActivity.startActivity(intent);
//                finish();
            }
        });

        txtShopName = findViewById(R.id.txt_shop_name);
        txtShopBranch = findViewById(R.id.txt_shop_branch);

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
        txtName = findViewById(R.id.txt_name);
        txtCustomerName = findViewById(R.id.txt_customer_name);
        txtAddress = findViewById(R.id.txt_address);
        txtMobileNumber = findViewById(R.id.txt_mobile);
        imgPhone = findViewById(R.id.img_phone);
        imgPhone.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (ConfirmCallPermission())
                    callCustomer();
            }
        });
        imgMessage = findViewById(R.id.img_message);
        imgMessage.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (ConfirmCallPermission())
                    messageCustomer();
            }
        });
        txtEmail= findViewById(R.id.txt_email);
        txtBalance = findViewById(R.id.txt_balance);
        imgPremiumRegular = findViewById(R.id.img_premium_regular);
        layNext = findViewById(R.id.lay_next_customer);
        layNext.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(CUSTOMERS_NEXT_DETAIL);
            }
        });
        layPrevious = findViewById(R.id.lay_previous_customer);
        layPrevious.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(CUSTOMERS_PREV_DETAIL);
            }
        });
        mSlidingUpPanelLayout = findViewById(R.id.activity_customer_profile);
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

        mPullToRefreshView =findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                    }
                }, REFRESH_TIME);
            }
        });
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void callCustomer(){
        if (!currentCustomerMobile.equals("") && currentCustomerMobile != null){
            String phone = currentCustomerMobile;
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        }
    }

    private void messageCustomer(){

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        if (!currentCustomerMobile.equals("") && currentCustomerMobile != null){
            smsIntent.putExtra("address", currentCustomerMobile);
        }
        startActivity(smsIntent);
    }

    private void showCustomerInfomation(){
        txtName.setText(currentCustomerName);
        txtCustomerName.setText(currentCustomerName);
        txtAddress.setText(currentCustomerAddress);
        txtMobileNumber.setText(currentCustomerMobile);
        if (currentCustomerMobile.equals("")){
            imgPhone.setVisibility(View.GONE);
            imgMessage.setVisibility(View.GONE);
        }
        else {
            imgPhone.setVisibility(View.VISIBLE);
            imgMessage.setVisibility(View.VISIBLE);
        }
        txtEmail.setText(currentCustomerEmail);
        txtBalance.setText(currentCustomerBalance);
        if (currentCustomerType == CUSTOMER_REGULAR){
            imgPremiumRegular.setImageDrawable(getResources().getDrawable(R.drawable.regular_icon));
        }
        else {
            imgPremiumRegular.setImageDrawable(getResources().getDrawable(R.drawable.premium_icon));
        }
    }

    private void showCustomerTransactionList(){

        CustomerTransactionAdapter customerTransactionAdapter = new CustomerTransactionAdapter(thisContext,
                R.layout.item_customer_transaction, customerTransactionViewArrayList);
        lstTransaction.setAdapter(customerTransactionAdapter);
//        lstCustomer.setOnItemClickListener(onItemListner);
    }

    private void sendRequestToServer(int sqlNo){
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
        paramsPost.put("search_key", currentCustomerId);
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
                        requestUUID = responseData.getString("uuid");
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

    private void getRequestData(String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_customer_profile_data);
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
                        CustomProgress.dismissDialog();
                        customerTransactionViewArrayList = new ArrayList<>();
                        JSONObject responseData =  response.getJSONObject("data");
                        JSONArray tempDetail = responseData.getJSONArray("detail");
                        JSONArray tempTransaction = responseData.getJSONArray("transaction");
                        if (tempDetail != null){
                            JSONArray tempArray = tempDetail.getJSONArray(0);
                            currentCustomerId = tempArray.getString(0);
                            currentCustomerName = tempArray.getString(1) + " " + tempArray.getString(2);
                            currentCustomerAddress = tempArray.getString(4);
                            currentCustomerMobile = tempArray.getString(5);
                            currentCustomerEmail = tempArray.getString(6);
                            currentCustomerBalance = tempArray.getString(7);
                        }
                        if (tempTransaction != null){
                            for (int i = 0; i < tempTransaction.length(); i++){
                                JSONArray tempArray = tempTransaction.getJSONArray(i);
                                String id = tempArray.getString(1);
                                String dateTime = tempArray.getString(0);
                                String amount = standardDecimalFormat(tempArray.getString(2));
                                boolean cancelStatus = false;
                                if (!tempArray.getString(3).equals("") && tempArray.getString(3) != null){
                                    cancelStatus = true;
                                }
                                CustomerTransactionView customerTransactionView = new CustomerTransactionView(i, id, dateTime, amount, cancelStatus);
                                customerTransactionViewArrayList.add(customerTransactionView);
                            }
                        }
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
        layDisconnect.setVisibility(View.INVISIBLE);
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
        showCustomerInfomation();
        showCustomerTransactionList();
    }


    private boolean ConfirmCallPermission() {

        if( (ContextCompat.checkSelfPermission(thisContext, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) || ContextCompat.checkSelfPermission(thisContext, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS}, CALL_PERMISSION);
            return false;
        } else {
//            startLocaion();//开始定位
//            Toast.makeText(thisContext, "opened call contact and call permission", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case CALL_PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //用户同意权限
                }else{
                    //用户拒绝权限
                    Toast.makeText(thisContext, "not opened contact and call permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


}
