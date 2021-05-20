package com.nsoft.laundromat.controller.menu.ui.dashboard;

import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.menu.ui.offer.OfferFragment;
import com.nsoft.laundromat.controller.model.DashInventoryObject;
import com.nsoft.laundromat.controller.model.DashMachineObject;
import com.nsoft.laundromat.controller.model.DashUserObject;
import com.nsoft.laundromat.controller.model.NoticeObject;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.DASHBOARD_GET;
import static com.nsoft.laundromat.common.Global.DASHBOARD_GET_CATEGORY;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REFRESH_TIME;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.dashInventoryObjectArrayList;
import static com.nsoft.laundromat.common.Global.noticeObjectArrayList;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;
import static com.nsoft.laundromat.common.Global.strStaffLogIn;
import static com.nsoft.laundromat.common.Global.strStaffLogOut;
import static com.nsoft.laundromat.common.Global.strStaffNone;

public class DashboardFragment extends BaseFragment {
    private TextView txtPageTitle;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;
    private LinearLayout layNotice;
    private TextView txtNoticeCount;
    private ImageView imgNoticeBackground;

    private DashboardViewModel dashboardViewModel;
    private DashboardUserViewAdapter adapter;
    private DashboardMachineViewAdapter machineViewAdapter;
    private DashboardCategoryViewAdapter categoryViewAdapter;
    private RecyclerView rvUserList;
    private RecyclerView rvMachineList;
    private RecyclerView rvInventoryList;
    private ListView lstInventory;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private TextView txtShift;
    private TextView txtName;
    private TextView txtOpenDate;
    private TextView txtAmount;
    private TextView txtOpenTime;
    private TextView txtMachineCount;
    private Button btnWasher;
    private Button btnDryer;
    private ImageView imgPrevious;
    private ImageView imgNext;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private LinearLayout layNextUser;
    private LinearLayout layPrevioutUser;
    private Button btnTryAgain;
    private ArrayList<DashboardCategoryView> categoryList;

    private String strCurrentShift = "";
    private String strCurrentDate = "";
    private String strOpenedDate = "";
    private String strShiftOwner = "";
    private String strShiftAmount = "";
    private static int userCount = 5;
    private static int lastNo;
    private static int currentStartNo = 0;
    private Button currentSelectedButton;
    private String dashUUID = "";

    private int currentSelectedTagNo = 0;
    private String currentCategoryName = "";

    public static ArrayList<DashMachineObject> machineList;
    public static ArrayList<DashUserObject> userList;

    private boolean isGettingNotice = true;
    private boolean isFirst = true;
    private int noticeCount = 0;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private PullToRefreshView mPullToRefreshView;
    private SwipeRefreshLayout swipeLayout;

    private static Timer connectionTimer;
    private static int connectCount;
    private int machineKind = 1; // 1: washer, 2: dryer

    private static Timer machineStatusTimer;
    private static int MACHINE_STATUS_PERIOD = 60000;

    private TextView txtUserName;
    private Handler timerHandler;
    private boolean isCategoryClicked = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        currentRoot = root;
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        invisibleTopIcons();

        //<<<<mars_add_20200104
//        TextView customView = (TextView)
//                LayoutInflater.from(getContext()).inflate(R.layout.app_bar_main,
//                        null);
//        ActionBar.LayoutParams params = new
//                ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
//                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
//
//        customView.setText("Some centered text");
//        getContext().getSupportActionBar().setCustomView(customView, params);
        //>>>>

        txtShift= root.findViewById(R.id.txt_shift);
        txtOpenDate = root.findViewById(R.id.txt_open_date);
        txtOpenTime = root.findViewById(R.id.txt_open_time);
        txtName = root.findViewById(R.id.txt_name);
        txtAmount = root.findViewById(R.id.txt_amount);
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

        rvUserList = root.findViewById(R.id.rv_user_list);
        rvMachineList = root.findViewById(R.id.rv_machine_list);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvUserList.setLayoutManager(horizontalLayoutManager);
        rvUserList.setAdapter(adapter);

        LinearLayoutManager horizontalLayoutManager1
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvMachineList.setLayoutManager(horizontalLayoutManager1);
        rvMachineList.setAdapter(machineViewAdapter);
        rvInventoryList = root.findViewById(R.id.rv_inventory_list);
        LinearLayoutManager horizontalLayoutManager2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvInventoryList.setLayoutManager(horizontalLayoutManager2);

        lstInventory = root.findViewById(R.id.lst_inventory);

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

        btnDryer = root.findViewById(R.id.btn_dryer);
        btnDryer.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                machineKind = 2;
                showMachineList();
            }
        });
        btnWasher = root.findViewById(R.id.btn_washer);
        btnWasher.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                machineKind = 1;
                showMachineList();
            }
        });
        txtMachineCount = root.findViewById(R.id.txt_machine_count);

        layNextUser = root.findViewById(R.id.lay_next_user);
        layNextUser.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (currentStartNo  < Global.dashUserObjectArrayList.size()){
                    showUserList(currentStartNo + 1);
                }
            }
        });
        layPrevioutUser = root.findViewById(R.id.lay_previous_user);
        layPrevioutUser.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (currentStartNo > 0){
                    showUserList(currentStartNo - 1);
                }
            }
        });
        mSlidingUpPanelLayout = root.findViewById(R.id.fragment_home);
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

//        swipeLayout = root.findViewById(R.id.swipe_container);
//         Adding Listener
//        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Your code here
////                Toast.makeText(getApplicationContext(), "Works!", Toast.LENGTH_LONG).show();
//                // To keep animation for 4 seconds
//                new Handler().postDelayed(new Runnable() {
//                    @Override public void run() {
//                        // Stop animation (This will be after 3 seconds)
//                        swipeLayout.setRefreshing(false);
//                    }
//                }, 4000); // Delay in millis
//            }
//        });
//
//        // Scheme colors for animation
//        swipeLayout.setColorSchemeColors(
//                getResources().getColor(android.R.color.holo_blue_bright),
//                getResources().getColor(android.R.color.holo_green_light),
//                getResources().getColor(android.R.color.holo_orange_light),
//                getResources().getColor(android.R.color.holo_red_light)
//        );

//        mPullToRefreshView =root.findViewById(R.id.pull_to_refresh);
//        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                mPullToRefreshView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPullToRefreshView.setRefreshing(false);
//                    }
//                }, REFRESH_TIME);
//            }
//        });

        isCategoryClicked = false;
        showBottomInformation();
        sendRequestToServer(DASHBOARD_GET, EMPTY_STRING);

//        showNoticeList(root, noticeObjectArrayList);
        return root;
    }

    @Override
    public void onResume() {
        if (strNoticeDetailActivity.equals("NoticeDetailActivity")){
            strNoticeDetailActivity = "";
            sendRequestToServer(NOTICE_GET, EMPTY_STRING);
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timerHandler != null)
            timerHandler.removeCallbacks(updater);
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_dashboard));
        layNotice = getActivity().findViewById(R.id.lay_notice);
        txtNoticeCount = getActivity().findViewById(R.id.txt_notice_count);
        layNotice.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });
//        imgNoticeBackground = getActivity().findViewById(R.id.img_notice_background);
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

    private void sendRequestToServer(int sqlNo, String searchKey){
//        stopMachineStatusTimer();
        if (timerHandler != null && updater != null){
            timerHandler.removeCallbacks(updater);
        }
        if (sqlNo == NOTICE_GET){
            isGettingNotice = true;
        }
        else {
            isGettingNotice = false;
        }
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
                        dashUUID = responseData.getString("uuid");
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

    private void getDashboardData(String uniqueId){
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
                        Global.dashMachineObjectArrayList = new ArrayList<>();
                        Global.dashDryerObjectArrayList = new ArrayList<>();
                        Global.dashWasherObjectArrayList = new ArrayList<>();
                        Global.dashUserObjectArrayList = new ArrayList<>();
                        dashInventoryObjectArrayList = new ArrayList<>();
                        categoryList = new ArrayList<>();
                        CustomProgress.dismissDialog();
                        JSONArray responseData =  response.getJSONArray("data");
                        JSONObject jsonData = responseData.getJSONObject(0);
                        String dashOneContent = jsonData.getString("dashboard1");
                        String dashTwoContent = jsonData.getString("dashboard2");
                        String dashThreeContent = jsonData.getString("dashboard3");
                        String dashFourContent = jsonData.getString("dashboard4");
                        String dashCategoryContent = jsonData.getString("category");
                        JSONArray dashone = new JSONArray(dashOneContent);
                        JSONArray temp = dashone.getJSONArray(0);
                        strCurrentShift = temp.get(0).toString();
                        strOpenedDate = temp.get(1).toString();
                        strShiftOwner = temp.get(2).toString();
                        strShiftAmount = temp.get(3).toString();
                        strCurrentDate = temp.get(0).toString();

                        JSONArray dashTwo = new JSONArray(dashTwoContent);
                        for (int i = 0; i < dashTwo.length(); i++){
                            DashMachineObject tempObject = new DashMachineObject();
                            JSONArray temp2 = dashTwo.getJSONArray(i);
                            tempObject.setId(temp2.get(0).toString());
                            tempObject.setName(temp2.get(1).toString());
                            tempObject.setStatus(temp2.get(2).toString());
                            tempObject.setKind(temp2.get(3).toString());
                            tempObject.setDuration(temp2.get(4).toString());
                            tempObject.setRegisterTime(temp2.get(5).toString());
                            String machineName = temp2.get(1).toString();
                            String tempNo = machineName.split(" ")[1];
                            int machineNo = Integer.parseInt(tempNo);
                            tempObject.setMachineNo(machineNo + "");

                            Global.dashMachineObjectArrayList.add(tempObject);
                            if (temp2.get(3).toString().equals("DRYER")){
                                Global.dashDryerObjectArrayList.add(tempObject);
                            }
                            else if (temp2.get(3).toString().equals("WASHER")){
                                Global.dashWasherObjectArrayList.add(tempObject);
                            }
                        }

                        JSONArray dashThree = new JSONArray(dashThreeContent);
                        for (int i = 0; i < dashThree.length(); i++){
                            DashInventoryObject tempObject = new DashInventoryObject();
                            JSONArray temp3 = dashThree.getJSONArray(i);
                            tempObject.setName(temp3.get(0).toString());
                            tempObject.setUnit(temp3.get(1).toString());
                            tempObject.setFirst(temp3.get(2).toString());
                            tempObject.setSecond(temp3.get(3).toString());
                            tempObject.setThird(temp3.get(4).toString());
                            int tempStorage = 0;
                            int storage = temp3.getInt(2);
                            int ussage = temp3.getInt(3);
                            tempStorage = storage - ussage;
                            if (temp3.getInt(4) == 0|| temp3.getString(4) == null){
                                tempObject.setCriticalStatus(false);
                            }
                            else {
                                if (temp3.getInt(4) > (storage - ussage)){
                                    tempObject.setCriticalStatus(true);
                                }
                                else
                                    tempObject.setCriticalStatus(false);
                            }
                            tempObject.setStorage(tempStorage);

                            dashInventoryObjectArrayList.add(tempObject);
                        }

                        JSONArray dashFour = new JSONArray(dashFourContent);
                        for (int i = 0; i < dashFour.length(); i++){
                            DashUserObject tempUser = new DashUserObject();
                            JSONArray temp4 = dashFour.getJSONArray(i);
                            tempUser.setName(temp4.get(0).toString());
                            tempUser.setRole(temp4.get(1).toString());
                            tempUser.setTimeIn(temp4.get(3).toString());
                            tempUser.setTimeOut(temp4.get(4).toString());
                            Global.dashUserObjectArrayList.add(tempUser);
                        }

                        JSONArray dashCategory = new JSONArray(dashCategoryContent);
//                        String firstName = "ALL";
//                        int firstNo = 0;
//                        DashboardCategoryView dashboardCategoryView = new DashboardCategoryView(firstName, firstNo, false);
//                        categoryList.add(dashboardCategoryView);
                        for (int i = 0; i < dashCategory.length(); i++){
                            String name = dashCategory.getJSONArray(i).get(0).toString();
                            DashboardCategoryView temp3 = new DashboardCategoryView(name, i, false);
                            categoryList.add(temp3);
                        }

                        connectionSuccess();
                        if (isFirst){
                            isFirst = false;
                            sendRequestToServer(NOTICE_GET, EMPTY_STRING);
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

    private void getDashboardCategoryData(String uniqueId){
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
                        dashInventoryObjectArrayList = new ArrayList<>();
                        CustomProgress.dismissDialog();
                        JSONArray responseData =  response.getJSONArray("data");
                        JSONObject jsonData = responseData.getJSONObject(0);
                        String dashThreeContent = jsonData.getString("dashboard3");

                        JSONArray dashThree = new JSONArray(dashThreeContent);
                        for (int i = 0; i < dashThree.length(); i++){
                            DashInventoryObject tempObject = new DashInventoryObject();
                            JSONArray temp3 = dashThree.getJSONArray(i);
                            tempObject.setName(temp3.get(0).toString());
                            tempObject.setUnit(temp3.get(1).toString());
                            tempObject.setFirst(temp3.get(2).toString());
                            tempObject.setSecond(temp3.get(3).toString());
                            tempObject.setThird(temp3.get(4).toString());
                            int tempStorage = 0;
                            int storage = temp3.getInt(2);
                            int ussage = temp3.getInt(3);
                            tempStorage = storage - ussage;
                            if (temp3.getInt(4) == 0|| temp3.getString(4) == null){
                                tempObject.setCriticalStatus(false);
                            }
                            else {
                                if (temp3.getInt(4) > (storage - ussage)){
                                    tempObject.setCriticalStatus(true);
                                }
                                else
                                    tempObject.setCriticalStatus(false);
                            }
                            tempObject.setStorage(tempStorage);

                            dashInventoryObjectArrayList.add(tempObject);
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
                    if (connectCount == Global.SERVER_CONNECTION_COUNT){
                        showConnectFailedUI();
                    }
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
                    noticeObjectArrayList = new ArrayList<>();
                    noticeCount = 0;
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
                            if (!tempData.getString(5).equals("True")){
                                noticeCount++;
                            }
                            noticeObjectArrayList.add(noticeObject);
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
                    if (connectCount == Global.SERVER_CONNECTION_COUNT){
                        showConnectFailedUI();
                    }
                }

            }
        }.execute(httpCallPost);
    }

    private void showDashboardOne(){
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyy/MM/dd", d.getTime());
        String [] arrayOpenDateTime = strOpenedDate.split(" ");
        if (arrayOpenDateTime.length == 3){
            txtOpenTime.setText(arrayOpenDateTime[1] + " " + arrayOpenDateTime[2]);
            String dtStart = arrayOpenDateTime[0];
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(dtStart);
                SimpleDateFormat spf= new SimpleDateFormat("EEEE, MMMM d, yyyy");
                String dateOne = spf.format(date);
                txtOpenDate.setText(dateOne);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if (arrayOpenDateTime.length == 2){
            txtOpenTime.setText(arrayOpenDateTime[1]);
            String dtStart = arrayOpenDateTime[0];
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(dtStart);
                SimpleDateFormat spf= new SimpleDateFormat("EEEE, MMMM d, yyyy");
                String dateOne = spf.format(date);
                txtOpenDate.setText(dateOne);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        txtShift.setText(strCurrentShift);
        txtName.setText(strShiftOwner);
        DecimalFormat df = new DecimalFormat("#,###.00");
        Double amount = Double.parseDouble(strShiftAmount);
        txtAmount.setText(df.format(amount));
    }

    private void showMachineList(){
        ArrayList<String> userMachines = new ArrayList<>();
        ArrayList<String> machineStatus = new ArrayList<>();
        int usedCount = 0;
        machineList = new ArrayList<>();

        if (machineKind == 1){
            machineList = Global.dashWasherObjectArrayList;
            btnWasher.setSelected(true);
            btnDryer.setSelected(false);
        }
        else if (machineKind == 2){
            machineList = Global.dashDryerObjectArrayList;
            btnDryer.setSelected(true);
            btnWasher.setSelected(false);
        }
        for (int i = 0; i < machineList.size(); i++){
            DashMachineObject tempObject = new DashMachineObject();
            tempObject = machineList.get(i);
            userMachines.add(tempObject.getMachineNo());
            if (tempObject.getStatus().equals("ON-USE")){
                long milliseconds = 0;
                long currentTimeMillis = System.currentTimeMillis();
                int remainTime = 0;
                remainTime = Integer.parseInt(tempObject.getDuration());
//                String startTime = tempObject.getRegisterTime().split("\\.")[0];
                String startTime = tempObject.getRegisterTime();
                SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
                try {
                    Date d = f.parse(startTime);
                    milliseconds = d.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int diffMin = (int) ((currentTimeMillis - milliseconds)/ 60000);
                if (diffMin < remainTime){
                    machineStatus.add(remainTime - diffMin + " min");
                    usedCount += 1;
                }
                else {
                    machineStatus.add("AVAILABLE");
                }
            }
            else{
                machineStatus.add(tempObject.getStatus());
            }
        }
        txtMachineCount.setText(usedCount + "/" + machineList.size());
        machineViewAdapter = new DashboardMachineViewAdapter(getContext(),userMachines, machineStatus);
        rvMachineList.setAdapter(machineViewAdapter);
    }

    private void showUserList(int startNo){
        userList = new ArrayList<>();
        currentStartNo = startNo;
        ArrayList<String> userNames = new ArrayList<>();
        ArrayList<String> userStatus = new ArrayList<>();
        userList = Global.dashUserObjectArrayList;
        for (int i = 0; i <  userList.size(); i++){
            DashUserObject temp = new DashUserObject();
            int j = startNo + i;
            if (j >= userList.size()){
                j = 0;
            }
            temp = userList.get(j);
            userNames.add(temp.getName());
            String status = strStaffNone;
            if (temp.getTimeIn().equals("") && temp.getTimeOut().equals("")){
                status = strStaffNone;
            }
            else if (!temp.getTimeIn().equals("") && temp.getTimeOut().equals("")){
                status = strStaffLogIn;
            }
            else {
                status = strStaffLogOut;
            }
            userStatus.add(status);
        }
        adapter = new DashboardUserViewAdapter(getContext(),userStatus, userNames);
        rvUserList.setAdapter(adapter);
    }

    private void showInventoryList(){

        DashboardInventoryAdapter inventoryAdapter = new DashboardInventoryAdapter(getContext(),
                R.layout.item_inventory, dashInventoryObjectArrayList);
        lstInventory.setAdapter(inventoryAdapter);
    }

    private void showInventoryCategoryList(){
        categoryViewAdapter = new DashboardCategoryViewAdapter(getContext(), categoryList, categoryListener);
        rvInventoryList.setAdapter(categoryViewAdapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentSelectedButton = rvInventoryList.findViewWithTag("category" + currentSelectedTagNo);
                currentSelectedButton.setSelected(true);
            }

        }, 500);
    }

    private OfferFragment.ItemClickListener categoryListener = new OfferFragment.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ArrayList<DashboardCategoryView> tempArrayList = new ArrayList<>();
            if (position != currentSelectedTagNo){

                String selectedCategoryName = categoryList.get(position).categoryName;
                if (selectedCategoryName.equals("ALL")){
                    selectedCategoryName = EMPTY_STRING;
                }
                currentCategoryName = selectedCategoryName;
                if (currentSelectedButton != null)
                    currentSelectedButton.setSelected(false);
                Button selectedButton = rvInventoryList.findViewWithTag("category" + position);
                selectedButton.setSelected(true);
                currentSelectedTagNo = position;
                currentSelectedButton = selectedButton;
                isCategoryClicked = true;
                sendRequestToServer(DASHBOARD_GET_CATEGORY, selectedCategoryName);
            }
        }
    };

    private void showConnectingUI(){
        showProgressDialog(getString(R.string.message_content_loading));
        if (isFirst){
            layMainContent.setVisibility(View.GONE);
            layLoading.setVisibility(View.VISIBLE);
            layDisconnect.setVisibility(View.GONE);
        }
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
        if (!isFirst){
            layMainContent.setVisibility(View.VISIBLE);
            layLoading.setVisibility(View.GONE);
            layDisconnect.setVisibility(View.INVISIBLE);
            showDashboardOne();
            showMachineList();
            updateMachineStatus();
            showInventoryList();
            showUserList(0);
            if (currentSelectedTagNo == 0) {
                showInventoryCategoryList();
            }
            if (isGettingNotice){

                layNotice.setVisibility(View.VISIBLE);
                if (noticeCount > 0){
                    txtNoticeCount.setText(noticeCount + "");
//                    imgNoticeBackground.setVisibility(View.VISIBLE);
                    txtNoticeCount.setVisibility(View.VISIBLE);
                }
                else {
                    txtNoticeCount.setVisibility(View.GONE);
//                    imgNoticeBackground.setVisibility(View.GONE);
                }
            }
        }
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
                    if (isGettingNotice){
                        getNoticeData(dashUUID);
                    }
                    else {
//                        if (_isTest)
//                            dashUUID = "7d263e6815c5de8c62d4748d26fcde99";
                        if (!isCategoryClicked)
                            getDashboardData(dashUUID);
                        else
                            getDashboardCategoryData(dashUUID);
                    }
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

//    private void initMachineStatusTimer() {
//        stopMachineStatusTimer();
//        machineStatusTimer = new Timer();
//        machineStatusTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                updateMachineStatus();
//            }
//
//        }, MACHINE_STATUS_PERIOD, MACHINE_STATUS_PERIOD);
//
//    }
//
//    private void stopMachineStatusTimer(){
//        if(machineStatusTimer != null){
//            machineStatusTimer.cancel();
//            machineStatusTimer = null;
//        }
//    }

    Runnable updater;
    private void updateMachineStatus() {
        timerHandler = new Handler();

        updater = new Runnable() {
            @Override
            public void run() {
                showMachineList();
                timerHandler.postDelayed(updater,MACHINE_STATUS_PERIOD);
            }
        };
        timerHandler.post(updater);
    }

}