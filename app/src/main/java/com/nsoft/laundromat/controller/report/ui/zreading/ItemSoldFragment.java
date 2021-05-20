package com.nsoft.laundromat.controller.report.ui.zreading;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.BarChartObject;
import com.nsoft.laundromat.controller.model.ItemSoldChartObject;
import com.nsoft.laundromat.controller.model.ItemSoldDailyObject;
import com.nsoft.laundromat.controller.model.ItemSoldHoulyObject;
import com.nsoft.laundromat.controller.model.ItemSoldMonthlyObject;
import com.nsoft.laundromat.controller.model.ItemSoldWeeklyObject;
import com.nsoft.laundromat.controller.model.ItemSoldYearlyObject;
import com.nsoft.laundromat.controller.model.ReportObject;
import com.nsoft.laundromat.controller.report.ui.xreading.ReportThreeAdapter;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_ITEM_SOLD;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REPORTS_ITEM_SOLD;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.reportObjectArrayList;

public class ItemSoldFragment extends BaseFragment implements OnChartValueSelectedListener {
    private TextView txtPageTitle;
    private ImageView imgHome;
    private LinearLayout layEmail;
    private ImageView imgSendEmail;
    private ListView lstSalesItem;
    private ListView lstSalesItemThree;
    private ImageView imgCalendar;
    private LinearLayout layHourly;
    private LinearLayout layDaily;
    private LinearLayout layWeekly;
    private LinearLayout layMonthly;
    private LinearLayout layYearly;
    private TextView txtCalendar;
    private BarChart barChartItemSold;
    private BarChart barChartItemSoldOne;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private TextView txtListTitle;
    private LinearLayout layListItem;
    private LinearLayout layListItemThree;
    private TextView txtListTitleThree;
    private TextView txtListSubTitleThree;

    private static String HOURLY_CATEGORY = "Hourly";
    private static String DAILY_CATEGORY = "Daily";
    private static String WEEKLY_CATEGORY = "Weekly";
    private static String MONTHLY_CATEGORY = "Monthly";
    private static String YEARLY_CATEGORY = "Yearly";

    private String selectedDate = "";
    private String selectedCategory = "";
    private String strUUID = "";
    protected Typeface tfRegular;
    protected Typeface tfLight;

    private boolean isSendEmail = false;

    private ArrayList<ItemSoldChartObject> totalChartObjects;
    private ArrayList<ItemSoldHoulyObject> totalHourlyObjects;
    private ArrayList<ItemSoldDailyObject> totalDailyObjects;
    private ArrayList<ItemSoldWeeklyObject> totalWeeklyObjects;
    private ArrayList<ItemSoldMonthlyObject> totalMonthlyObjects;
    private ArrayList<ItemSoldYearlyObject> totalYearlyObjects;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_item_sold, container, false);
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
        invisibleTopIcons();
        layEmail.setVisibility(View.VISIBLE);

        imgSendEmail.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                sendRequestToServer(EMAIL_REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
                isSendEmail = true;
            }
        });

        layMainContent = root.findViewById(R.id.lay_main);
        layLoading = root.findViewById(R.id.lay_loading);
        layDisconnect = root.findViewById(R.id.lay_disconnect);
        txtError = root.findViewById(R.id.txt_error);
        btnTryAgain = root.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
//                startConnection();
            }
        });
        layListItem = root.findViewById(R.id.lay_list_item);
        layListItemThree = root.findViewById(R.id.lay_list_item_three);
        lstSalesItem = root.findViewById(R.id.lst_sale_item);
        lstSalesItemThree = root.findViewById(R.id.lst_sale_item_three);
        txtListTitle = root.findViewById(R.id.txt_list_title);
        txtListTitleThree = root.findViewById(R.id.txt_list_title_three);
        txtListSubTitleThree = root.findViewById(R.id.txt_list_sub_title_three);
        txtCalendar = root.findViewById(R.id.txt_calendar);
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
                        if(year > currentYear)
                        {
                            txtCalendar.setText("");
                            String msgContent = getString(R.string.content_input_date_incorrect);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month > currentMonth){
                            txtCalendar.setText("");
                            String msgContent = getString(R.string.content_input_date_incorrect);
                            showToast(msgContent);
                        }
                        else if (year == currentYear && month == currentMonth && day > currentDay){
                            txtCalendar.setText("");
                            String msgContent = getString(R.string.content_input_date_incorrect);
                            showToast(msgContent);
                        }
                        else {
                            String dtStart = year + "/" + month + "/" + day;
                            txtCalendar.setText(standardDateFormat(dtStart));
                            selectedDate = year + "-" + month + "-" + day;
                            sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
//                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//                            try {
//                                Date date = format.parse(dtStart);
//                                SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
//                                String dateOne = spf.format(date);
//                                txtCalendar.setText(dateOne);
//                                selectedDate = year + "-" + month + "-" + day;
//                                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
                        }
                    }
                });
                datePickerDialogFragment.show(getActivity().getFragmentManager(), "DatePickerDialogFragment");
            }
        });
        layHourly = root.findViewById(R.id.lay_hourly);
        layHourly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layHourly.setSelected(true);
                selectedCategory = HOURLY_CATEGORY;
                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
            }
        });

        layDaily = root.findViewById(R.id.lay_daily);
        layDaily.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layDaily.setSelected(true);
                selectedCategory = DAILY_CATEGORY;
                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
            }
        });

        layWeekly = root.findViewById(R.id.lay_weekly);
        layWeekly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layWeekly.setSelected(true);
                selectedCategory = WEEKLY_CATEGORY;
                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
            }
        });

        layMonthly = root.findViewById(R.id.lay_monthly);
        layMonthly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layMonthly.setSelected(true);
                selectedCategory = MONTHLY_CATEGORY;
                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
            }
        });

        layYearly = root.findViewById(R.id.lay_yearly);
        layYearly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layYearly.setSelected(true);
                selectedCategory = YEARLY_CATEGORY;
                sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);
            }
        });
        layHourly.setSelected(true);
        selectedCategory = HOURLY_CATEGORY;
        getCurrentDate();
        sendRequestToServer(REPORTS_ITEM_SOLD, selectedCategory, selectedDate);

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_item_sold);
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
        barChartItemSold = root.findViewById(R.id.cht_item_sold);
        barChartItemSoldOne = root.findViewById(R.id.cht_item_sold_one);
//        initBarChart();
//        setBarChartValue();

        return root;
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_report_item_sold));
        layEmail = getActivity().findViewById(R.id.lay_email);
        imgSendEmail = getActivity().findViewById(R.id.img_send_email);
        layEmail.setVisibility(View.GONE);
    }

    private void makeCategoryNoSelected(){
        layHourly.setSelected(false);
        layDaily.setSelected(false);
        layWeekly.setSelected(false);
        layMonthly.setSelected(false);
        layYearly.setSelected(false);
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

    private void initBarChart(){
        barChartItemSold.setVisibility(View.VISIBLE);
        barChartItemSoldOne.setVisibility(View.GONE);
        barChartItemSold.removeAllViews();
        barChartItemSold.clear();
        barChartItemSold.getDescription().setEnabled(false);

        barChartItemSold.setPinchZoom(false);
        barChartItemSold.setDrawBarShadow(false);
        barChartItemSold.setDrawGridBackground(false);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(chart); // For bounds control
//        barChartItemSold.setMarker(mv); // Set the marker to the chart;

        Legend l = barChartItemSold.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
//        l.setTypeface(tfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = barChartItemSold.getXAxis();
//        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value);
            }
        });

        YAxis leftAxis = barChartItemSold.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChartItemSold.getAxisRight().setEnabled(false);
        barChartItemSold.animateY(500);
    }

    private void setBarChartValue(ArrayList<ItemSoldChartObject> barChartArray){

        float groupSpace = 0.08f;
        float barSpace = 0.03f;
        float barWidth = (1.0f - groupSpace) / barChartArray.size() - barSpace;
        // (0.2 + 0.03) * 4 + 0.08 = 1.00 -> interval per "group"

        int groupCount = barChartArray.get(0).getBarChartObjects().size();
        int startValue = barChartArray.get(0).getBarChartObjects().get(0).getLabelName();
        List<IBarDataSet> dataSets = new ArrayList<>();

        for (int i = 0; i < barChartArray.size(); i++){
            ArrayList<BarEntry> values1 = new ArrayList<>();
            ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
            barChartObjects = barChartArray.get(i).getBarChartObjects();
            String strLabel = barChartArray.get(i).getItemName();
            int color = barChartArray.get(i).getColor();
            for (int j = 0; j < barChartObjects.size(); j++) {
                BarChartObject tempValue = barChartObjects.get(j);
                values1.add(new BarEntry(tempValue.getLabelName(), tempValue.getAmount()));
            }

            BarDataSet set1;
            if (barChartItemSold.getData() != null && barChartItemSold.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChartItemSold.getData().getDataSetByIndex(i);
                set1.setValues(values1);
                barChartItemSold.getData().notifyDataChanged();
                barChartItemSold.notifyDataSetChanged();
                dataSets.add(set1);
            } else {
                // create 4 DataSets
                set1 = new BarDataSet(values1, strLabel);
                set1.setColor(color);
                set1.setDrawValues(false);
                dataSets.add(set1);
            }
        }

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new LargeValueFormatter());
        barChartItemSold.setData(data);

        barChartItemSold.setFitBars(true);
        XAxis xAxis = barChartItemSold.getXAxis();
        xAxis.setLabelCount(groupCount);

        // specify the width each bar should have
        barChartItemSold.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        barChartItemSold.getXAxis().setAxisMinimum(startValue);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        barChartItemSold.getXAxis().setAxisMaximum(startValue + barChartItemSold.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChartItemSold.groupBars(startValue, groupSpace, barSpace);
        barChartItemSold.invalidate();
    }

    private void initBarChartOneBranch() {
        barChartItemSoldOne.setVisibility(View.VISIBLE);
        barChartItemSold.setVisibility(View.GONE);
        barChartItemSoldOne.removeAllViews();
        barChartItemSoldOne.clear();


        barChartItemSoldOne.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChartItemSoldOne.setMaxVisibleValueCount(60);
        barChartItemSoldOne.setTouchEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChartItemSoldOne.setPinchZoom(false);
        barChartItemSoldOne.setAutoScaleMinMaxEnabled(false);

        barChartItemSoldOne.setDrawBarShadow(false);
        barChartItemSoldOne.setDrawGridBackground(false);

        Legend l = barChartItemSoldOne.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
//        l.setTypeface(tfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = barChartItemSoldOne.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
//                return xLabelValues[(int) value];
                return String.valueOf((int) value);
            }
        });

        //barChartEEG.getAxisLeft().setDrawGridLines(false);
        YAxis leftAxis = barChartItemSoldOne.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChartItemSoldOne.getAxisRight();
        rightAxis.setEnabled(false);

        barChartItemSoldOne.animateY(500);
        barChartItemSoldOne.getLegend().setEnabled(false);
    }

    private void setBarChartValueOneBranch(String [] barChartXValues, int [] barChartValues, int[] varColors, String strLabel) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        for (int i = 0; i < barChartValues.length; i++){
            yVals1.add(new BarEntry(Float.parseFloat(barChartXValues[i]), barChartValues[i]));
        }
        BarDataSet set1;

        if (barChartItemSoldOne.getData() != null && barChartItemSoldOne.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChartItemSoldOne.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChartItemSoldOne.getData().notifyDataChanged();
            barChartItemSoldOne.notifyDataSetChanged();
            dataSets.add(set1);
        } else {
            set1 = new BarDataSet(yVals1, strLabel);
            set1.setColors(varColors);
            set1.setDrawValues(false);
            dataSets.add(set1);
        }

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new LargeValueFormatter());
        barChartItemSoldOne.setData(data);

        barChartItemSoldOne.setFitBars(true);
        XAxis xAxis = barChartItemSoldOne.getXAxis();
        xAxis.setLabelCount(barChartXValues.length);

        barChartItemSoldOne.invalidate();
    }

    private void sendRequestToServer(int sqlNo, String categoryName, String selectedDate){
        if (sqlNo == REPORTS_ITEM_SOLD){
            isSendEmail = false;
        }
        else if (sqlNo == EMAIL_REPORTS_ITEM_SOLD){
            isSendEmail = true;
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

    private void getItemSoldData(String uniqueId){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_report_data);
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
                        JSONArray tempResult = responseData.getJSONArray("result");
                        totalHourlyObjects = new ArrayList<>();
                        totalDailyObjects = new ArrayList<>();
                        totalWeeklyObjects = new ArrayList<>();
                        totalMonthlyObjects = new ArrayList<>();
                        totalYearlyObjects = new ArrayList<>();
                        totalChartObjects = new ArrayList<>();

                        reportObjectArrayList = new ArrayList<>();
                        if (tempResult.length() == 0){
                            connectionEmpty();
                        }
                        else {
                            connectionSuccess();
                            for (int i = 0; i < tempResult.length(); i++){
                                JSONArray tempArray = tempResult.getJSONArray(i);

                                if (selectedCategory.equals(HOURLY_CATEGORY)){
                                    ItemSoldHoulyObject itemSoldHoulyObject = new ItemSoldHoulyObject();
                                    itemSoldHoulyObject.setDateTime(tempArray.getString(0));
                                    itemSoldHoulyObject.setWeekday(tempArray.getString(1));
                                    itemSoldHoulyObject.setTime(tempArray.getInt(2));
                                    itemSoldHoulyObject.setItemName(tempArray.getString(3));
                                    itemSoldHoulyObject.setItemCount(tempArray.getInt(4));
                                    itemSoldHoulyObject.setAmount(tempArray.getInt(5));
                                    totalHourlyObjects.add(itemSoldHoulyObject);

                                    ReportObject reportObject = new ReportObject();
                                    reportObject.setNo(i + 1);
                                    reportObject.setSubTitle(tempArray.getInt(2) + " ");
                                    reportObject.setTitle(tempArray.getString(3));
                                    reportObject.setAmount(standardDecimalFormat(tempArray.getInt(4) + ""));
                                    reportObjectArrayList.add(reportObject);
                                }
                                else if (selectedCategory.equals(DAILY_CATEGORY)){
                                    ItemSoldDailyObject itemSoldDailyObject = new ItemSoldDailyObject();
                                    itemSoldDailyObject.setDateTime(tempArray.getString(0));
                                    itemSoldDailyObject.setDay(tempArray.getInt(1));
                                    itemSoldDailyObject.setWeekDay(tempArray.getString(2));
                                    itemSoldDailyObject.setItemName(tempArray.getString(3));
                                    itemSoldDailyObject.setItemCount(tempArray.getInt(4));
                                    itemSoldDailyObject.setAmount(tempArray.getInt(5));
                                    totalDailyObjects.add(itemSoldDailyObject);

                                    ReportObject reportObject = new ReportObject();
                                    reportObject.setNo(i + 1);
                                    reportObject.setSubTitle(tempArray.getInt(1) + " ");
                                    reportObject.setTitle(tempArray.getString(3));
                                    reportObject.setAmount(standardDecimalFormat(tempArray.getInt(4) + ""));
                                    reportObjectArrayList.add(reportObject);
                                }
                                else if (selectedCategory.equals(WEEKLY_CATEGORY)){
                                    ItemSoldWeeklyObject itemSoldWeeklyObject = new ItemSoldWeeklyObject();
                                    itemSoldWeeklyObject.setWeek(tempArray.getInt(0));
                                    itemSoldWeeklyObject.setMonthName(tempArray.getString(1));
                                    itemSoldWeeklyObject.setItemName(tempArray.getString(2));
                                    itemSoldWeeklyObject.setItemCount(tempArray.getInt(3));
                                    itemSoldWeeklyObject.setAmount(tempArray.getInt(4));
                                    totalWeeklyObjects.add(itemSoldWeeklyObject);

                                    ReportObject reportObject = new ReportObject();
                                    reportObject.setNo(i + 1);
                                    reportObject.setTitle(tempArray.getString(2));
                                    reportObject.setSubTitle(tempArray.getInt(0) + " ");
                                    reportObject.setAmount(standardDecimalFormat(tempArray.getInt(3) + ""));
                                    reportObjectArrayList.add(reportObject);
                                }
                                else if (selectedCategory.equals(MONTHLY_CATEGORY)){
                                    ItemSoldMonthlyObject itemSoldMonthlyObject = new ItemSoldMonthlyObject();
                                    itemSoldMonthlyObject.setMonthName(tempArray.getString(0));
                                    itemSoldMonthlyObject.setItemName(tempArray.getString(1));
                                    itemSoldMonthlyObject.setItemCount(tempArray.getInt(2));
                                    itemSoldMonthlyObject.setAmount(tempArray.getInt(3));
                                    itemSoldMonthlyObject.setMonthNo(getMonthNo(tempArray.getString(0)));

                                    totalMonthlyObjects.add(itemSoldMonthlyObject);

                                    ReportObject reportObject = new ReportObject();
                                    reportObject.setNo(i + 1);
                                    reportObject.setTitle(tempArray.getString(1));
                                    reportObject.setSubTitle(tempArray.getString(0));
                                    reportObject.setAmount(standardDecimalFormat(tempArray.getInt(2) + ""));
                                    reportObjectArrayList.add(reportObject);
                                }
                                else if (selectedCategory.equals(YEARLY_CATEGORY)){
                                    ItemSoldYearlyObject itemSoldYearlyObject = new ItemSoldYearlyObject();
                                    itemSoldYearlyObject.setYear(tempArray.getInt(0));
                                    itemSoldYearlyObject.setItemName(tempArray.getString(1));
                                    itemSoldYearlyObject.setItemCount(tempArray.getInt(2));
                                    itemSoldYearlyObject.setAmount(tempArray.getInt(3));
                                    totalYearlyObjects.add(itemSoldYearlyObject);

                                    ReportObject reportObject = new ReportObject();
                                    reportObject.setNo(i + 1);
                                    reportObject.setTitle(tempArray.getString(1));
                                    reportObject.setSubTitle(tempArray.getInt(0) + "");
                                    reportObject.setAmount(standardDecimalFormat(tempArray.getInt(2) + ""));
                                    reportObjectArrayList.add(reportObject);
                                }
                            }
                            showItemSalesList();
                            getGraphInformation();
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
                        isSendEmail = false;
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

    private int getMonthNo(String month){
        int monthNo = 0;
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++){
            if (month.equals(months[i])){
                monthNo = i + 1;
            }
        }
        return monthNo;
    }

    private void startConnection() {
        showConnectingUI();
        initConnectionTimer();
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

    private void connectionSuccess(){
        barChartItemSold.removeAllViews();
        stopConnectionTimer();
        showConnectSuccessUI();
    }
    private void connectionEmpty(){
        barChartItemSold.removeAllViews();
        barChartItemSold.clear();
        barChartItemSoldOne.removeAllViews();
        barChartItemSoldOne.clear();
        showEmptyList();
        stopConnectionTimer();
        showConnectSuccessUI();
    }
    private void showEmptyList(){
        layListItem.setVisibility(View.INVISIBLE);
        layListItemThree.setVisibility(View.INVISIBLE);
//        if (selectedCategory.equals(HOURLY_CATEGORY)){
//            txtListTitle.setText(getContext().getResources().getString(R.string.str_time));
//        }
//        else if (selectedCategory.equals(DAILY_CATEGORY)){
//            txtListTitle.setText(getContext().getResources().getString(R.string.str_date));
//        }
//        else if (selectedCategory.equals(WEEKLY_CATEGORY)){
//            txtListTitle.setText(getContext().getResources().getString(R.string.str_week));
//        }
//        else if (selectedCategory.equals(MONTHLY_CATEGORY)){
//            txtListTitle.setText(getContext().getResources().getString(R.string.str_month));
//        }
//        else if (selectedCategory.equals(YEARLY_CATEGORY)){
//            txtListTitle.setText(getContext().getResources().getString(R.string.str_year));
//        }
//        reportObjectArrayList = new ArrayList<>();
//        ReportObject reportObject = new ReportObject();
//        reportObject.setAmount( "");
//        reportObject.setNo(0);
//        reportObject.setTitle("");
//        reportObjectArrayList.add(reportObject);
//        ReportAdapter reportAdapter = new ReportAdapter(getContext(), R.layout.item_sales_report, reportObjectArrayList);
//        lstSalesItem.setAdapter(reportAdapter);
    }

    private void  showItemSalesList(){
        if (selectedCategory.equals(HOURLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_time));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_time));
        }
        else if (selectedCategory.equals(DAILY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_date));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_date));
        }
        else if (selectedCategory.equals(WEEKLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_week));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_week));
        }
        else if (selectedCategory.equals(MONTHLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_month));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_month));
        }
        else if (selectedCategory.equals(YEARLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_year));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_year));
        }
        layListItemThree.setVisibility(View.VISIBLE);
        layListItem.setVisibility(View.GONE);
        ReportThreeAdapter reportThreeAdapter = new ReportThreeAdapter(getContext(), R.layout.item_sales_report_three, reportObjectArrayList);
        lstSalesItemThree.setAdapter(reportThreeAdapter);

//        else {
//            layListItem.setVisibility(View.VISIBLE);
//            layListItemThree.setVisibility(View.GONE);
//            ReportAdapter reportAdapter = new ReportAdapter(getContext(), R.layout.item_sales_report, reportObjectArrayList);
//            lstSalesItem.setAdapter(reportAdapter);
//        }
    }

    private void getGraphInformation(){
        totalChartObjects = new ArrayList<>();
        if (selectedCategory.equals(HOURLY_CATEGORY)){
            List<Integer> timeArray = new ArrayList<>();
            List<String> itemArray = new ArrayList<>();
           for (int i = 0; i < totalHourlyObjects.size(); i++){
               if (!timeArray.contains(totalHourlyObjects.get(i).getTime())){
                   timeArray.add(totalHourlyObjects.get(i).getTime());
               }
               if (!itemArray.contains(totalHourlyObjects.get(i).getItemName())){
                   itemArray.add(totalHourlyObjects.get(i).getItemName());
               }
           }
           for (int i = 0; i < itemArray.size(); i++){
               String strItem = itemArray.get(i);
               ItemSoldChartObject itemSoldChartObject = new ItemSoldChartObject();
               itemSoldChartObject.setItemName(itemArray.get(i));
               itemSoldChartObject.setNo(i);
               itemSoldChartObject.setSelectStatus(true);
               itemSoldChartObject.setColor(getColor(i));
               ArrayList<ItemSoldHoulyObject> tempItemList = new ArrayList<>();
               for (int k = 0; k < totalHourlyObjects.size(); k++){
                   if (totalHourlyObjects.get(k).getItemName().equals(strItem)){
                       tempItemList.add(totalHourlyObjects.get(k));
                   }
               }
               ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
               for (int j = 0; j < timeArray.size(); j++){
                   BarChartObject barChartObject = new BarChartObject();
                   barChartObject.setLabelName(timeArray.get(j));
                   barChartObject.setAmount(0);
                   for (int k = 0; k < tempItemList.size(); k++){
                       if (tempItemList.get(k).getTime() == timeArray.get(j)){
                           barChartObject.setAmount(tempItemList.get(k).getItemCount());
                       }
                   }
                   barChartObjects.add(barChartObject);
               }
               itemSoldChartObject.setBarChartObjects(barChartObjects);
               totalChartObjects.add(itemSoldChartObject);
           }
        }
        else if (selectedCategory.equals(DAILY_CATEGORY)){
            List<Integer> dayArray = new ArrayList<>();
            List<String> itemArray = new ArrayList<>();
            for (int i = 0; i < totalDailyObjects.size(); i++){
                if (!dayArray.contains(totalDailyObjects.get(i).getDay())){
                    dayArray.add(totalDailyObjects.get(i).getDay());
                }
                if (!itemArray.contains(totalDailyObjects.get(i).getItemName())){
                    itemArray.add(totalDailyObjects.get(i).getItemName());
                }
            }
            for (int i = 0; i < itemArray.size(); i++){
                String strItem = itemArray.get(i);
                ItemSoldChartObject itemSoldChartObject = new ItemSoldChartObject();
                itemSoldChartObject.setItemName(itemArray.get(i));
                itemSoldChartObject.setNo(i);
                itemSoldChartObject.setSelectStatus(true);
                itemSoldChartObject.setColor(getColor(i));
                ArrayList<ItemSoldDailyObject> tempItemList = new ArrayList<>();
                for (int k = 0; k < totalDailyObjects.size(); k++){
                    if (totalDailyObjects.get(k).getItemName().equals(strItem)){
                        tempItemList.add(totalDailyObjects.get(k));
                    }
                }
                ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
                for (int j = 0; j < dayArray.size(); j++){
                    BarChartObject barChartObject = new BarChartObject();
                    barChartObject.setLabelName(dayArray.get(j));
                    barChartObject.setAmount(0);
                    for (int k = 0; k < tempItemList.size(); k++){
                        if (tempItemList.get(k).getDay() == dayArray.get(j)){
                            barChartObject.setAmount(tempItemList.get(k).getItemCount());
                        }
                    }
                    barChartObjects.add(barChartObject);
                }
                itemSoldChartObject.setBarChartObjects(barChartObjects);
                totalChartObjects.add(itemSoldChartObject);
            }

        }
        else if (selectedCategory.equals(WEEKLY_CATEGORY)){
            List<Integer> weekArray = new ArrayList<>();
            List<String> itemArray = new ArrayList<>();
            for (int i = 0; i < totalWeeklyObjects.size(); i++){
                if (!weekArray.contains(totalWeeklyObjects.get(i).getWeek())){
                    weekArray.add(totalWeeklyObjects.get(i).getWeek());
                }
                if (!itemArray.contains(totalWeeklyObjects.get(i).getItemName())){
                    itemArray.add(totalWeeklyObjects.get(i).getItemName());
                }
            }
            for (int i = 0; i < itemArray.size(); i++){
                String strItem = itemArray.get(i);
                ItemSoldChartObject itemSoldChartObject = new ItemSoldChartObject();
                itemSoldChartObject.setItemName(itemArray.get(i));
                itemSoldChartObject.setNo(i);
                itemSoldChartObject.setSelectStatus(true);
                itemSoldChartObject.setColor(getColor(i));
                ArrayList<ItemSoldWeeklyObject> tempItemList = new ArrayList<>();
                for (int k = 0; k < totalWeeklyObjects.size(); k++){
                    if (totalWeeklyObjects.get(k).getItemName().equals(strItem)){
                        tempItemList.add(totalWeeklyObjects.get(k));
                    }
                }
                ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
                for (int j = 0; j < weekArray.size(); j++){
                    BarChartObject barChartObject = new BarChartObject();
                    barChartObject.setLabelName(weekArray.get(j));
                    barChartObject.setAmount(0);
                    for (int k = 0; k < tempItemList.size(); k++){
                        if (tempItemList.get(k).getWeek() == weekArray.get(j)){
                            barChartObject.setAmount(tempItemList.get(k).getItemCount());
                        }
                    }
                    barChartObjects.add(barChartObject);
                }
                itemSoldChartObject.setBarChartObjects(barChartObjects);
                totalChartObjects.add(itemSoldChartObject);
            }
        }
        else if (selectedCategory.equals(MONTHLY_CATEGORY)){
            List<Integer> monthArray = new ArrayList<>();
            List<String> itemArray = new ArrayList<>();
            for (int i = 0; i < totalMonthlyObjects.size(); i++){
                if (!monthArray.contains(totalMonthlyObjects.get(i).getMonthNo())){
                    monthArray.add(totalMonthlyObjects.get(i).getMonthNo());
                }
                if (!itemArray.contains(totalMonthlyObjects.get(i).getItemName())){
                    itemArray.add(totalMonthlyObjects.get(i).getItemName());
                }
            }
            for (int i = 0; i < itemArray.size(); i++){
                String strItem = itemArray.get(i);
                ItemSoldChartObject itemSoldChartObject = new ItemSoldChartObject();
                itemSoldChartObject.setItemName(itemArray.get(i));
                itemSoldChartObject.setNo(i);
                itemSoldChartObject.setSelectStatus(true);
                itemSoldChartObject.setColor(getColor(i));
                ArrayList<ItemSoldMonthlyObject> tempItemList = new ArrayList<>();
                for (int k = 0; k < totalMonthlyObjects.size(); k++){
                    if (totalMonthlyObjects.get(k).getItemName().equals(strItem)){
                        tempItemList.add(totalMonthlyObjects.get(k));
                    }
                }
                ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
                for (int j = 0; j < monthArray.size(); j++){
                    BarChartObject barChartObject = new BarChartObject();
                    barChartObject.setLabelName(monthArray.get(j));
                    barChartObject.setAmount(0);
                    for (int k = 0; k < tempItemList.size(); k++){
                        if (tempItemList.get(k).getMonthNo() == monthArray.get(j)){
                            barChartObject.setAmount(tempItemList.get(k).getItemCount());
                        }
                    }
                    barChartObjects.add(barChartObject);
                }
                itemSoldChartObject.setBarChartObjects(barChartObjects);
                totalChartObjects.add(itemSoldChartObject);
            }

        }
        else if (selectedCategory.equals(YEARLY_CATEGORY)){
            List<Integer> yearArray = new ArrayList<>();
            List<String> itemArray = new ArrayList<>();
            for (int i = 0; i < totalYearlyObjects.size(); i++){
                if (!yearArray.contains(totalYearlyObjects.get(i).getYear())){
                    yearArray.add(totalYearlyObjects.get(i).getYear());
                }
                if (!itemArray.contains(totalYearlyObjects.get(i).getItemName())){
                    itemArray.add(totalYearlyObjects.get(i).getItemName());
                }
            }
            for (int i = 0; i < itemArray.size(); i++){
                String strItem = itemArray.get(i);
                ItemSoldChartObject itemSoldChartObject = new ItemSoldChartObject();
                itemSoldChartObject.setItemName(itemArray.get(i));
                itemSoldChartObject.setNo(i);
                itemSoldChartObject.setSelectStatus(true);
                itemSoldChartObject.setColor(getColor(i));
                ArrayList<ItemSoldYearlyObject> tempItemList = new ArrayList<>();
                for (int k = 0; k < totalYearlyObjects.size(); k++){
                    if (totalYearlyObjects.get(k).getItemName().equals(strItem)){
                        tempItemList.add(totalYearlyObjects.get(k));
                    }
                }
                ArrayList<BarChartObject> barChartObjects = new ArrayList<>();
                for (int j = 0; j < yearArray.size(); j++){
                    BarChartObject barChartObject = new BarChartObject();
                    barChartObject.setLabelName(yearArray.get(j));
                    barChartObject.setAmount(0);
                    for (int k = 0; k < tempItemList.size(); k++){
                        if (tempItemList.get(k).getYear() == yearArray.get(j)){
                            barChartObject.setAmount(tempItemList.get(k).getItemCount());
                        }
                    }
                    barChartObjects.add(barChartObject);
                }
                itemSoldChartObject.setBarChartObjects(barChartObjects);
                totalChartObjects.add(itemSoldChartObject);
            }
        }
        showBarChartValues();
    }

    private int getColor(int no){
        int color = getResources().getColor(R.color.clr_graph_00);
        switch (no % 8){
            case 0:
                color = getResources().getColor(R.color.clr_graph_00);
                break;
            case 1:
                color = getResources().getColor(R.color.clr_graph_01);
                break;
            case 2:
                color = getResources().getColor(R.color.clr_graph_02);
                break;
            case 3:
                color = getResources().getColor(R.color.clr_graph_03);
                break;
            case 4:
                color = getResources().getColor(R.color.clr_graph_04);
                break;
            case 5:
                color = getResources().getColor(R.color.clr_graph_05);
                break;
            case 6:
                color = getResources().getColor(R.color.clr_graph_06);
                break;
            case 7:
                color = getResources().getColor(R.color.clr_graph_07);
                break;
        }
        return color;
    }

    private void showBarChartValues(){
        ArrayList<ItemSoldChartObject> tempChartObjects = new ArrayList<>();
        if (totalChartObjects.size() != 0){
            int selectedItemCount = 0;
            for (int i = 0; i <totalChartObjects.size(); i++){
                if (totalChartObjects.get(i).getSelectStatus()){
                    selectedItemCount++;
                    tempChartObjects.add(totalChartObjects.get(i));
                }
            }
            if (selectedItemCount > 1){
                initBarChart();
                setBarChartValue(tempChartObjects);
            }
            else if (selectedItemCount == 1){
                String [] barChartXLabelValues = new String[tempChartObjects.get(0).getBarChartObjects().size()];
                int [] barChartValues = new int[tempChartObjects.get(0).getBarChartObjects().size()];
                int [] barChartColors = new int[tempChartObjects.get(0).getBarChartObjects().size()];
                String strBranchName = tempChartObjects.get(0).getItemName();
                for (int i = 0; i < tempChartObjects.get(0).getBarChartObjects().size(); i++){
                    barChartXLabelValues[i] = tempChartObjects.get(0).getBarChartObjects().get(i).getLabelName() + "";
                    barChartValues[i] = tempChartObjects.get(0).getBarChartObjects().get(i).getAmount();
                    barChartColors[i] = tempChartObjects.get(0).getColor();
                }
                initBarChartOneBranch();
                setBarChartValueOneBranch(barChartXLabelValues, barChartValues, barChartColors, strBranchName);
            }
            else {
                barChartItemSold.removeAllViews();
                barChartItemSold.clear();
                barChartItemSoldOne.removeAllViews();
                barChartItemSoldOne.clear();
                showEmptyList();
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
                    if (isSendEmail)
                        getEmailSendStatus(strUUID);
                    else
                        getItemSoldData(strUUID);
                }
            }
        }, 1000, 1000);
    }

    private void stopConnection(){
        stopConnectionTimer();
    }

    public void stopConnectionTimer(){
        if(connectionTimer != null){
            connectionTimer.cancel();
            connectionTimer = null;
        }
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}