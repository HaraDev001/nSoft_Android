package com.nsoft.laundromat.controller.menu.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.api.services.people.v1.model.Interest;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.customer.CustomerProfileActivity;
import com.nsoft.laundromat.controller.model.CustomerObject;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.CUSTOMERS_GET_SEARCH;
import static com.nsoft.laundromat.common.Global.CUSTOMERS_GET_SEARCH_ALL;
import static com.nsoft.laundromat.common.Global.CUSTOMERS_GET_TOP20;
import static com.nsoft.laundromat.common.Global.CUSTOMER_PREMIUM;
import static com.nsoft.laundromat.common.Global.CUSTOMER_REGULAR;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.RESULT_SEARCH_EMPTY;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentCustomerId;
import static com.nsoft.laundromat.common.Global.customerObjectArrayList;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;

public class CustomerFragment extends BaseFragment implements AdapterView.OnItemClickListener{
    private TextView txtPageTitle;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private CustomerViewModel customerViewModel;
    private ListView lstCustomer;
    private LinearLayout layEmptyResult;
    private int customerId;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private EditText edtSearch;
    private ImageView imgSearch;
    private LinearLayout laySearch;
    private ToggleButton tooglePremiumRegular;

    private String requestUUID = "";
    private String strSearchValue = "";
    private boolean isToggleClicked = false;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        customerViewModel =
                ViewModelProviders.of(this).get(CustomerViewModel.class);

        View root = inflater.inflate(R.layout.fragment_customer, container, false);
        currentRoot = root;
        final TextView textView = root.findViewById(R.id.text_slideshow);

        invisibleTopIcons();
        tooglePremiumRegular = getActivity().findViewById(R.id.togglePremiumRegular);
        tooglePremiumRegular.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                isToggleClicked = !isToggleClicked;
                if (isToggleClicked){
                    getRequestData(requestUUID, CUSTOMER_REGULAR);
                }
                else {
                    getRequestData(requestUUID, CUSTOMER_PREMIUM);
                }
            }
        });

        lstCustomer = root.findViewById(R.id.lst_customer);
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

        layEmptyResult = root.findViewById(R.id.empty_result);
        customerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        edtSearch = root.findViewById(R.id.edt_search);
        imgSearch = root.findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                strSearchValue = edtSearch.getText().toString();
                if (isToggleClicked){
                    getRequestData(requestUUID, CUSTOMER_REGULAR);
                }
                else {
                    getRequestData(requestUUID, CUSTOMER_PREMIUM);
                }
            }
        });

        laySearch = root.findViewById(R.id.lay_search);
        laySearch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                strSearchValue = edtSearch.getText().toString();
                if (strSearchValue.equals(""))
                    sendRequestToServer(CUSTOMERS_GET_SEARCH_ALL);
                else
                    sendRequestToServer(CUSTOMERS_GET_SEARCH);
//                if (isToggleClicked){
//                    getRequestData(requestUUID, CUSTOMER_REGULAR);
//                }
//                else {
//                    getRequestData(requestUUID, CUSTOMER_PREMIUM);
//                }
            }
        });
        mSlidingUpPanelLayout = root.findViewById(R.id.fra_customer);
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

        isToggleClicked = false;
        tooglePremiumRegular.setChecked(false);

        showBottomInformation();
        sendRequestToServer(CUSTOMERS_GET_TOP20);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        strNoticeDetailActivity = "";
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_customer));
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

    public void showCustomerList(){
        ArrayList<CustomerView> customerItem = new ArrayList<>();

        for (int i = 0; i < customerObjectArrayList.size(); i++) {
            CustomerObject obj = customerObjectArrayList.get(i);
            customerItem.add(new CustomerView(obj.getFirstName() + " " + obj.getLastName(), obj.getAmount(), obj.getId() + "",
                    obj.getCustomerTime(), obj.getDayNumber()));
        }

        CustomerAdapter customerAdapter = new CustomerAdapter(getContext(),
                R.layout.item_customer, customerItem, mListener);
        lstCustomer.setAdapter(customerAdapter);
        lstCustomer.setOnItemClickListener(this);
    }

    ListView.OnItemClickListener onItemListner = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            customerId = customerObjectArrayList.get(position).getId();
            Toast.makeText(getContext(), "position" + customerId, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), CustomerProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

    private CustomerAdapter.MyClickListener mListener = new CustomerAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            switch (v.getId()){
                case R.id.img_phone:
//                    String myData = "content://contacts/people/";
//                    Intent myActivity2 = new Intent(Intent.ACTION_VIEW, Uri.parse( myData) );
//                    startActivity(myActivity2);
//                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                    startActivity(intent);

                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    startActivity(intent);
                    break;
                case R.id.img_message:

//                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//                    sendIntent.setData(Uri.parse("sms:"));
////                    sendIntent.putExtra("sms_body", x);
//                    startActivity(sendIntent);

                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
//                    smsIntent.putExtra("address", "12125551212");
//                    smsIntent.putExtra("sms_body","Body of Message");
                    startActivity(smsIntent);
                    break;
            }
        }
    };

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
        if (sqlNo == CUSTOMERS_GET_SEARCH){
            paramsPost.put("search_key", strSearchValue);
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

    private void getRequestData(String uniqueId, int customerType){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_customer_data);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("unique_id", uniqueId);
        paramsPost.put("search_value", "");
        paramsPost.put("customer_type", customerType + "");
        Global.currentCustomerType = customerType;
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
                        customerObjectArrayList = new ArrayList<>();
                        for (int i = 0; i < responseData.length(); i++){
                            JSONArray tempData = responseData.getJSONArray(i);
                            CustomerObject customerObject = new CustomerObject();
                            customerObject.setId(Integer.parseInt(tempData.get(0).toString()));
                            customerObject.setLastName(tempData.get(1).toString());
                            customerObject.setFirstName(tempData.get(2).toString());
                            customerObject.setPhoneNumber(tempData.get(3).toString());
                            customerObject.setCustomerTime(tempData.get(4).toString());
                            int dayNumber = 0;
                            if (!tempData.get(4).toString().equals("")){
                                dayNumber = Integer.parseInt(tempData.get(4).toString());
                            }
                            customerObject.setDayNumber(dayNumber);
                            customerObject.setAmount(standardDecimalFormat(tempData.get(5).toString()));
                            customerObjectArrayList.add(customerObject);
                        }
                        connectionSuccess();
                    }
                    else if (responseCode == RESULT_SEARCH_EMPTY){

                        CustomProgress.dismissDialog();
                        connectionEmpty();
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
                    CustomProgress.dismissDialog();
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
        layPremiumRegular.setVisibility(View.GONE);
    }

    private void showConnectFailedUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.GONE);
        layLoading.setVisibility(View.VISIBLE);
        layDisconnect.setVisibility(View.VISIBLE);
        layPremiumRegular.setVisibility(View.GONE);
        txtError.setText(getResources().getString(R.string.service_connection_failed_shop) + " " + SHOP_NAME + " " + SHOP_BRANCH);
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);
        layDisconnect.setVisibility(View.INVISIBLE);
        layPremiumRegular.setVisibility(View.VISIBLE);
    }

    public void initConnectionTimer() {
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
                    if (isToggleClicked)
                        getRequestData(requestUUID, CUSTOMER_REGULAR);
                    else
                        getRequestData(requestUUID, CUSTOMER_PREMIUM);
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
        layEmptyResult.setVisibility(View.GONE);
        lstCustomer.setVisibility(View.VISIBLE);
        showCustomerList();

    }

    private void connectionEmpty(){
        stopConnectionTimer();
        showConnectSuccessUI();
        layEmptyResult.setVisibility(View.VISIBLE);
        lstCustomer.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        currentCustomerId = customerObjectArrayList.get(position).getId() + "";
        Intent intent = new Intent(getContext(), CustomerProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }
}