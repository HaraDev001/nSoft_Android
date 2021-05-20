package com.nsoft.laundromat.controller.menu.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.nsoft.laundromat.controller.shop.AddShopActivity;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.MACHINE_ID;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.RESULT_EMPTY_SHOP;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.SHOP_PAGE_DELAY_TIME;
import static com.nsoft.laundromat.common.Global.shopViewArrayList;
import static com.nsoft.laundromat.common.Global.strAddShopActivity;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;
import static com.nsoft.laundromat.common.Global.userEmail;
import static com.nsoft.laundromat.common.Global.userId;
import static com.nsoft.laundromat.common.Global.userLastShopId;

public class HomeFragment extends BaseFragment {
    private TextView txtPageTitle;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private HomeViewModel homeViewModel;
    private LinearLayout btnAddShop;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private ListView lstShop;
    private LinearLayout layEmptyResult;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;

    private ArrayList<ShopView> tempShopViewArrayList;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        currentRoot = root;
        invisibleTopIcons();

        btnAddShop = root.findViewById(R.id.btn_add_shop);
        btnAddShop.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(getContext(), AddShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);

            }
        });

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

        lstShop = root.findViewById(R.id.lst_shop);
        layEmptyResult = root.findViewById(R.id.empty_result);

        layLoading = root.findViewById(R.id.lay_loading);
        layMainContent = root.findViewById(R.id.lay_main);
        layDisconnect = root.findViewById(R.id.lay_disconnect);
        txtError = root.findViewById(R.id.txt_error);
        btnTryAgain= root.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                initDelayTime();
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

        showBottomInformation();
        updateShopLog();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (strAddShopActivity.equals("AddShopActivity")){
            strAddShopActivity = "";
//            getShopList();
            updateShopLog();
        }
        strNoticeDetailActivity = "";
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_shop));
        layNotice = getActivity().findViewById(R.id.lay_notice);
        layNextPrevious = getActivity().findViewById(R.id.lay_next_previous);
        layPremiumRegular = getActivity().findViewById(R.id.lay_premium_regular);
        layAvailableDisable = getActivity().findViewById(R.id.lay_available_disable);
        layNextPrevious.setVisibility(View.GONE);
        layPremiumRegular.setVisibility(View.GONE);
        layAvailableDisable.setVisibility(View.GONE);
        layNotice.setVisibility(View.GONE);
    }

    private void updateShopLog(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_update_shop_log);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", userId + "");
        paramsPost.put("email", userEmail);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {
                        initDelayTime();
                    }
                    else if (responseCode == Global.RESULT_EMPTY_SHOP) {
                        connectionEmpty();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("result failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(httpCallPost);
    }

    private void getShopList(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_shop_list);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", userId + "");
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
                        shopViewArrayList = new ArrayList<>();
                        for (int i = 0; i < responseData.length(); i++){
                            JSONObject tempData = responseData.getJSONObject(i);
                            String shopID = tempData.getString("id");
                            String shopName = tempData.getString("shop_name");
                            String branch = tempData.getString("branch");
                            String machineId = tempData.getString("machine_id");
                            String amount = tempData.getString("amount");
                            int onlineStatus = tempData.getInt("online_status");
                            boolean selected = false;
                            if (shopID.equals(userLastShopId + "")){
                                selected = true;
                                SHOP_NAME = shopName;
                                SHOP_BRANCH = branch;
                                MACHINE_ID = machineId;
                            }

                            if (responseData.length() == 1){
                                selected = true;
                                SHOP_NAME = shopName;
                                SHOP_BRANCH = branch;
                                MACHINE_ID = machineId;
                                userLastShopId = Integer.parseInt(shopID);
                                layEmptyResult.setVisibility(View.GONE);
                                lstShop.setVisibility(View.VISIBLE);
                                updateLastShop(shopID);
                            }

                            ShopView shopView = new ShopView(shopID, shopName, branch, machineId, amount, selected, onlineStatus);
                            shopViewArrayList.add(shopView);
                        }
                        showShopList(shopViewArrayList);
                    }
                    else if (responseCode == RESULT_EMPTY_SHOP){
                        connectionEmpty();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        showConnectFailedUI();
                    }
                } catch (JSONException e) {
                    showConnectFailedUI();
                    e.printStackTrace();
                }

            }
        }.execute(httpCallPost);
    }

    private void updateLastShop(String shopId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_update_last_shop);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("user_id", userId + "");
        paramsPost.put("shop_id", shopId);
        httpCallPost.setParams(paramsPost);
        new HttpRequest(){
            @Override
            public void onResponse(String str) {
                super.onResponse(str);
                try {
                    JSONObject response = new JSONObject(str);
                    int responseCode = (int) response.get("code");
                    if (responseCode == Global.RESULT_SUCCESS) {

                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("update last shop failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.execute(httpCallPost);
    }

    private void showShopList(ArrayList<ShopView> shopArrayList){
        showConnectSuccessUI();
        showBottomInformation();
        ArrayList<ShopView> shopItem = new ArrayList<>();
        shopItem = shopArrayList;
        ShopAdapter shopAdapter = new ShopAdapter(getContext(), R.layout.item_shop, shopItem);
        lstShop.setAdapter(shopAdapter);
        lstShop.setOnItemClickListener(onItemListener);
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempShopViewArrayList = new ArrayList<>();
            for (int i = 0; i < shopViewArrayList.size(); i++){
                ShopView tempArray = shopViewArrayList.get(i);
               if (i == position){
                   tempArray.selected = true;
                   SHOP_NAME = tempArray.name;
                   SHOP_BRANCH = tempArray.branch;
                   MACHINE_ID = tempArray.machineId;
                   String shopId = tempArray.shopId;
                   userLastShopId = Integer.parseInt(shopId);
                   updateLastShop(shopId);
               }
               else {
                   tempArray.selected = false;
               }
                tempShopViewArrayList.add(tempArray);
            }
            shopViewArrayList = new ArrayList<>();
            shopViewArrayList = tempShopViewArrayList;
            showShopList(tempShopViewArrayList);
        }
    };

    private void initDelayTime(){
        showConnectingUI();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getShopList();
            }
        }, SHOP_PAGE_DELAY_TIME);
    }

    private void connectionEmpty(){
        showConnectSuccessUI();
        layEmptyResult.setVisibility(View.VISIBLE);
        lstShop.setVisibility(View.GONE);
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);
        layDisconnect.setVisibility(View.INVISIBLE);
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

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

}