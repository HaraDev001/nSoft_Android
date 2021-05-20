package com.nsoft.laundromat.controller.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.controller.login.LoginActivity;
import com.nsoft.laundromat.controller.menu.ui.home.NoticeAdapter;
import com.nsoft.laundromat.controller.model.NoticeObject;
import com.nsoft.laundromat.controller.notice.NoticeDetailActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.dialog.ECProgressDialog;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_ACTED;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.NOTICE_HIDDEN;
import static com.nsoft.laundromat.common.Global.NOTICE_REQUEST;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentActivityName;
import static com.nsoft.laundromat.common.Global.currentNoticeActionStatus;
import static com.nsoft.laundromat.common.Global.currentNoticeContent;
import static com.nsoft.laundromat.common.Global.currentNoticeNo;
import static com.nsoft.laundromat.common.Global.currentNoticeTitle;
import static com.nsoft.laundromat.common.Global.currentNoticeType;
import static com.nsoft.laundromat.common.Global.currentNoticeViewStatus;
import static com.nsoft.laundromat.common.Global.noticeObjectArrayList;
import static com.nsoft.laundromat.common.Global.pastActivityName;
import static com.nsoft.laundromat.common.Global.userEmailKey;
import static com.nsoft.laundromat.common.Global.userId;
import static com.nsoft.laundromat.common.Global.userPasswordKey;
import static com.nsoft.laundromat.common.Global.userSdkKey;


public abstract class BaseActivity extends AppCompatActivity {
    public AppCompatActivity thisActivity;
    public Context thisContext;
    public View thisView;


    private ListView lstNotice;
    private String strUUID;

    public ECProgressDialog mPostingdialog;

    private boolean isItemButtonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!pastActivityName.equals("") && !currentActivityName.equals("NoticeDetailActivity")) {
            sendNoticeRequest(NOTICE_GET, EMPTY_STRING);
            pastActivityName = "";
        }
        if (currentActivityName.equals("NoticeDetailActivity")){
            currentActivityName = "";
        }
    }

    public String getServerUrl() {
        String httpUrl;
        if (Global._isCloud)
            httpUrl = getString(R.string.str_cloud_http_server);
        else
            httpUrl = getString(R.string.str_local_http_server);
        return httpUrl;
    }

    public void showToast(String content){
        Toast.makeText(thisContext, content, Toast.LENGTH_SHORT).show();
    }

    public void showPostingDialog(String str) {
        mPostingdialog = new ECProgressDialog(thisContext, str);
        mPostingdialog.show();
    }

    public void showProgressDialog(String message){
        CustomProgress.dismissDialog();
        if(!((Activity) thisContext).isFinishing())
            CustomProgress.show(thisContext, message, false, null, false);
    }


    public String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    public void facebookLogout(){
        LoginManager.getInstance().logOut();
    }

    public void tryLogOut(){
        userId = -100;
        Global.userName = "";
        Global.userEmail = "";
        Global.userFacebookId = "";
        Global.userGoogleId = "";
        Global.userPaypalEmail = "";
        Global.userPassword = "";
        Global.userUniqueId = "";
        setInformationToSystem(userEmailKey, "");
        setInformationToSystem(userPasswordKey, "");
        setInformationToSystem(userSdkKey, "");
        showToast("you logged in another device");
        Intent intent = new Intent(thisContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        thisContext.startActivity(intent);
        finish();
    }

    // region ----- notice  -----
    public void sendNoticeRequest(int sqlNo, String searchKey){

        lstNotice = thisView.findViewById(R.id.lst_notice);
        lstNotice.setVisibility(View.INVISIBLE);
        if (sqlNo == NOTICE_GET){
            isItemButtonClicked = false;
        }
        else {
            isItemButtonClicked = true;
        }
        showProgressDialog(thisContext.getResources().getString(R.string.loading_press));
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
                    if (!isItemButtonClicked)
                        noticeObjectArrayList = new ArrayList<>();
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        JSONObject responseData = response.getJSONObject("data");
                        JSONArray tempArray = responseData.getJSONArray("result");
                        for (int i = 0; i < tempArray.length(); i++){
                            NoticeObject noticeObject = new NoticeObject();
                            JSONArray tempData = tempArray.getJSONArray(i);
                            noticeObject.setNo(tempData.getInt(0));
                            noticeObject.setDateTime(tempData.getString(1));
                            noticeObject.setType(tempData.getString(2));
                            noticeObject.setTitle(tempData.getString(3));
                            noticeObject.setContent(tempData.getString(4));
                            noticeObject.setViewStatus(tempData.getString(5));
                            noticeObject.setActionStatus(tempData.getString(6));
                            noticeObjectArrayList.add(noticeObject);
                        }
                        connectionSuccess();
                        if (isItemButtonClicked){
                            sendNoticeRequest(NOTICE_GET, EMPTY_STRING);
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

    private void startConnection() {
        initConnectionTimer();
    }

    private void stopConnection(){
        stopConnectionTimer();
    }

    private void initConnectionTimer() {
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

    private void stopConnectionTimer(){
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
        showNoticeList();
    }

    private void connectionSuccess(){
        stopConnectionTimer();
        showConnectSuccessUI();
    }

    public void showNoticeList(){
        lstNotice = thisView.findViewById(R.id.lst_notice);
        lstNotice.setVisibility(View.VISIBLE);
        if (noticeObjectArrayList != null){
            NoticeAdapter noticeAdapter = new NoticeAdapter(thisContext, R.layout.item_notice, noticeObjectArrayList, noticeListener);
            lstNotice.setAdapter(noticeAdapter);
            lstNotice.setOnItemClickListener(onNoticeItemListener);
        }
    }

    private NoticeAdapter.MyClickListener noticeListener = new NoticeAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            NoticeObject noticeObject = noticeObjectArrayList.get(position);
            String strKey = "";
            switch (v.getId()){
                case R.id.btn_first:
                    strKey = noticeObject.getNo() + "_" + 1;
                    sendNoticeRequest(NOTICE_ACTED, strKey);
                    break;
                case R.id.btn_second:
                    if (noticeObject.getType().equals(NOTICE_REQUEST)){
                        if (!noticeObject.getActionStatus().equals("True")){
                            strKey = noticeObject.getNo() + "_" + 0;
                            sendNoticeRequest(NOTICE_ACTED, strKey);
                        }
                        else {
                            strKey = noticeObject.getNo()  + "";
                            sendNoticeRequest(NOTICE_HIDDEN, strKey);
                        }
                    }
                    else {
                        strKey = noticeObject.getNo() + "";
                        sendNoticeRequest(NOTICE_HIDDEN, strKey);
                    }
                    break;
            }
        }
    };

    ListView.OnItemClickListener onNoticeItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NoticeObject noticeObject = noticeObjectArrayList.get(position);
            currentNoticeNo = noticeObject.getNo();
            currentNoticeType = noticeObject.getType();
            currentNoticeTitle = noticeObject.getTitle();
            currentNoticeContent = noticeObject.getContent();
            currentNoticeViewStatus = noticeObject.getViewStatus();
            currentNoticeActionStatus = noticeObject.getActionStatus();
            Intent intent = new Intent(thisContext, NoticeDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            thisActivity.startActivity(intent);
        }
    };
    // endregion

    private SharedPreferences pref;
    public void setInformationToSystem(String keyname, String keyinfo){

        pref = getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(keyname, keyinfo);
        editor.commit();
    }

    public String getInformationFromSystem(String keyname){
        SharedPreferences shared = getSharedPreferences("info",MODE_PRIVATE);
        String string_temp = shared.getString(keyname, "");
        return string_temp;
    }

    public String standardDecimalFormat(String inputAmount){
        String strAmount = "";
        if (!inputAmount.equals("")){
            DecimalFormat df = new DecimalFormat("#,###.00");
            Double amount = Double.parseDouble(inputAmount);
            strAmount = df.format(amount);
        }
        if (strAmount.equals(".00")){
            strAmount = "0.00";
        }
        return strAmount;
    }

    public String standardDateFormat(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardDateFormatThreeMonth(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardDateFormatThreeMonthForStaff(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("MMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardTimeFormat(String inputTime){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String strTime = "";
        try {
            Date date = format.parse(inputTime);
            SimpleDateFormat spf= new SimpleDateFormat("hh:mm a");
            String dateOne = spf.format(date);
            strTime = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }

    public String standardTimeFormatForStaff(String inputTime){
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss a");
        String strTime = "";
        try {
            Date date = format.parse(inputTime);
            SimpleDateFormat spf= new SimpleDateFormat("hh:mm a");
            String dateOne = spf.format(date);
            strTime = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strTime;
    }


}
