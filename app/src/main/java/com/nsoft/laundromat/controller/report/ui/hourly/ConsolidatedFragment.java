package com.nsoft.laundromat.controller.report.ui.hourly;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.BarChartObject;
import com.nsoft.laundromat.controller.model.BranchObject;
import com.nsoft.laundromat.controller.model.ConsolidateBranchObject;
import com.nsoft.laundromat.controller.model.ConsolidateChartObject;
import com.nsoft.laundromat.controller.model.ConsolidateDailyObject;
import com.nsoft.laundromat.controller.model.ConsolidateHoulyObject;
import com.nsoft.laundromat.controller.model.ConsolidateMonthlyObject;
import com.nsoft.laundromat.controller.model.ConsolidateWeeklyObject;
import com.nsoft.laundromat.controller.model.ConsolidateYearlyObject;
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

import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_CONSOLIDATE;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REPORTS_CONSOLIDATE;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.SHOP_PAGE_DELAY_TIME;

public class ConsolidatedFragment extends BaseFragment {
    private TextView txtPageTitle;
    private ImageView imgHome;
    private LinearLayout layEmail;
    private ImageView imgSendEmail;
    private ListView lstReport;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private ImageView imgCalendar;
    private LinearLayout layHourly;
    private LinearLayout layDaily;
    private LinearLayout layWeekly;
    private LinearLayout layMonthly;
    private LinearLayout layYearly;
    private TextView txtCalendar;
    private BarChart barChartConsolidate;
    private BarChart barChartConsolidateOne;
    private RecyclerView rvBranchList;
    private ConsolidateBranchViewAdapter adapter;
    public static ArrayList<BranchObject> branchObjects;

    private static String HOURLY_CATEGORY = "Hourly";
    private static String DAILY_CATEGORY = "Daily";
    private static String WEEKLY_CATEGORY = "Weekly";
    private static String MONTHLY_CATEGORY = "Monthly";
    private static String YEARLY_CATEGORY = "Yearly";

    private String selectedDate = "";
    private String selectedCategory = "";
    private String strUUIDS = "";
    private String strUUID = "";
    private int currentSelectedTagNo = 0;
    private LinearLayout currentSelectedBranch;
    private boolean isSendEmail = false;

    private ArrayList<ConsolidateBranchObject> totalBranchObjects;
    private ArrayList<ConsolidateHoulyObject> totalHourlyObjects;
    private ArrayList<ConsolidateDailyObject> totalDailyObjects;
    private ArrayList<ConsolidateWeeklyObject> totalWeeklyObjects;
    private ArrayList<ConsolidateMonthlyObject> totalMonthlyObjects;
    private ArrayList<ConsolidateYearlyObject> totalYearlyObjects;
    private ArrayList<ConsolidateChartObject> totalChartObjects;

    private String machineIds;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    public static int mainTransparentColor = Color.argb(0,163,179, 219);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_consolidated, container, false);
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

        invisibleTopIcons();
        layEmail.setVisibility(View.VISIBLE);
        imgSendEmail.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                machineIds = "";
                for (int i = 0; i < branchObjects.size(); i++){
                    BranchObject branchObject = branchObjects.get(i);
                    if (branchObject.getStatus().equals("true")){
                        if (i == branchObjects.size()-1){
                            machineIds += branchObject.getMachineId();
                        }
                        else
                            machineIds += branchObject.getMachineId() + ",";
                    }
                }
                sendRequestToServerForEmail(EMAIL_REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
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
                startConnection();
            }
        });

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
                            sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
//                            try {
//                                Date date = format.parse(dtStart);
//                                SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
//                                String dateOne = spf.format(date);
//                                txtCalendar.setText(dateOne);
//                                selectedDate = year + "-" + month + "-" + day;
//                                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
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
                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
            }
        });

        layDaily = root.findViewById(R.id.lay_daily);
        layDaily.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layDaily.setSelected(true);
                selectedCategory = DAILY_CATEGORY;
                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
            }
        });

        layWeekly = root.findViewById(R.id.lay_weekly);
        layWeekly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layWeekly.setSelected(true);
                selectedCategory = WEEKLY_CATEGORY;
                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
            }
        });

        layMonthly = root.findViewById(R.id.lay_monthly);
        layMonthly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layMonthly.setSelected(true);
                selectedCategory = MONTHLY_CATEGORY;
                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
            }
        });

        layYearly = root.findViewById(R.id.lay_yearly);
        layYearly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layYearly.setSelected(true);
                selectedCategory = YEARLY_CATEGORY;
                sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
            }
        });
        layHourly.setSelected(true);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvBranchList = root.findViewById(R.id.rv_branch_list);
        rvBranchList.setLayoutManager(horizontalLayoutManager);
        rvBranchList.setAdapter(adapter);
        showBottomInformation();

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_consolidated);
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

        barChartConsolidate = root.findViewById(R.id.cht_consolidated);
        barChartConsolidateOne= root.findViewById(R.id.cht_consolidated_one);

//        initBarChart();

        selectedDate = EMPTY_STRING;
        selectedCategory = HOURLY_CATEGORY;

        getCurrentDate();
        sendRequestToServer(REPORTS_CONSOLIDATE, selectedCategory, selectedDate);
        return root;
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_report_consolidate));
        layEmail = getActivity().findViewById(R.id.lay_email);
        imgSendEmail = getActivity().findViewById(R.id.img_send_email);
        layEmail.setVisibility(View.GONE);
    }

    private void showBottomInformation(){
        txtShopName.setText(SHOP_NAME);
        txtShopBranch.setText(SHOP_BRANCH);
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

    private void showBranchList(){
        adapter = new ConsolidateBranchViewAdapter(getContext(), branchObjects, categoryListener);
        rvBranchList.setAdapter(adapter);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
//
    private ItemClickListener categoryListener = new ConsolidatedFragment.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
                if (branchObjects.get(position).getStatus().equals("true")){
                    branchObjects.get(position).setStatus(false);
                    totalChartObjects.get(position).setSelectStatus(false);
                }
                else{
                    branchObjects.get(position).setStatus(true);
                    totalChartObjects.get(position).setSelectStatus(true);
                }
                LinearLayout selectedButton = rvBranchList.findViewWithTag("category" + position);
                selectedButton.setSelected(!selectedButton.isSelected());
                currentSelectedTagNo = position;
                currentSelectedBranch = selectedButton;
                showBarChartValues();
        }
    };

    private void showBarChartValues(){
        ArrayList<ConsolidateChartObject> tempChartObjects = new ArrayList<>();
        if (totalChartObjects.size() != 0){
            int selectedShopCount = 0;
            for (int i = 0; i <totalChartObjects.size(); i++){
                if (totalChartObjects.get(i).getSelectStatus()){
                    selectedShopCount++;
                    tempChartObjects.add(totalChartObjects.get(i));
                }
            }
            if (selectedShopCount > 1){
                initBarChart();
                setBarChartValue1(tempChartObjects);
            }
            else if (selectedShopCount == 1){
                String [] barChartXLabelValues = new String[tempChartObjects.get(0).getBarChartObjects().size()];
                int [] barChartValues = new int[tempChartObjects.get(0).getBarChartObjects().size()];
                int [] barChartColors = new int[tempChartObjects.get(0).getBarChartObjects().size()];
                String strBranchName = tempChartObjects.get(0).getBranchName();
                for (int i = 0; i < tempChartObjects.get(0).getBarChartObjects().size(); i++){
                    barChartXLabelValues[i] = tempChartObjects.get(0).getBarChartObjects().get(i).getLabelName() + "";
                    barChartValues[i] = tempChartObjects.get(0).getBarChartObjects().get(i).getAmount();
                    barChartColors[i] = tempChartObjects.get(0).getColor();
                }
                initBarChartOneBranch();
                setBarChartValueOneBranch(barChartXLabelValues, barChartValues, barChartColors, strBranchName);
            }
            else {
                barChartConsolidate.removeAllViews();
                barChartConsolidate.clear();
                barChartConsolidateOne.removeAllViews();
                barChartConsolidateOne.clear();
            }
        }
    }

    private void initBarChart(){
        barChartConsolidate.setVisibility(View.VISIBLE);
        barChartConsolidate.setVisibility(View.GONE);
        barChartConsolidate.removeAllViews();
        barChartConsolidate.clear();
        barChartConsolidate.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChartConsolidate.setPinchZoom(false);
        barChartConsolidate.setDrawBarShadow(false);
        barChartConsolidate.setDrawGridBackground(false);

//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//        mv.setChartView(chart); // For bounds control
//        barChartItemSold.setMarker(mv); // Set the marker to the chart;

        Legend l = barChartConsolidate.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
//        l.setTypeface(tfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = barChartConsolidate.getXAxis();
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

        YAxis leftAxis = barChartConsolidate.getAxisLeft();
//        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        barChartConsolidate.getAxisRight().setEnabled(false);
        barChartConsolidate.animateY(500);
    }

    private void setBarChartValue1(ArrayList<ConsolidateChartObject> barChartArray){

        barChartConsolidate.setVisibility(View.VISIBLE);
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
            String strLabel = barChartArray.get(i).getBranchName();
            int color = barChartArray.get(i).getColor();
            for (int j = 0; j < barChartObjects.size(); j++) {
                BarChartObject tempValue = barChartObjects.get(j);
                values1.add(new BarEntry(tempValue.getLabelName(), tempValue.getAmount()));
            }

            BarDataSet set1;
            if (barChartConsolidate.getData() != null && barChartConsolidate.getData().getDataSetCount() > 0) {
                set1 = (BarDataSet) barChartConsolidate.getData().getDataSetByIndex(i);
                set1.setValues(values1);
                barChartConsolidate.getData().notifyDataChanged();
                barChartConsolidate.notifyDataSetChanged();
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
        barChartConsolidate.setData(data);

        barChartConsolidate.setFitBars(true);
        XAxis xAxis = barChartConsolidate.getXAxis();
        xAxis.setLabelCount(groupCount);

        // specify the width each bar should have
        barChartConsolidate.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        barChartConsolidate.getXAxis().setAxisMinimum(startValue);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        barChartConsolidate.getXAxis().setAxisMaximum(startValue + barChartConsolidate.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        barChartConsolidate.groupBars(startValue, groupSpace, barSpace);
        barChartConsolidate.invalidate();
    }

    private void initBarChartOneBranch() {
        barChartConsolidateOne.setVisibility(View.VISIBLE);
        barChartConsolidate.setVisibility(View.GONE);
        barChartConsolidateOne.removeAllViews();
        barChartConsolidateOne.clear();


        barChartConsolidateOne.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChartConsolidateOne.setMaxVisibleValueCount(60);
        barChartConsolidateOne.setTouchEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChartConsolidateOne.setPinchZoom(false);
        barChartConsolidateOne.setAutoScaleMinMaxEnabled(false);

        barChartConsolidateOne.setDrawBarShadow(false);
        barChartConsolidateOne.setDrawGridBackground(false);

        Legend l = barChartConsolidateOne.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
//        l.setTypeface(tfLight);
        l.setYOffset(0f);
        l.setXOffset(10f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);

        XAxis xAxis = barChartConsolidateOne.getXAxis();
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
        YAxis leftAxis = barChartConsolidateOne.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChartConsolidateOne.getAxisRight();
        rightAxis.setEnabled(false);

        barChartConsolidateOne.animateY(500);
        barChartConsolidateOne.getLegend().setEnabled(false);
    }

    private void setBarChartValueOneBranch(String [] barChartXValues, int [] barChartValues, int[] varColors, String strLabel) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();

        for (int i = 0; i < barChartValues.length; i++){
            yVals1.add(new BarEntry(Float.parseFloat(barChartXValues[i]), barChartValues[i]));
        }
        BarDataSet set1;

        if (barChartConsolidateOne.getData() != null && barChartConsolidateOne.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChartConsolidateOne.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChartConsolidateOne.getData().notifyDataChanged();
            barChartConsolidateOne.notifyDataSetChanged();
            dataSets.add(set1);
        } else {
            set1 = new BarDataSet(yVals1, strLabel);
            set1.setColors(varColors);
            set1.setDrawValues(false);
            dataSets.add(set1);
        }

        BarData data = new BarData(dataSets);
        data.setValueFormatter(new LargeValueFormatter());
        barChartConsolidateOne.setData(data);

        barChartConsolidateOne.setFitBars(true);
        XAxis xAxis = barChartConsolidateOne.getXAxis();
        xAxis.setLabelCount(barChartXValues.length);

        barChartConsolidateOne.invalidate();
    }

    private void sendRequestToServer(int sqlNo, String categoryName, String selectedDate){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.POST);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_request_uuids);
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
                        String uuids = responseData.getString("uuids");
                        strUUIDS = uuids;
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

    private void sendRequestToServerForEmail(int sqlNo, String categoryName, String selectedDate){
        if (machineIds.equals("")){
            showToast("Please select shop");
        }
        else{
            HttpCall httpCallPost = new HttpCall();
            httpCallPost.setMethodtype(HttpCall.POST);
            String url = "\n" + getServerUrl() + getString(R.string.str_url_request_email_uuid);
            httpCallPost.setUrl(url);
            HashMap<String,String> paramsPost = new HashMap<>();
            paramsPost.put("user_id", Global.userId + "");
            paramsPost.put("unique_id", Global.userUniqueId);
            paramsPost.put("machine_id", machineIds);
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
    }

    @SuppressLint("StaticFieldLeak")
    private void getConsolidateResultData(String uniqueIds){
        HttpCall httpCallPost = new HttpCall();
        httpCallPost.setMethodtype(HttpCall.GET);
        String url = "\n" + getServerUrl() + getString(R.string.str_url_get_consolidate_data);
        httpCallPost.setUrl(url);
        HashMap<String,String> paramsPost = new HashMap<>();
        paramsPost.put("requestUniqueIDs", uniqueIds);
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

                        branchObjects = new ArrayList<>();
                        totalBranchObjects = new ArrayList<>();
                        totalHourlyObjects = new ArrayList<>();
                        totalDailyObjects = new ArrayList<>();
                        totalWeeklyObjects = new ArrayList<>();
                        totalMonthlyObjects = new ArrayList<>();
                        totalYearlyObjects = new ArrayList<>();
                        totalChartObjects = new ArrayList<>();
                        if (responseData.length() == 0 || responseData == null){
                            connectionEmpty();
                        }
                        else {
                            for (int i = 0; i < responseData.length(); i++){
                                JSONObject jsonObject = responseData.getJSONObject(i);
                                BranchObject consolidateBranchObject = new BranchObject();
                                ConsolidateChartObject consolidateChartObject = new ConsolidateChartObject();
                                ArrayList<BarChartObject> tempBarChartValues = new ArrayList<>();
                                String strShopName = jsonObject.getString("shopName");
                                String strBranchName = jsonObject.getString("branch");
                                String strMachineId = jsonObject.getString("machineId");
                                JSONArray tempResult = jsonObject.getJSONArray("data");
                                consolidateBranchObject.setBranchName(strBranchName);
                                consolidateBranchObject.setShopName(strShopName);
                                consolidateBranchObject.setMachineId(strMachineId);
                                consolidateBranchObject.setNo(i);
                                consolidateBranchObject.setStatus(true);
                                branchObjects.add(consolidateBranchObject);

                                if (selectedCategory.equals(HOURLY_CATEGORY)){
                                    for (int j = 0; j < tempResult.length(); j++){
                                        JSONArray tempArray = tempResult.getJSONArray(j);
                                        ConsolidateHoulyObject HourlyObject = new ConsolidateHoulyObject();
                                        HourlyObject.setShopName(strShopName);
                                        HourlyObject.setBranchName(strBranchName);
                                        HourlyObject.setDateTime(tempArray.getString(0));
                                        HourlyObject.setWeekday(tempArray.getString(1));
                                        HourlyObject.setTime(tempArray.getInt(2));
                                        HourlyObject.setAmount(tempArray.getInt(3));
                                        totalHourlyObjects.add(HourlyObject);

                                        BarChartObject barChartObject = new BarChartObject();
                                        barChartObject.setLabelName(tempArray.getInt(2));
                                        barChartObject.setAmount(tempArray.getInt(3));
                                        tempBarChartValues.add(barChartObject);
                                    }
                                }
                                else if (selectedCategory.equals(DAILY_CATEGORY)){
                                    for (int j = 0; j < tempResult.length(); j++){
                                        JSONArray tempArray = tempResult.getJSONArray(j);
                                        ConsolidateDailyObject DailyObject = new ConsolidateDailyObject();
                                        DailyObject.setShopName(strShopName);
                                        DailyObject.setBranchName(strBranchName);
                                        DailyObject.setDateTime(tempArray.getString(0));
                                        DailyObject.setMonth(tempArray.getString(1));
                                        DailyObject.setDay(tempArray.getInt(2));
                                        DailyObject.setAmount(tempArray.getInt(3));
                                        totalDailyObjects.add(DailyObject);

                                        BarChartObject barChartObject = new BarChartObject();
                                        barChartObject.setLabelName(tempArray.getInt(2));
                                        barChartObject.setAmount(tempArray.getInt(3));
                                        tempBarChartValues.add(barChartObject);
                                    }
                                }
                                else if (selectedCategory.equals(WEEKLY_CATEGORY)){
                                    for (int j = 0; j < tempResult.length(); j++){
                                        JSONArray temArray = tempResult.getJSONArray(j);
                                        ConsolidateWeeklyObject WeeklyObject = new ConsolidateWeeklyObject();
                                        WeeklyObject.setShopName(strShopName);
                                        WeeklyObject.setBranchName(strBranchName);
                                        WeeklyObject.setYear(temArray.getString(0));
                                        WeeklyObject.setMonthName(temArray.getString(1));
                                        WeeklyObject.setMonthNo(temArray.getInt(2));
                                        WeeklyObject.setWeekNo(temArray.getInt(3));
                                        WeeklyObject.setAmount(temArray.getInt(4));
                                        totalWeeklyObjects.add(WeeklyObject);

                                        BarChartObject barChartObject = new BarChartObject();
                                        barChartObject.setLabelName(temArray.getInt(3));
                                        barChartObject.setAmount(temArray.getInt(4));
                                        tempBarChartValues.add(barChartObject);
                                    }
                                }
                                else if (selectedCategory.equals(MONTHLY_CATEGORY)){
                                    for (int j = 0; j < tempResult.length(); j++){
                                        JSONArray tempArray = tempResult.getJSONArray(j);
                                        ConsolidateMonthlyObject MonthlyObject = new ConsolidateMonthlyObject();
                                        MonthlyObject.setShopName(strShopName);
                                        MonthlyObject.setBranchName(strBranchName);
                                        MonthlyObject.setYear(tempArray.getString(0));
                                        MonthlyObject.setMonthName(tempArray.getString(1));
                                        MonthlyObject.setMonthNo(tempArray.getInt(2));
                                        MonthlyObject.setAmount(tempArray.getInt(3));
                                        totalMonthlyObjects.add(MonthlyObject);

                                        BarChartObject barChartObject = new BarChartObject();
                                        barChartObject.setLabelName(tempArray.getInt(2));
                                        barChartObject.setAmount(tempArray.getInt(3));
                                        tempBarChartValues.add(barChartObject);
                                    }
                                }
                                else if (selectedCategory.equals(YEARLY_CATEGORY)){
                                    for (int j = 0; j < tempResult.length(); j++){
                                        JSONArray tempArray = tempResult.getJSONArray(j);
                                        ConsolidateYearlyObject YearlyObject = new ConsolidateYearlyObject();
                                        YearlyObject.setShopName(strShopName);
                                        YearlyObject.setBranchName(strBranchName);
                                        YearlyObject.setYear(tempArray.getString(0));
                                        YearlyObject.setAmount(tempArray.getInt(1));
                                        totalYearlyObjects.add(YearlyObject);

                                        BarChartObject barChartObject = new BarChartObject();
                                        barChartObject.setLabelName(tempArray.getInt(0));
                                        barChartObject.setAmount(tempArray.getInt(1));
                                        tempBarChartValues.add(barChartObject);
                                    }
                                }

                                consolidateChartObject.setNo(i);
                                consolidateChartObject.setShopName(strShopName);
                                consolidateChartObject.setBranchName(strBranchName);
                                consolidateChartObject.setBarChartObjects(tempBarChartValues);
                                consolidateChartObject.setSelectStatus(true);
                                switch (i % 8){
                                    case 0:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_00));
                                        break;
                                    case 1:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_01));
                                        break;
                                    case 2:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_02));
                                        break;
                                    case 3:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_03));
                                        break;
                                    case 4:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_04));
                                        break;
                                    case 5:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_05));
                                        break;
                                    case 6:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_06));
                                        break;
                                    case 7:
                                        consolidateChartObject.setColor(getResources().getColor(R.color.clr_graph_07));
                                        break;
                                }

                                totalChartObjects.add(consolidateChartObject);
                            }
                            connectionSuccess();
                        }
                    }
                    else if (responseCode == Global.RESULT_FAILED) {
                        showConnectFailedUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showConnectFailedUI();
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
                        showConnectFailedUI();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showConnectFailedUI();

                }

            }
        }.execute(httpCallPost);
    }

    private void startConnection() {
        showConnectingUI();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isSendEmail)
                    getEmailSendStatus(strUUID);
                else {
                    getConsolidateResultData(strUUIDS);
                }

            }
        }, SHOP_PAGE_DELAY_TIME);
    }

    private void connectionSuccess(){
        barChartConsolidate.removeAllViews();
        showConnectSuccessUI();
        showBranchList();
        showBarChartValues();

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

    private void connectionEmpty(){
        showConnectSuccessUI();
        barChartConsolidateOne.clear();
        barChartConsolidate.clear();
    }

}