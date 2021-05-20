package com.nsoft.laundromat.controller.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.controller.login.LoginActivity;
import com.nsoft.laundromat.controller.menu.ui.home.NoticeAdapter;
import com.nsoft.laundromat.controller.menu.ui.home.ShopAdapter;
import com.nsoft.laundromat.controller.menu.ui.home.ShopView;
import com.nsoft.laundromat.controller.model.NoticeObject;
import com.nsoft.laundromat.controller.notice.NoticeDetailActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;

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

import static android.content.Context.MODE_PRIVATE;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.MACHINE_ID;
import static com.nsoft.laundromat.common.Global.NOTICE_ACTED;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.NOTICE_HIDDEN;
import static com.nsoft.laundromat.common.Global.NOTICE_REQUEST;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentNoticeActionStatus;
import static com.nsoft.laundromat.common.Global.currentNoticeContent;
import static com.nsoft.laundromat.common.Global.currentNoticeNo;
import static com.nsoft.laundromat.common.Global.currentNoticeTitle;
import static com.nsoft.laundromat.common.Global.currentNoticeType;
import static com.nsoft.laundromat.common.Global.currentNoticeViewStatus;
import static com.nsoft.laundromat.common.Global.noticeObjectArrayList;
import static com.nsoft.laundromat.common.Global.pastActivityName;
import static com.nsoft.laundromat.common.Global.shopViewArrayList;
import static com.nsoft.laundromat.common.Global.str_cloud_http_server;
import static com.nsoft.laundromat.common.Global.str_local_http_server;
import static com.nsoft.laundromat.common.Global.userEmailKey;
import static com.nsoft.laundromat.common.Global.userId;
import static com.nsoft.laundromat.common.Global.userPasswordKey;
import static com.nsoft.laundromat.common.Global.userSdkKey;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BaseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BaseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BaseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView lstMyShop;
    private TextView txtShopName;
    private TextView txtShopBranch;
    public View currentRoot;
    private ArrayList<ShopView> tempShopViewArrayList;

    private ListView lstNotice;
    private ImageView imgNoticeType;
    private TextView txtNoticeTitle;
    private TextView txtNoticeIntroduction;
    private Button btnFirst;
    private Button btnSecond;

    private String strUUID;
    private boolean isItemButtonClicked = false;

    private int noticeCount = 0;
    private TextView txtNoticeCount;

    public BaseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BaseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseFragment newInstance(String param1, String param2) {
        BaseFragment fragment = new BaseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView textView = new TextView(getActivity());
        textView.setText(R.string.hello_blank_fragment);

        return textView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!pastActivityName.equals("")) {
            sendNoticeRequest(NOTICE_GET, EMPTY_STRING);
            pastActivityName = "";
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and description
        void onFragmentInteraction(Uri uri);
    }

    public void showProgressDialog(String message){
        CustomProgress.dismissDialog();
        if(!((Activity) getContext()).isFinishing())
            CustomProgress.show(getContext(), message, false, null, false);
    }

    public String getServerUrl() {
        String httpUrl;
        if (Global._isCloud)
            httpUrl = getContext().getResources().getString(R.string.str_cloud_http_server);
        else
            httpUrl = getContext().getResources().getString(R.string.str_local_http_server);
        return httpUrl;
    }

    public String getServerUrlDashboard() {
        String httpUrl;
        if (Global._isCloud)
            httpUrl = str_cloud_http_server;
        else
            httpUrl = str_local_http_server;
        return httpUrl;
    }

    public void showToast(String content){
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    public void showUserShopList(View root, ArrayList<ShopView> shopArrayList){
        currentRoot = root;
        txtShopName = root.findViewById(R.id.txt_shop_name);
        txtShopBranch = root.findViewById(R.id.txt_shop_branch);
        lstMyShop = root.findViewById(R.id.lst_my_shop);
        showBottomInformation();
        ArrayList<ShopView> shopItem = new ArrayList<>();
        if (shopArrayList != null){
            shopItem = shopArrayList;
            ShopAdapter shopAdapter = new ShopAdapter(getContext(), R.layout.item_shop, shopItem);
            lstMyShop.setAdapter(shopAdapter);
            lstMyShop.setOnItemClickListener(onShopItemListener);
        }
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

    ListView.OnItemClickListener onShopItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempShopViewArrayList = new ArrayList<>();
            if (!shopViewArrayList.get(position).selected){
                for (int i = 0; i < shopViewArrayList.size(); i++){
                    ShopView tempArray = shopViewArrayList.get(i);
                    if (i == position){
                        tempArray.selected = true;
                        SHOP_NAME = tempArray.name;
                        SHOP_BRANCH = tempArray.branch;
                        MACHINE_ID = tempArray.machineId;
                    }
                    else {
                        tempArray.selected = false;
                    }
                    tempShopViewArrayList.add(tempArray);
                }
                shopViewArrayList = new ArrayList<>();
                shopViewArrayList = tempShopViewArrayList;
                showUserShopList(currentRoot, tempShopViewArrayList);
            }
            else {
//                showToast("current item selected");
            }
        }
    };

    public void showNoticeList(){
        lstNotice = currentRoot.findViewById(R.id.lst_notice);
        lstNotice.setVisibility(View.VISIBLE);
        if (noticeObjectArrayList != null){
            NoticeAdapter noticeAdapter = new NoticeAdapter(getContext(), R.layout.item_notice, noticeObjectArrayList, noticeListener);
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
                            strKey = noticeObject.getNo() + "";
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
            Intent intent = new Intent(getContext(), NoticeDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

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
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
    private SharedPreferences pref;
    public void setInformationToSystem(String keyname, String keyinfo){

        pref = getContext().getSharedPreferences("info", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(keyname, keyinfo);
        editor.commit();
    }

    public String getInformationFromSystem(String keyname){
        SharedPreferences shared = getContext().getSharedPreferences("info",MODE_PRIVATE);
        String string_temp = shared.getString(keyname, "");
        return string_temp;
    }

    // --------------------
    // ---- Chart Data ----
    // --------------------

    public LineDataSet createSet(int colorValue, String title) {

        LineDataSet set = new LineDataSet(null, title);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(colorValue);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        // set the small circle on graph
        set.setDrawCircles(true);
        set.setCircleColor(colorValue);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(colorValue);
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }
    // >>>>
    // endregion

    // region ----- notice  -----
    public void sendNoticeRequest(int sqlNo, String searchKey){

        lstNotice = currentRoot.findViewById(R.id.lst_notice);
        lstNotice.setVisibility(View.INVISIBLE);
        if (sqlNo == NOTICE_GET){
            isItemButtonClicked = false;
        }
        else {
            isItemButtonClicked = true;
        }
        showProgressDialog(getContext().getResources().getString(R.string.loading_press));
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

        txtNoticeCount = getActivity().findViewById(R.id.txt_notice_count);
        if (txtNoticeCount != null){
            txtNoticeCount.setText(noticeCount + "");
        }
    }
    // endregion

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
            SimpleDateFormat spf= new SimpleDateFormat("EEEE, MMMM d, yyyy");
            String dateOne = spf.format(date);
            strDate = dateOne;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public String standardDateFormatForStaff(String inputDate){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = "";
        try {
            Date date = format.parse(inputDate);
            SimpleDateFormat spf= new SimpleDateFormat("EEEE, MMMM d, yyyy");
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
