package com.nsoft.laundromat.controller.staff;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.EMAIL_STAFF_PROFILE;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.STAFFS_NEXT_DETAIL;
import static com.nsoft.laundromat.common.Global.STAFFS_PREV_DETAIL;
import static com.nsoft.laundromat.common.Global.STAFFS_SELECTED_DETAIL;
import static com.nsoft.laundromat.common.Global._isTest;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentStaffEmail;
import static com.nsoft.laundromat.common.Global.currentStaffName;
import static com.nsoft.laundromat.common.Global.currentStaffPosition;
import static com.nsoft.laundromat.common.Global.currentStaffRole;
import static com.nsoft.laundromat.common.Global.staffObjectArrayList;
import static com.nsoft.laundromat.common.Global.staffTransactionViewArrayList;

public class StaffProfileActivity extends BaseActivity {

    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ImageView imgTopRight;
    private ListView lstTransaction;
    private ListView lstInventory;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;

    private TextView txtStaffName;
    private TextView txtEmail;
    private TextView txtRole;
    private TextView txtName;
    private LinearLayout layNext;
    private LinearLayout layPrevious;
    private TextView txtInventoryTitle;

    private String requestUUID = "";
    private String selectedDate = "";
    private String selectedCategory = "";
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private boolean isSendEmail = false;
    private String minDate = "";
    private String maxDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_profile);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_staff_profile);

        initBasicUI();

        showBottomInformation();

        selectedDate = EMPTY_STRING;
        selectedCategory = EMPTY_STRING;

        getCurrentDateTime();
        if (_isTest)
            selectedDate = "2019-10-14";
        minDate = EMPTY_STRING;
        maxDate = EMPTY_STRING;

        sendRequestToServer(STAFFS_SELECTED_DETAIL);

    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
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
        txtTopTitle.setText(getString(R.string.title_staff_profile));
        imgTopRight = findViewById(R.id.img_top_right);
        imgTopRight.setImageDrawable(getResources().getDrawable(R.drawable.icon_send_email));
        imgTopRight.setVisibility(View.VISIBLE);
        imgTopRight.setClickable(true);
        imgTopRight.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServerForEmail(EMAIL_STAFF_PROFILE, currentStaffName, selectedDate);
                isSendEmail = true;
            }
        });
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(StaffProfileActivity.this, MenuActivity.class);
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
        lstTransaction = findViewById(R.id.lst_staff_transaction);
        lstInventory = findViewById(R.id.lst_staff_inventory);
        txtStaffName = findViewById(R.id.txt_staff_name);
        txtEmail = findViewById(R.id.txt_email);
        txtRole = findViewById(R.id.txt_role);
        txtName = findViewById(R.id.txt_name);
        txtInventoryTitle = findViewById(R.id.txt_inventory_title);
        layPrevious = findViewById(R.id.lay_previous_staff);
        layPrevious.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                sendRequestToServer(STAFFS_PREV_DETAIL);
                selectedDate = getPreviousDate(selectedDate);
                sendRequestToServer(STAFFS_SELECTED_DETAIL);
            }
        });
        layNext = findViewById(R.id.lay_next_staff);
        layNext.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                sendRequestToServer(STAFFS_NEXT_DETAIL);
                selectedDate = getNextDate(selectedDate);
                sendRequestToServer(STAFFS_SELECTED_DETAIL);
            }
        });
        mSlidingUpPanelLayout = findViewById(R.id.activity_staff_profile);
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

    private void getCurrentDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        selectedDate = formattedDate;
    }

    private String getPreviousDate(String inputDate){
        String strPrev = "";
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, -1);
            strPrev = format.format(c.getTime());

            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
            strPrev ="";
        }
        return strPrev;
    }

    private String getNextDate(String inputDate){
        String strNext = "";
        SimpleDateFormat  format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(inputDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            strNext = format.format(c.getTime());

            System.out.println(date);
        } catch (Exception e) {
            e.printStackTrace();
            strNext ="";
        }
        return strNext;
    }

    private void sendRequestToServer(final int sqlNo){
        isSendEmail = false;
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
        if (sqlNo == STAFFS_PREV_DETAIL){
            paramsPost.put("search_key", getPreviousUniqueId(currentStaffPosition) + "_" + selectedDate);
        }
        else if (sqlNo == STAFFS_NEXT_DETAIL){
            paramsPost.put("search_key", getNextUniqueId(currentStaffPosition)  + "_" + selectedDate);
        }
        else {
            paramsPost.put("search_key", currentStaffName + "_" + selectedDate);
        }
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
                        if (sqlNo == STAFFS_PREV_DETAIL && currentStaffPosition > 0){
                            currentStaffPosition -= 1;
                        }
                        else if (sqlNo == STAFFS_NEXT_DETAIL && currentStaffPosition < staffObjectArrayList.size()){
                            currentStaffPosition += 1;
                        }
                        startConnection();
                    }
                    else if (responseCode == Global.RESULT_INCORRECT_UUID){
                        CustomProgress.dismissDialog();
                        tryLogOut();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("Getting uuid failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }

    private void sendRequestToServerForEmail(int sqlNo, String categoryName, String selectedDate){
        isSendEmail = true;
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
        paramsPost.put("search_key", categoryName + "_" + selectedDate);
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
                        showToast("Getting uuid failed");
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
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_staff_profile_data);
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
                        staffTransactionViewArrayList = new ArrayList<>();
                        JSONObject responseData =  response.getJSONObject("data");
//                        JSONArray tempProfile = responseData.getJSONArray("profile");
                        JSONArray tempAttendance = responseData.getJSONArray("profile");
//                        if (tempProfile != null && tempProfile.length() != 0){
//                            JSONArray tempArray = tempProfile.getJSONArray(0);
//                            currentStaffId = tempArray.getString(0);
//                            currentStaffName = tempArray.getString(1) + " " + tempArray.getString(2);
//                            currentStaffEmail = tempArray.getString(4);
//                            currentStaffRole = tempArray.getString(5);
//                        }
                        minDate = EMPTY_STRING;
                        maxDate = EMPTY_STRING;
                        if (tempAttendance != null && tempAttendance.length() > 0){
                            for (int i = 0; i < tempAttendance.length(); i++){
                                JSONArray tempArray = tempAttendance.getJSONArray(i);
//                                String dateTime = tempArray.getString(0);
//                                String status = tempArray.getString(1);
//                                String strDay = "";
//                                String strDate = "";
//                                String strTime = "";
//                                String strInOut = "";
//                                strInOut = status;
//                                String [] arrayOpenDateTime = dateTime.split(" ");
//                                if (arrayOpenDateTime.length == 2){
//                                    strTime = arrayOpenDateTime[1];
//                                    String dtStart = arrayOpenDateTime[0];
//                                    SimpleDateFormat format = new SimpleDateFormat("yyyy/ff6806MM/dd");
//                                    try {
//                                        Date date = format.parse(dtStart);
//                                        SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
//                                        String dayDate = spf.format(date);
//                                        if (dayDate.split(",").length == 2){
//                                            strDay = dayDate.split(",")[0];
//                                            strDate = dayDate.split(",")[1];
//                                        }
//                                    } catch (ParseException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                StaffTransactionView staffTransactionView = new StaffTransactionView(strDate, strDay, strTime, strInOut);
                                String tempShiftName = tempArray.getString(5);
                                int shiftNo = 1;
                                if (tempShiftName.contains("1")){
                                    shiftNo = 1;
                                }
                                else if (tempShiftName.contains("2")){
                                    shiftNo = 2;
                                }
                                else if (tempShiftName.contains("3")){
                                    shiftNo = 3;
                                }
                                String tempDate =  tempArray.getString(6);
                                String tempTimeIn =  tempArray.getString(7);
                                String tempTimeOut =  tempArray.getString(8);
                                String strDate = "";
                                String strTimeIn = "";
                                String strTimeOut = "";
                                if (!tempDate.equals("") && tempDate != null && tempDate.split(" ").length ==3){
                                    strDate = standardDateFormatThreeMonthForStaff(tempDate.split(" ")[0]);
                                    if (i == 0){
                                        minDate = strDate;
                                    }
                                    if (i == tempAttendance.length() - 1){
                                        maxDate = strDate;
                                    }
                                }
                                if (!tempTimeIn.equals("") && tempTimeIn != null && tempTimeIn.split(" ").length ==3){
                                    strTimeIn = standardTimeFormatForStaff(tempTimeIn.split(" ")[1] + " " + tempTimeIn.split(" ")[2]);
                                }
                                if (!tempTimeOut.equals("") && tempTimeOut != null && tempTimeOut.split(" ").length ==3){
                                    strTimeOut = standardTimeFormatForStaff(tempTimeOut.split(" ")[1] + " " + tempTimeOut.split(" ")[2]);
                                }
                                StaffTransactionView staffTransactionView = new StaffTransactionView(strDate, shiftNo +"", strTimeIn, strTimeOut);
                                staffTransactionViewArrayList.add(staffTransactionView);
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
                    showToast("Network exception");
                }

            }
        }.execute(httpCallPost);
    }

    private void getEmailSendStatus(String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_dashboard_data);
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
                        isSendEmail = false;
                        JSONArray responseData =  response.getJSONArray("data");
                        JSONObject tempResult = responseData.getJSONObject(0);
                        String sendStatus = tempResult.getString("result");
                        connectionSuccess();
                        if (sendStatus.contains("Success")){
                            showToast("Email sending success");
                        }
                        else {
                            showToast("Email sending failed");
                        }
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

    private String getPreviousUniqueId(int position){
        String strUniqueId = "";
        if (position > 0){
            strUniqueId = staffObjectArrayList.get(position - 1).getName();
        }
        return strUniqueId;
    }

    private String getNextUniqueId(int position){
        String strUniqueId = "";
        if (position < staffObjectArrayList.size()){
            strUniqueId = staffObjectArrayList.get(position + 1).getName();
        }
        return strUniqueId;
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
        showStaffInformation();
        showStaffTransactionList();
    }

    private void showStaffInformation(){
        txtStaffName.setText(currentStaffName);
        txtEmail.setText(currentStaffEmail);
        txtRole.setText(currentStaffRole);
        txtName.setText(currentStaffName);
    }
    private void showStaffTransactionList(){
        StaffTransactionAdapter staffTransactionAdapter = new StaffTransactionAdapter(thisContext,
                R.layout.item_staff_profile, staffTransactionViewArrayList);
        lstInventory.setAdapter(staffTransactionAdapter);

        txtInventoryTitle.setText(minDate + " - " + maxDate);;
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
                    if (isSendEmail)
                        getEmailSendStatus(requestUUID);
                    else
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
}
