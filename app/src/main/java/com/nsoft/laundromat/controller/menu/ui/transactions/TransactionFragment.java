package com.nsoft.laundromat.controller.menu.ui.transactions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.TransactionObject;
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

import static com.nsoft.laundromat.common.Global.DEFAULT_STRING;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REFRESH_TIME;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.TRANSACTIONS_GET_NEXT;
import static com.nsoft.laundromat.common.Global.TRANSACTIONS_GET_PREV;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.transactionObjectArrayList;

public class TransactionFragment extends BaseFragment {
    private TextView txtPageTitle;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private LinearLayout layNext;
    private LinearLayout layPrevious;

    private TransactionViewModel transactionViewModel;
    private ListView lstTransaction;
    private LinearLayout layEmptyResult;
    private int transactionId;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private ListView lstMyShop;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShiftDate;
    private TextView txtShift;
    private TextView txtOpenDate;
    private TextView txtOpenName;
    private TextView txtCloseDate;
    private TextView txtCloseName;
    private TextView txtGrossSale;
    private TextView txtCashReceived;
    private TextView txtCashCount;
    private TextView txtBankDeposit;
    private Toolbar toolbar;

    private String requestUUID = "";
    private String strShiftNo = "";
    private String strShiftDate = "";
    private String strShiftName = "";
    private String strOpenDate = "";
    private String strOpenName = "";
    private String strCloseDate = "";
    private String strCloseName = "";
    private String strGrossSale = "";
    private String strCashReceived = "";
    private String strCashCount = "";
    private String strBankDeposit = "";

    private String transactionShiftId = "";
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private PullToRefreshView mPullToRefreshView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        transactionViewModel =
                ViewModelProviders.of(this).get(TransactionViewModel.class);
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        currentRoot = root;
        final TextView textView = root.findViewById(R.id.text_gallery);
        transactionViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        invisibleTopIcons();
        layNextPrevious.setVisibility(View.VISIBLE);
        layNext = getActivity().findViewById(R.id.lay_next);
        layNext.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(TRANSACTIONS_GET_NEXT, transactionShiftId);
            }
        });
        layPrevious = getActivity().findViewById(R.id.lay_previous);
        layPrevious.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(TRANSACTIONS_GET_PREV, transactionShiftId);
            }
        });

        lstTransaction = root.findViewById(R.id.lst_transaction);
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

        layLoading = root.findViewById(R.id.lay_loading);
        layMainContent = root.findViewById(R.id.lay_main);
        layDisconnect = root.findViewById(R.id.lay_disconnect);
        txtError = root.findViewById(R.id.txt_error);
        btnTryAgain= root.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                startConnection();
            }
        });
        txtShiftDate = root.findViewById(R.id.txt_shift_date);
        txtShift = root.findViewById(R.id.txt_shift);
        txtOpenDate = root.findViewById(R.id.txt_open_date);
        txtOpenName = root.findViewById(R.id.open_name);
        txtCloseDate = root.findViewById(R.id.txt_close_date);
        txtCloseName = root.findViewById(R.id.close_name);
        txtGrossSale = root.findViewById(R.id.txt_gross_sale);
        txtCashReceived = root.findViewById(R.id.txt_cash_received);
        txtCashCount = root.findViewById(R.id.txt_cash_count);
        txtBankDeposit = root.findViewById(R.id.txt_bank_deposit);

        lstMyShop = root.findViewById(R.id.lst_my_shop);

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_transaction);
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
        mPullToRefreshView =root.findViewById(R.id.pull_to_refresh);
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

        showBottomInformation();
        sendRequestToServer(Global.TRANSACTIONS_GET, transactionShiftId);
        return root;
    }


    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_transaction));
        layNotice = getActivity().findViewById(R.id.lay_notice);
        layNextPrevious = getActivity().findViewById(R.id.lay_next_previous);
        layPremiumRegular = getActivity().findViewById(R.id.lay_premium_regular);
        layAvailableDisable = getActivity().findViewById(R.id.lay_available_disable);
        layNextPrevious.setVisibility(View.GONE);
        layPremiumRegular.setVisibility(View.GONE);
        layAvailableDisable.setVisibility(View.GONE);
        layNotice.setVisibility(View.GONE);
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    private void showTransactionList(){
        ArrayList<TransactionView> customerItem = new ArrayList<>();

        for (int i = 0; i < transactionObjectArrayList.size(); i++) {
            TransactionObject obj = transactionObjectArrayList.get(i);
            customerItem.add(new TransactionView(obj.getUserName(), obj.getPhotoUrl(), obj.getOperationId(),
                    obj.getAmount()));
        }

        TransactionAdapter transactionAdapter = new TransactionAdapter(getContext(),
                R.layout.item_transaction, customerItem);
        lstTransaction.setAdapter(transactionAdapter);
        lstTransaction.setOnItemClickListener(onItemListener);
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            transactionId = transactionObjectArrayList.get(position).getId();
//            Toast.makeText(getContext(), "position" + transactionId, Toast.LENGTH_SHORT).show();
        }
    };

    private void sendRequestToServer(int sqlNo, String searchKey){
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
                        CustomProgress.dismissDialog();
                        JSONArray responseData =  response.getJSONArray("data");
                        transactionObjectArrayList = new ArrayList<>();
                        JSONObject jsonData = responseData.getJSONObject(0);
                        String transactionShiftContent = jsonData.getString("transactionShift");
                        String transactionListContent = jsonData.getString("transactionList");
                        if (!transactionShiftContent.equals("[]")){
                            JSONArray transactionShift = new JSONArray(transactionShiftContent);
                            JSONArray tempOne = transactionShift.getJSONArray(0);
                            String [] openDateTime;
                            String [] closeDateTime;
                            openDateTime = tempOne.get(2).toString().split(" ");
                            closeDateTime = tempOne.get(4).toString().split(" ");
                            if (openDateTime.length > 1){
                                strShiftDate = openDateTime[0];
                                strOpenDate = openDateTime[1];
                            }
                            if (closeDateTime.length > 1){
                                strCloseDate = closeDateTime[1];
                            }
                            String tempShiftName = tempOne.get(1).toString();
                            if (tempShiftName.contains("FIRST")){
                                strShiftName = 1 + "";
                            }
                            else if (tempShiftName.contains("SECOND")){
                                strShiftName = 2 + "";
                            }
                            else if (tempShiftName.contains("THIRD")){
                                strShiftName = 3 + "";
                            }
                            strOpenName = tempOne.get(3).toString();
                            strCloseName = tempOne.get(5).toString();
                            strCashReceived = tempOne.get(6).toString();
                            strGrossSale = tempOne.get(7).toString();
                            strCashCount = tempOne.get(8).toString();
                            strBankDeposit = tempOne.get(9).toString();
                            transactionShiftId = tempOne.get(0).toString();
                        }
                        else {
                            strShiftDate = DEFAULT_STRING;
                            strOpenDate = DEFAULT_STRING;
                            strCloseDate = DEFAULT_STRING;
                            strShiftName = DEFAULT_STRING;
                            strOpenName = DEFAULT_STRING;
                            strCloseName = DEFAULT_STRING;
                            strCashReceived = DEFAULT_STRING;
                            strGrossSale = DEFAULT_STRING;
                            strCashCount = DEFAULT_STRING;
                            strBankDeposit = DEFAULT_STRING;
                        }
                        if (!transactionListContent.equals("[]")){
                            JSONArray transactionList = new JSONArray(transactionListContent);
                            for (int i = 0; i < transactionList.length(); i++){
                                TransactionObject tempObject = new TransactionObject();
                                JSONArray tempTwo = transactionList.getJSONArray(i);
                                tempObject.setOperationId(tempTwo.get(0).toString());
                                tempObject.setAmount(standardDecimalFormat(tempTwo.get(1).toString()));
                                tempObject.setUserName(tempTwo.get(2).toString());
                                transactionObjectArrayList.add(tempObject);
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
                    if (connectCount == Global.SERVER_CONNECTION_COUNT){
                        showConnectFailedUI();
                    }
                    e.printStackTrace();
                }

            }
        }.execute(httpCallPost);
    }

    private void showConnectingUI(){
        showProgressDialog(getString(R.string.message_content_loading));
        layMainContent.setVisibility(View.GONE);
        layLoading.setVisibility(View.VISIBLE);
        layDisconnect.setVisibility(View.GONE);
        layNextPrevious.setVisibility(View.INVISIBLE);
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.GONE);
        layLoading.setVisibility(View.VISIBLE);
        layDisconnect.setVisibility(View.VISIBLE);
        layNextPrevious.setVisibility(View.INVISIBLE);
        txtError.setText(getResources().getString(R.string.service_connection_failed_shop) + " " + SHOP_NAME + " " + SHOP_BRANCH);
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);
        layDisconnect.setVisibility(View.INVISIBLE);
        layNextPrevious.setVisibility(View.VISIBLE);
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
        showShiftContent();
        showTransactionList();
    }

    private void showShiftContent(){
        txtShift.setText(strShiftName);
        txtShiftDate.setText(standardDateFormatForStaff(strShiftDate));
        txtOpenDate.setText(standardTimeFormat(strOpenDate));
        txtOpenName.setText(strOpenName);
        txtCloseDate.setText(standardTimeFormat(strCloseDate));
        txtCloseName.setText(strCloseName);
        txtCashReceived.setText(standardDecimalFormat(strCashReceived));
        txtGrossSale.setText(standardDecimalFormat(strGrossSale));
        txtCashCount.setText(standardDecimalFormat(strCashCount));
        txtBankDeposit.setText(standardDecimalFormat(strBankDeposit));
    }
}