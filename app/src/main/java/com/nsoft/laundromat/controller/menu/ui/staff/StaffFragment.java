package com.nsoft.laundromat.controller.menu.ui.staff;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.StaffObject;
import com.nsoft.laundromat.controller.staff.StaffProfileActivity;
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

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.RESULT_SEARCH_EMPTY;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.STAFFS_GET;
import static com.nsoft.laundromat.common.Global._isTest;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentStaffName;
import static com.nsoft.laundromat.common.Global.currentStaffPosition;
import static com.nsoft.laundromat.common.Global.currentStaffRole;
import static com.nsoft.laundromat.common.Global.staffObjectArrayList;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;

public class StaffFragment extends BaseFragment {
    private TextView txtPageTitle;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private ToolsViewModel toolsViewModel;
    private ListView lstStaff;
    private LinearLayout layEmptyResult;
    private int staffId;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private ImageView imgSearch;
    private EditText edtSearch;

    private String requestUUID = "";
    private String strSearchValue = "";
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private PullToRefreshView mPullToRefreshView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_staff, container, false);
        currentRoot = root;
        invisibleTopIcons();

        lstStaff = root.findViewById(R.id.lst_stuff);
        layEmptyResult = root.findViewById(R.id.empty_result);
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
        edtSearch = root.findViewById(R.id.edt_staff_search);
        imgSearch = root.findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                strSearchValue = edtSearch.getText().toString();
                getRequestData(requestUUID);
            }
        });

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_staff);
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

        showBottomInformation();
        sendRequestToServer(STAFFS_GET);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        strNoticeDetailActivity = "";
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_staff));
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

    private void showStaffList(){

        ArrayList<StaffView> staffItem = new ArrayList<>();

        for (int i = 0; i < staffObjectArrayList.size(); i++) {
            StaffObject obj = staffObjectArrayList.get(i);
            staffItem.add(new StaffView(obj.getName(), obj.getRole() + "", obj.getShiftNo(), obj.getTimeIn(),
                    obj.getTimeOut()));
        }

        StaffAdapter staffAdapter = new StaffAdapter(getContext(),
                R.layout.item_staff, staffItem, mListener);
        lstStaff.setAdapter(staffAdapter);
        lstStaff.setOnItemClickListener(onItemListener);
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            currentStaffId = staffObjectArrayList.get(position).getId() + "";
            currentStaffName = staffObjectArrayList.get(position).getName();
            currentStaffRole = staffObjectArrayList.get(position).getRole();
            currentStaffPosition = position;
            Intent intent = new Intent(getContext(), StaffProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

    private StaffAdapter.MyClickListener mListener = new StaffAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            switch (v.getId()){
                case R.id.img_phone:
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    startActivity(intent);
                    break;
                case R.id.img_message:
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
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
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_staff_data);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("unique_id", uniqueId);
        paramsPost.put("search_value", strSearchValue);
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
                        staffObjectArrayList = new ArrayList<>();
                        for (int i = 0; i < responseData.length(); i++){
                            JSONArray tempData = responseData.getJSONArray(i);
                            StaffObject staffObject = new StaffObject();
//                            staffObject.setId(tempData.get(0).toString());
                            staffObject.setName(tempData.get(0).toString());
                            staffObject.setRole(tempData.get(1).toString());
                            String tempShiftNo = tempData.get(2).toString();
                            String shiftNo = "";
                            if (tempShiftNo != null && !tempShiftNo.equals(""))
                                shiftNo = tempShiftNo.substring(0, 1);
                            staffObject.setShiftNo(shiftNo);
                            String tempTimeIn = tempData.get(3).toString();
                            String strTimeIn = "";
                            String tempTimeOut = tempData.get(4).toString();
                            String strTimeOut = "";
                            if (!tempTimeIn.equals("") && tempTimeIn != null && tempTimeIn.split(" ").length ==3){
                                strTimeIn = standardTimeFormatForStaff(tempTimeIn.split(" ")[1] + " " + tempTimeIn.split(" ")[2]);
                            }
                            if (!tempTimeOut.equals("") && tempTimeOut != null && tempTimeOut.split(" ").length ==3){
                                strTimeOut = standardTimeFormatForStaff(tempTimeOut.split(" ")[1] + " " + tempTimeOut.split(" ")[2]);
                            }
                            staffObject.setTimeIn(strTimeIn);
                            staffObject.setTimeOut(strTimeOut);
                            staffObjectArrayList.add(staffObject);
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
                    e.printStackTrace();
                    if (connectCount == Global.SERVER_CONNECTION_COUNT){
                        showConnectFailedUI();
                    }
//                    showToast("network exception");
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

    private void initConnectionNoticeTimer() {
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
//                    if (_isTest){
//                        requestUUID = "130ca7e6459d3d1f2e42e33b578c55f3";
//                    }
                    getRequestData(requestUUID);
                }
            }
        }, 1000, 1000);
    }

    private void stopNoticeConnectionTimer(){
        if(connectionTimer != null){
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }

    private void stopConnection(){
        stopNoticeConnectionTimer();
    }

    private void startConnection() {
        showConnectingUI();
        initConnectionNoticeTimer();
    }

    private void connectionSuccess(){
        stopNoticeConnectionTimer();
        showConnectSuccessUI();
        layEmptyResult.setVisibility(View.GONE);
        lstStaff.setVisibility(View.VISIBLE);
        showStaffList();
    }

    private void connectionEmpty(){
        stopNoticeConnectionTimer();
        showConnectSuccessUI();
        layEmptyResult.setVisibility(View.VISIBLE);
        lstStaff.setVisibility(View.GONE);
    }
}