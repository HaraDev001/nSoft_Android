package com.nsoft.laundromat.controller.report.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_CUSTOMER_LIST;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_FINANCIAL_STATEMENT;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_INVENTORY;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_ITEM_SOLD_BREAKDOWN;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_LEAST_ITEMS;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_MONTHLY_REPORT;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_PAYINS_PAYOUT;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_PETTY_CASH;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_PRODUCT_ITEM_LIST;
import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_TOP_ITEMS;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REPORTS_ITEM_SOLD;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;

public class MoreFragment extends BaseFragment {
    private TextView txtPageTitle;
    private ImageView imgHome;
    private LinearLayout layEmail;
    private ImageView imgSendEmail;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private Button btnEmailMonthly;
    private Button btnEmailItemSold;
    private Button btnEmailPayIns;
    private Button btnEmailFinancial;
    private Button btnEmailCash;
    private Button btnEmailCustomer;
    private Button btnEmailProductItem;
    private Button btnEmailInventory;
    private Button btnEmailTopItem;
    private Button btnEmailLeastItem;

    private ImageView imgCalendar;
    private TextView txtCalendar;

    private String strUUID = "";
    private String selectedDate = "";
    private String selectedCategory = "";
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_more, container, false);
        currentRoot = root;
        imgHome = root.findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent i = new Intent(getActivity(), MenuActivity.class);
//                startActivity(i);
//                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });
        txtShopName = root.findViewById(R.id.txt_shop_name);
        txtShopBranch = root.findViewById(R.id.txt_shop_branch);

        layMainContent = root.findViewById(R.id.lay_main);
        layLoading = root.findViewById(R.id.lay_loading);
        layDisconnect = root.findViewById(R.id.lay_disconnect);
        txtError = root.findViewById(R.id.txt_error);
        btnTryAgain = root.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startConnection();
            }
        });

        btnEmailMonthly = root.findViewById(R.id.btn_monthly_email);
        btnEmailMonthly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_MONTHLY_REPORT, selectedCategory, selectedDate);
            }
        });

        btnEmailItemSold = root.findViewById(R.id.btn_item_sold_email);
        btnEmailItemSold.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_ITEM_SOLD_BREAKDOWN, selectedCategory, selectedDate);
            }
        });

        btnEmailPayIns = root.findViewById(R.id.btn_pay_in_email);
        btnEmailPayIns.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_PAYINS_PAYOUT, selectedCategory, selectedDate);
            }
        });

        btnEmailFinancial = root.findViewById(R.id.btn_finance_email);
        btnEmailFinancial.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_FINANCIAL_STATEMENT, selectedCategory, selectedDate);
            }
        });

        btnEmailCash = root.findViewById(R.id.btn_cash_email);
        btnEmailCash.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_PETTY_CASH, selectedCategory, selectedDate);
            }
        });

        btnEmailCustomer = root.findViewById(R.id.btn_customer_email);
        btnEmailCustomer.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_CUSTOMER_LIST, selectedCategory, selectedDate);
            }
        });

        btnEmailProductItem = root.findViewById(R.id.btn_product_email);
        btnEmailProductItem.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_PRODUCT_ITEM_LIST, selectedCategory, selectedDate);
            }
        });

        btnEmailInventory = root.findViewById(R.id.btn_inventory_email);
        btnEmailInventory.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_INVENTORY, selectedCategory, selectedDate);
            }
        });

        btnEmailTopItem = root.findViewById(R.id.btn_top_item_email);
        btnEmailTopItem.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_TOP_ITEMS, selectedCategory, selectedDate);
            }
        });

        btnEmailLeastItem = root.findViewById(R.id.btn_least_item_email);
        btnEmailLeastItem.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_LEAST_ITEMS, selectedCategory, selectedDate);
            }
        });

        imgCalendar = root.findViewById(R.id.img_calendar);
        imgCalendar.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                    @Override
                    public void onDateChoose(int year, int month, int day) {
                        Calendar c = Calendar.getInstance();
                        int currentYear = c.get(Calendar.YEAR);
                        int currentMonth = c.get(Calendar.MONTH) + 1;
                        int currentDay = c.get(Calendar.DAY_OF_MONTH);
//                        if(year > currentYear)
//                        {
//                            txtCalendar.setText("");
//                            String msgContent = getString(R.string.content_input_date_incorrect);
//                            showToast(msgContent);
//                        }
//                        else if (year == currentYear && month > currentMonth){
//                            txtCalendar.setText("");
//                            String msgContent = getString(R.string.content_input_date_incorrect);
//                            showToast(msgContent);
//                        }
//                        else if (year == currentYear && month == currentMonth && day > currentDay){
//                            txtCalendar.setText("");
//                            String msgContent = getString(R.string.content_input_date_incorrect);
//                            showToast(msgContent);
//                        }
//                        else {
                            String dtStart = year + "/" + month + "/" + day;
                            txtCalendar.setText(standardDateFormat(dtStart));
                            selectedDate = year + "-" + month + "-" + day;
//                        }
                    }
                });
                datePickerDialogFragment.show(getActivity().getFragmentManager(), "DatePickerDialogFragment");
            }
        });
        txtCalendar = root.findViewById(R.id.txt_calendar);

        selectedDate = EMPTY_STRING;
        selectedCategory = EMPTY_STRING;

        mSlidingUpPanelLayout = root.findViewById(R.id.fragment_more);
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
        getCurrentDate();
        invisibleTopIcons();
        showBottomInformation();

        return root;
    }

    private void getCurrentDate(){
        Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH) + 1;
        int currentDay = c.get(Calendar.DAY_OF_MONTH);
        String dtStart = currentYear + "/" + currentMonth + "/" + currentDay;
        txtCalendar.setText(standardDateFormat(dtStart));
        selectedDate = currentYear + "-" + currentMonth + "-" + currentDay;
    }

    private void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_report_more));
        layEmail = getActivity().findViewById(R.id.lay_email);
        imgSendEmail = getActivity().findViewById(R.id.img_send_email);
        layEmail.setVisibility(View.GONE);
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }
    private void sendRequestToServer(int sqlNo, String categoryName, String selectedDate){
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
                        strUUID = responseData.getString("uuid");
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
                    getEmailSendStatus(strUUID);
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
    }


}