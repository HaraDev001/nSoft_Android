package com.nsoft.laundromat.controller.item;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseActivity;
import com.nsoft.laundromat.controller.menu.ui.offer.OfferCategoryView;
import com.nsoft.laundromat.controller.model.ReplenishItemDetailObject;
import com.nsoft.laundromat.controller.model.ReplenishItemObject;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yalantis.phoenix.PullToRefreshView;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.DEFAULT_STRING;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.OFFERS_REPLENISH_GET_CATEGORY;
import static com.nsoft.laundromat.common.Global.OFFERS_REPLENISH_GET_CATEGORY_DETAIL;
import static com.nsoft.laundromat.common.Global.OFFERS_REPLENISH_SAVE;
import static com.nsoft.laundromat.common.Global.REFRESH_TIME;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.isMainActivity;
import static com.nsoft.laundromat.common.Global.itemViewArrayList;
import static com.nsoft.laundromat.common.Global.itemViewCategoryArrayList;
import static com.nsoft.laundromat.common.Global.replenishItemDetailObjectArrayList;
import static com.nsoft.laundromat.common.Global.replenishItemObjectArrayList;

public class ItemReplenishActivity extends BaseActivity {
    private ImageView imgTopLeft;
    private TextView txtTopTitle;
    private ListView lstItemReplenish;
    private ImageView imgHome;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private ListView lstItemReplenishBottom;
    private LinearLayout layAddList;
    private LinearLayout layReplenish;
    private LinearLayout layCategorySelect;
    private LinearLayout layNameSelect;
    private TextView txtItemCategory;
    private TextView txtItemName;
    private TextView txtItemUnit;
    private ImageView imgCalendar;
    private EditText edtQuantity;
    private TextView txtExpiredDate;

    private int currentCheckedCategoryNo = 0;
    private String currentCheckedCategoryName = "";
    private int currentCheckedItemNo = 0;
    private String currentCheckedItemName = "";
    private boolean isFirst = true;
    private String[] listCategories;
    private String[] listItems;
    private String requestUUID = "";

    private RecyclerView rvCategoryList;
    private ItemReplenishCategoryViewAdapter categoryViewAdapter;
    private ArrayList<OfferCategoryView> categoryList;
    private int currentSelectedTagNo = 0;
    private Button currentSelectedButton;
    private String currentCategoryName = "";

    private boolean isFirstShowItem = true;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private PullToRefreshView mPullToRefreshView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_replenish);

        thisActivity = this;
        thisContext = this;
        thisView = findViewById(R.id.activity_item_replenish);

        initBasicUI();
        showBottomInformation();
        itemViewArrayList = new ArrayList<>();
        isFirst = true;
        sendRequestToServer(OFFERS_REPLENISH_GET_CATEGORY, DEFAULT_STRING);
        isMainActivity = false;
    }

    @Override
    public void onDestroy() {

        CustomProgress.dismissDialog();
        layLoading.setVisibility(View.GONE);
        super.onDestroy();
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
        txtTopTitle.setText(R.string.title_item_replenish);
//
//        lstItemReplenish = findViewById(R.id.lst_replenish);
        imgHome = findViewById(R.id.img_home);
        imgHome.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                Intent intent = new Intent(ItemReplenishActivity.this, MenuActivity.class);
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

        lstItemReplenishBottom = findViewById(R.id.lst_replenish_bottom);
        layAddList = findViewById(R.id.lay_add_list);
        layAddList.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                addNewItemList();
            }
        });
        layReplenish = findViewById(R.id.lay_replenish);
        layReplenish.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                if (itemViewArrayList.size() > 0){

                    new AlertDialog.Builder(thisContext)
//                        .setTitle("Title")
                        .setMessage("Do you really want to replenish item?")
//                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                saveReplenishItems(OFFERS_REPLENISH_SAVE);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                }
                else {
                    showToast("There is no item");
                }

            }
        });
        txtItemCategory = findViewById(R.id.txt_category);
        txtItemName = findViewById(R.id.txt_item_name);
        txtItemUnit = findViewById(R.id.txt_unit);
        txtExpiredDate = findViewById(R.id.txt_date);
        edtQuantity = findViewById(R.id.edt_quantity);
        imgCalendar = findViewById(R.id.img_calendar);
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
                        if(year < currentYear)
                        {
                            txtExpiredDate.setText("");
                            String msgContent = getString(R.string.content_input_date_overlay);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month < currentMonth){
                            txtExpiredDate.setText("");
                            String msgContent = getString(R.string.content_input_date_overlay);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month == currentMonth && day < currentDay){
                            txtExpiredDate.setText("");
                            String msgContent = getString(R.string.content_input_date_overlay);
                            showToast(msgContent);
                        }
                        else {
                            txtExpiredDate.setText(year + "-" + month + "-" + day);
                        }
                    }
                });
                datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
            }
        });

        layCategorySelect = findViewById(R.id.lay_category_select);
        layCategorySelect.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItemReplenishActivity.this);
                mBuilder.setTitle("Choose an category");
                mBuilder.setSingleChoiceItems(listCategories, currentCheckedCategoryNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtItemCategory.setText(listCategories[i]);
                        currentCheckedCategoryNo = i;
                        currentCheckedCategoryName = listCategories[i];
                        dialogInterface.dismiss();
                        sendRequestToServer(OFFERS_REPLENISH_GET_CATEGORY_DETAIL, currentCheckedCategoryName);
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });
        layNameSelect = findViewById(R.id.lay_name_select);
        layNameSelect.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ItemReplenishActivity.this);
                mBuilder.setTitle("Choose an item");
                mBuilder.setSingleChoiceItems(listItems, currentCheckedItemNo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        txtItemName.setText(listItems[i]);
                        txtItemUnit.setText(replenishItemDetailObjectArrayList.get(i).getUnit());
                        currentCheckedItemNo = i;
                        currentCheckedItemNo = i;
                        currentCheckedItemName = listItems[i];
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        rvCategoryList = findViewById(R.id.rv_category_list);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(thisContext, LinearLayoutManager.HORIZONTAL, false);
        rvCategoryList.setLayoutManager(horizontalLayoutManager);

        lstItemReplenish = findViewById(R.id.lst_item);

        mSlidingUpPanelLayout = findViewById(R.id.activity_item_replenish);
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

        mPullToRefreshView =findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {

                    showToast(itemViewArrayList.size() + "");
                    mPullToRefreshView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPullToRefreshView.setRefreshing(false);
                        }
                    }, REFRESH_TIME);
                }
            });
        }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
    }

//    private void showItemList(){
//        itemViewArrayList = new ArrayList<>();
//        for (int i = 0; i < 10; i++){
//            ItemView itemObject = new ItemView("Item Name", "100pcs", "2019.00", "");
//
//            itemViewArrayList.add(itemObject);
//        }
//        ItemAdapter itemAdapter = new ItemAdapter(thisContext,
//                R.layout.item_replenish, itemViewArrayList, mListener);
//        lstItemReplenish.setAdapter(itemAdapter);
//    }

    private void showItemReplenishList(){
        if (itemViewArrayList != null)
        {
            ItemReplenishAdapter itemReplenishAdapter = new ItemReplenishAdapter(thisContext,
                    R.layout.item_replenish_bottom, itemViewArrayList, itemSelectListener);
            lstItemReplenishBottom.setAdapter(itemReplenishAdapter);
        }
    }

    private ItemReplenishAdapter.MyClickListener itemSelectListener = new ItemReplenishAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(int position, View v) {
            switch (v.getId()){
                case R.id.img_delete:
                    deleteSelectedItem(itemViewArrayList.get(position).itemId + "");
                    break;
            }
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

    private void getRequestData(final String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_item_options);
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
                        JSONObject responseData =  response.getJSONObject("data");
                        JSONArray tempOptions = responseData.getJSONArray("options");
                        if (!isFirst){
                            CustomProgress.dismissDialog();
                            replenishItemDetailObjectArrayList = new ArrayList<>();
                            listItems = new String[tempOptions.length()];
                            if (tempOptions != null){
                                for (int i = 0; i < tempOptions.length(); i++){
                                    JSONArray tempArray = tempOptions.getJSONArray(i);
                                    ReplenishItemDetailObject replenishItemDetailObject = new ReplenishItemDetailObject();
                                    replenishItemDetailObject.setItemName(tempArray.getString(0));
                                    replenishItemDetailObject.setUnit(tempArray.getString(1));
                                    replenishItemDetailObject.setItemCode(tempArray.getString(2));
                                    replenishItemDetailObject.setNo(i);
                                    replenishItemDetailObject.setExpiredDate("000");
                                    replenishItemDetailObjectArrayList.add(replenishItemDetailObject);
                                    listItems[i] = tempArray.getString(0);
                                }
                                txtItemName.setText(replenishItemDetailObjectArrayList.get(0).getItemName());
                                txtItemUnit.setText(replenishItemDetailObjectArrayList.get(0).getUnit());
                                currentCheckedItemNo = 0;
                            }
                            getTempReplenishItems();
                        }
                        else {
                            isFirst = false;
                            replenishItemObjectArrayList = new ArrayList<>();
                            listCategories = new String[tempOptions.length()];
                            if (tempOptions != null){
                                for (int i = 0; i < tempOptions.length(); i++){
                                    JSONArray tempArray = tempOptions.getJSONArray(i);
                                    ReplenishItemObject replenishItemObject = new ReplenishItemObject();
                                    replenishItemObject.setItemName(tempArray.getString(0));
                                    replenishItemObject.setNo(i);
//                                    replenishItemObject.setItemCode(tempArray.getString(2));
                                    replenishItemObjectArrayList.add(replenishItemObject);
                                    listCategories[i] = tempArray.getString(0);
                                }
                                txtItemCategory.setText(replenishItemObjectArrayList.get(0).getItemName());
                                currentCheckedCategoryNo = 0;
                                currentCheckedCategoryName = replenishItemObjectArrayList.get(0).getItemName();
                                firstConnectionSuccess();
                                sendRequestToServer(OFFERS_REPLENISH_GET_CATEGORY_DETAIL, currentCheckedCategoryName);
                            }
                        }
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

    private void addNewItemList(String itemName, String itemCode, String quantity, String unit, String expiredDate){
        if (checkInputValue(itemCode, quantity, expiredDate)){
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_add_item_replenish);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("account_id", Global.userId + "");
            paramsPost.put("machine_id", Global.MACHINE_ID);
            paramsPost.put("item_code", itemCode);
            paramsPost.put("item_name", itemName);
            paramsPost.put("quantity", quantity);
            paramsPost.put("unit", unit);
            if (!expiredDate.contains("000"))
                paramsPost.put("expired_date", expiredDate);
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
                            itemViewArrayList = new ArrayList<>();
                            for (int i = 0; i < responseData.length(); i++) {
                                JSONObject temp = responseData.getJSONObject(i);
                                int id = temp.getInt("id");
                                String accountId = temp.getString("account_id");
                                String machineId = temp.getString("machine_id");
                                String strQuentity = temp.getString("quantity");
                                String strName = temp.getString("item_name");
                                String strCode = temp.getString("item_code");
                                String strUnit = temp.getString("unit");
                                String strDate = temp.getString("expired_date");

                                ItemView itemView = new ItemView(id, strName, strUnit, strDate, strQuentity, strCode);
                                itemViewArrayList.add(itemView);
                            }
                            showItemReplenishList();

                        }
                        else if (responseCode == Global.RESULT_FAILED) {
                            CustomProgress.dismissDialog();
                            showToast("add new item failed");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomProgress.dismissDialog();
                        showToast("Network error");
                    }
                }
            }.execute(httpCallPost);
        }
    }

    private void getTempReplenishItems(){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_item_replenish);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("account_id", Global.userId + "");
        paramsPost.put("machine_id", Global.MACHINE_ID);
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
                        itemViewArrayList = new ArrayList<>();
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject temp = responseData.getJSONObject(i);
                            int id = temp.getInt("id");
                            String accountId = temp.getString("account_id");
                            String machineId = temp.getString("machine_id");
                            String strQuentity = temp.getString("quantity");
                            String strName = temp.getString("item_name");
                            String strCode = temp.getString("item_code");
                            String strUnit = temp.getString("unit");
                            String strDate = temp.getString("expired_date");

                            ItemView itemView = new ItemView(id, strName, strUnit, strDate, strQuentity, strCode);
                            itemViewArrayList.add(itemView);
                        }
                        connectionSuccess();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("get replenish items failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }

    private void deleteSelectedItem(String itemId){
        showProgressDialog(getString(R.string.message_content_loading));
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_delete_item_replenish);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("account_id", Global.userId + "");
        paramsPost.put("machine_id", Global.MACHINE_ID);
        paramsPost.put("item_id", itemId);
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
                        itemViewArrayList = new ArrayList<>();
                        for (int i = 0; i < responseData.length(); i++) {
                            JSONObject temp = responseData.getJSONObject(i);
                            int id = temp.getInt("id");
                            String accountId = temp.getString("account_id");
                            String machineId = temp.getString("machine_id");
                            String strQuentity = temp.getString("quantity");
                            String strName = temp.getString("item_name");
                            String strCode = temp.getString("item_code");
                            String strUnit = temp.getString("unit");
                            String strDate = temp.getString("expired_date");

                            ItemView itemView = new ItemView(id, strName, strUnit, strDate, strQuentity, strCode);
                            itemViewArrayList.add(itemView);
                        }
                        showItemReplenishList();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("get replenish items failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }

    private void saveReplenishItems(int sqlNo){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_save_replenish_item);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("machine_id", Global.MACHINE_ID);
        paramsPost.put("request_by", Global.userEmail);
        paramsPost.put("sql_no", sqlNo + "");
        paramsPost.put("status_id", Global.DATA_REQUESTED + "");
        paramsPost.put("user_id", Global.userId + "");
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
                        showToast("items successfully replenished");
                        itemViewArrayList = new ArrayList<>();
                        showItemReplenishList();
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        CustomProgress.dismissDialog();
                        showToast("item save failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomProgress.dismissDialog();
                    showToast("Network error");
                }
            }
        }.execute(httpCallPost);
    }


    private boolean checkInputValue(String code, String quantity, String date){
        boolean isValue = true;
        if (code.equals("")){
            String content = getResources().getString(R.string.content_select_item_name);
            showToast(content);
            isValue = false;
        }
        else if (quantity.equals("")){
            String content = getResources().getString(R.string.content_input_quantity);
            showToast(content);
            isValue = false;
        }
//        else if (date.equals("000")){
//            String content = getResources().getString(R.string.content_input_date);
//            showToast(content);
//            isValue = false;
//        }

        return isValue;
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
        layDisconnect.setVisibility(View.GONE);
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
        isFirstShowItem = true;
        stopConnectionTimer();
        showConnectSuccessUI();
        if (currentCategoryName.equals(""))
            showCategoryList();
        showItemList();
        showItemReplenishList();
    }

    private void firstConnectionSuccess(){
        stopConnectionTimer();
//        showItemReplenishList();
    }

    private void showCategoryList(){
        categoryList = new ArrayList<>();
        for (int i = 0; i < replenishItemObjectArrayList.size(); i++){
            String categoryName = replenishItemObjectArrayList.get(i).getItemName();
            String categoryCode = replenishItemObjectArrayList.get(i).getItemCode();
            OfferCategoryView offerCategoryView = new OfferCategoryView(categoryName, i, false);
            categoryList.add(offerCategoryView);
        }
        categoryViewAdapter = new ItemReplenishCategoryViewAdapter(thisContext, categoryList, categoryListener);
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
                if (currentSelectedButton != null)
                    currentSelectedButton.setSelected(false);
                Button selectedButton = rvCategoryList.findViewWithTag("category" + position);
                selectedButton.setSelected(true);
                currentSelectedTagNo = position;
                currentSelectedButton = selectedButton;
                sendRequestToServer(OFFERS_REPLENISH_GET_CATEGORY_DETAIL, currentCategoryName);
            }
        }
    };

    private void showItemList(){
        if (isFirstShowItem){
            isFirstShowItem = false;
            itemViewCategoryArrayList = new ArrayList<>();
            for (int i = 0; i < replenishItemDetailObjectArrayList.size(); i++){
                int id = replenishItemDetailObjectArrayList.get(i).getNo();
                String name = replenishItemDetailObjectArrayList.get(i).getItemName();
                String code = replenishItemDetailObjectArrayList.get(i).getItemCode();
                String unit = replenishItemDetailObjectArrayList.get(i).getUnit();
                String expiredDate = replenishItemDetailObjectArrayList.get(i).getExpiredDate();
                String qty = String.valueOf(replenishItemDetailObjectArrayList.get(i).getQuantiry());
                ItemView itemObject = new ItemView(id, name, unit, expiredDate, qty, code);

                itemViewCategoryArrayList.add(itemObject);
            }
            ItemAdapter itemAdapter = new ItemAdapter(thisContext, R.layout.item_replenish, itemViewCategoryArrayList, mListener);
            itemAdapter.notifyDataSetChanged();
            lstItemReplenish.setAdapter(itemAdapter);
        }
        else{
            ItemAdapter itemAdapter = new ItemAdapter(thisContext, R.layout.item_replenish, itemViewCategoryArrayList, mListener);
            itemAdapter.notifyDataSetChanged();
            lstItemReplenish.setAdapter(itemAdapter);
        }
    }

    private ItemAdapter.MyClickListener mListener = new ItemAdapter.MyClickListener() {
        @Override
        public void myBtnOnClick(final int position, View v) {
            switch (v.getId()){
                case R.id.img_calendar:
                    DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
                    datePickerDialogFragment.setOnDateChooseListener(new DatePickerDialogFragment.OnDateChooseListener() {
                        @Override
                        public void onDateChoose(int year, int month, int day) {
                            Calendar c = Calendar.getInstance();
                            int currentYear = c.get(Calendar.YEAR);
                            int currentMonth = c.get(Calendar.MONTH) + 1;
                            int currentDay = c.get(Calendar.DAY_OF_MONTH);
                            if(year < currentYear)
                            {
                                String msgContent = getString(R.string.content_input_date_overlay);
                                showToast(msgContent);
                            }
                            else if (year == currentYear && month < currentMonth){
                                String msgContent = getString(R.string.content_input_date_overlay);
                                showToast(msgContent);
                            }
                            else if (year == currentYear && month == currentMonth && day < currentDay){
                                String msgContent = getString(R.string.content_input_date_overlay);
                                showToast(msgContent);
                            }
                            else {
                                String strDate = year + "-" + month + "-" + day;
                                itemViewCategoryArrayList.get(position).expiredDate = strDate;
                                showItemList();
                            }
                        }
                    });
                    datePickerDialogFragment.show(getFragmentManager(), "DatePickerDialogFragment");
                    break;
                case R.id.img_add:
                    final String itemName = itemViewCategoryArrayList.get(position).itemName;
                    final String itemCode = itemViewCategoryArrayList.get(position).itemCode;
                    final String quantity = itemViewCategoryArrayList.get(position).itemQty;
                    final String unit = itemViewCategoryArrayList.get(position).itemUnit;
                    final String expiredDate = itemViewCategoryArrayList.get(position).expiredDate;
                    addNewItemList(itemName, itemCode, quantity, unit, expiredDate);
                    break;
            }
        }
    };

}
