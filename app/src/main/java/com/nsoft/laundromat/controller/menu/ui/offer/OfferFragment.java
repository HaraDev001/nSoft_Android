package com.nsoft.laundromat.controller.menu.ui.offer;

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
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.item.ItemReplenishActivity;
import com.nsoft.laundromat.controller.offer.OfferDetailActivity;
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

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.OFFERS_GET;
import static com.nsoft.laundromat.common.Global.OFFER_AVAILABLE;
import static com.nsoft.laundromat.common.Global.OFFER_DISABLE;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.currentOfferCategory;
import static com.nsoft.laundromat.common.Global.currentOfferCode;
import static com.nsoft.laundromat.common.Global.currentOfferDescription;
import static com.nsoft.laundromat.common.Global.currentOfferPrice;
import static com.nsoft.laundromat.common.Global.offerViewArrayList;
import static com.nsoft.laundromat.common.Global.strNoticeDetailActivity;

public class OfferFragment extends BaseFragment {
    private TextView txtPageTitle;
    private View offerRoot;
    private LinearLayout layNotice;
    private LinearLayout layNextPrevious;
    private LinearLayout layPremiumRegular;
    private LinearLayout layAvailableDisable;

    private OfferViewModel offerViewModel;
    private LinearLayout layItemReplenish;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private RecyclerView rvCategoryList;
    private OfferCategoryViewAdapter categoryViewAdapter;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private ListView lstOffer;
    private ToggleButton tgAvailableDisable;
    private ArrayList<OfferCategoryView> categoryList;
    private ArrayList<OfferView> availableOfferList;
    private ArrayList<OfferView> disableOfferList;

    private String requestUUID = "";
    private String currentCategoryName = "";
    private int currentSelectedTagNo = 0;
    private Button currentSelectedButton;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        offerViewModel =
                ViewModelProviders.of(this).get(OfferViewModel.class);
        View root = inflater.inflate(R.layout.fragment_offer, container, false);
        currentRoot = root;
        final TextView textView = root.findViewById(R.id.text_share);

        invisibleTopIcons();

        layItemReplenish = root.findViewById(R.id.lay_item_replenish);
        layItemReplenish.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                Intent intent = new Intent(getContext(), ItemReplenishActivity.class);
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

        offerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

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
        lstOffer = root.findViewById(R.id.lst_offer_item);
        tgAvailableDisable = getActivity().findViewById(R.id.toggleAvailableDisable);
        tgAvailableDisable.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (tgAvailableDisable.isChecked()){
                    getOffersWithCategory(requestUUID, currentCategoryName);
                }
                else {
                    getOffersWithCategory(requestUUID, currentCategoryName);
                }
            }
        });

        disableOfferList = new ArrayList<>();
        availableOfferList = new ArrayList<>();
        categoryList = new ArrayList<>();
        rvCategoryList = root.findViewById(R.id.rv_category_list);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvCategoryList.setLayoutManager(horizontalLayoutManager);

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_offer);
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
        sendRequestToServer(OFFERS_GET, "");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        strNoticeDetailActivity = "";
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_offer));
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
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_offer_data);
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
                        JSONObject responseData =  response.getJSONObject("data");
                        JSONArray categoryArray = responseData.getJSONArray("category");
                        JSONArray availableArray = responseData.getJSONArray("available");
                        JSONArray disableArray = responseData.getJSONArray("disable");
                        categoryList = new ArrayList<>();
                        offerViewArrayList = new ArrayList<>();
                        availableOfferList = new ArrayList<>();
                        disableOfferList = new ArrayList<>();
                        if (categoryArray != null){
                            String firstName = "ALL";
                            int firstNo = 0;
                            OfferCategoryView offerCategoryView = new OfferCategoryView(firstName, firstNo, false);
                            categoryList.add(offerCategoryView);
                            for (int i = 0; i < categoryArray.length(); i++){
                                String name = categoryArray.get(i).toString().split("\"")[1];
                                OfferCategoryView temp = new OfferCategoryView(name, i+ 1, false);
                                categoryList.add(temp);
                            }
                        }
                        for (int j = 0; j<availableArray.length(); j++){
                            JSONArray tempArray = availableArray.getJSONArray(j);
                            String code = tempArray.getString(0);
                            String category = tempArray.getString(1);
                            String kind = tempArray.getString(2);
                            String description = tempArray.getString(3);
                            String price = standardDecimalFormat(tempArray.getString(4));
                            String cost = tempArray.getString(5);
                            String type = tempArray.getString(6);
                            OfferView offerView = new OfferView(code, category, kind, description, price, cost, type);
                            availableOfferList.add(offerView);
                        }
                        for (int j = 0; j<disableArray.length(); j++){
                            JSONArray tempArray = disableArray.getJSONArray(j);
                            String code = tempArray.getString(0);
                            String category = tempArray.getString(1);
                            String kind = tempArray.getString(2);
                            String description = tempArray.getString(3);
                            String price = standardDecimalFormat(tempArray.getString(4));
                            String cost = tempArray.getString(5);
                            String type = tempArray.getString(6);
                            OfferView offerView = new OfferView(code, category, kind, description, price, cost, type);
                            disableOfferList.add(offerView);
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

    private void getOffersWithCategory(String uniqueId, String category){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_offer_category);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("unique_id", uniqueId);
        paramsPost.put("category", category);
        if (tgAvailableDisable.isChecked()){
            paramsPost.put("type", OFFER_DISABLE + "");
        }
        else {
            paramsPost.put("type", OFFER_AVAILABLE + "");
        }
        paramsPost.put("search_key", "");
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
                        if (tgAvailableDisable.isChecked()){
                            disableOfferList = new ArrayList<>();
                            for (int j = 0; j<responseData.length(); j++){
                                JSONArray tempArray = responseData.getJSONArray(j);
                                String code = tempArray.getString(0);
                                String category = tempArray.getString(1);
                                String kind = tempArray.getString(2);
                                String description = tempArray.getString(3);
                                String price =standardDecimalFormat(tempArray.getString(4));
                                String cost = tempArray.getString(5);
                                String type = tempArray.getString(6);
                                OfferView offerView = new OfferView(code, category, kind, description, price, cost, type);
                                disableOfferList.add(offerView);
                            }
                            showOffersList(disableOfferList);
                        }
                        else {
                            availableOfferList = new ArrayList<>();
                            for (int j = 0; j<responseData.length(); j++){
                                JSONArray tempArray = responseData.getJSONArray(j);
                                String code = tempArray.getString(0);
                                String category = tempArray.getString(1);
                                String kind = tempArray.getString(2);
                                String description = tempArray.getString(3);
                                String price = standardDecimalFormat(tempArray.getString(4));
                                String cost = tempArray.getString(5);
                                String type = tempArray.getString(6);
                                OfferView offerView = new OfferView(code, category, kind, description, price, cost, type);
                                availableOfferList.add(offerView);
                            }
                            showOffersList(availableOfferList);
                        }
                        CustomProgress.dismissDialog();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        if (tgAvailableDisable.isChecked()){
                            disableOfferList = new ArrayList<>();
                            showOffersList(disableOfferList);
                        }
                        else {
                            availableOfferList = new ArrayList<>();
                            showOffersList(availableOfferList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
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
        layAvailableDisable.setVisibility(View.VISIBLE);
        txtError.setText(getResources().getString(R.string.service_connection_failed_shop) + " " + SHOP_NAME + " " + SHOP_BRANCH);
    }

    private void showConnectSuccessUI(){
        CustomProgress.dismissDialog();
        layMainContent.setVisibility(View.VISIBLE);
        layLoading.setVisibility(View.GONE);
        layDisconnect.setVisibility(View.INVISIBLE);
        layAvailableDisable.setVisibility(View.VISIBLE);
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
        showCategoryList();
        showOffersList(availableOfferList);
    }

    private void showOffersList(ArrayList<OfferView> arrayList){
        offerViewArrayList = new ArrayList<>();
        offerViewArrayList = arrayList;
        OfferAdapter staffAdapter = new OfferAdapter(getContext(),
                R.layout.item_offer, arrayList);
        lstOffer.setAdapter(staffAdapter);
        lstOffer.setOnItemClickListener(onItemListener);
    }

    ListView.OnItemClickListener onItemListener = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentOfferDescription = offerViewArrayList.get(position).description;
            currentOfferCode = offerViewArrayList.get(position).code;
            currentOfferCategory = offerViewArrayList.get(position).category;
            currentOfferPrice = offerViewArrayList.get(position).price;
            Intent intent = new Intent(getContext(), OfferDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().startActivity(intent);
        }
    };

    private void showCategoryList(){
        categoryViewAdapter = new OfferCategoryViewAdapter(getContext(), categoryList, categoryListener);
        rvCategoryList.setAdapter(categoryViewAdapter);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                currentSelectedButton = rvCategoryList.findViewWithTag("category" + 0);
                currentSelectedButton.setSelected(true);
            }

        }, 500);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    private ItemClickListener categoryListener = new ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            ArrayList<OfferCategoryView> tempArrayList = new ArrayList<>();
            if (position != currentSelectedTagNo){

                String selectedCategoryName = categoryList.get(position).categoryName;
                if (selectedCategoryName.equals("ALL")){
                    selectedCategoryName = "";
                }
                currentCategoryName = selectedCategoryName;
                getOffersWithCategory(requestUUID, selectedCategoryName);
                if (currentSelectedButton != null)
                    currentSelectedButton.setSelected(false);
                Button selectedButton = rvCategoryList.findViewWithTag("category" + position);
                selectedButton.setSelected(true);
                currentSelectedTagNo = position;
                currentSelectedButton = selectedButton;
            }
        }
    };
}