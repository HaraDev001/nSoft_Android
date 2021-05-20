package com.nsoft.laundromat.controller.report.ui.xreading;

import android.graphics.Color;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.ReportDailyObject;
import com.nsoft.laundromat.controller.model.ReportHoulyObject;
import com.nsoft.laundromat.controller.model.ReportObject;
import com.nsoft.laundromat.controller.model.ReportWeeklyObject;
import com.nsoft.laundromat.utils.dialog.CustomProgress;
import com.nsoft.laundromat.utils.network.HttpCall;
import com.nsoft.laundromat.utils.network.HttpRequest;
import com.ycuwq.datepicker.date.DatePickerDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.REPORTS_SALES;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.reportObjectArrayList;

public class XReadingFragment extends BaseFragment implements OnChartValueSelectedListener {

    private ImageView imgHome;
    private ListView lstReport;
    private ImageView imgCalendar;
    private LinearLayout layHourly;
    private LinearLayout layDaily;
    private LinearLayout layWeekly;
    private LinearLayout layMonthly;
    private LinearLayout layYearly;
    private TextView txtCalendar;
    private LineChart chtSoldReport;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShopName;
    private TextView txtShopBranch;

    private static String HOURLY_CATEGORY = "Hourly";
    private static String DAILY_CATEGORY = "Daily";
    private static String WEEKLY_CATEGORY = "Weekly";
    private static String MONTHLY_CATEGORY = "Monthly";
    private static String YEARLY_CATEGORY = "Yearly";

    private String selectedDate = "";
    private String selectedCategory = "";
    private String strUUID = "";

    private static int MONDAY = 0;
    private int TUESDAY = 1;
    private int WEDNESDAY = 2;
    private int THURSDAY = 3;
    private int FRIDAY = 4;
    private int SATURDAY = 5;
    private int SUNDAY = 6;

    private final String strMonday = "Monday";
    private final String strTuesday = "Tuesday";
    private final String strWednesday = "Wednesday";
    private final String strThursday = "Thursday";
    private final String strFriday= "Friday";
    private final String strSaturday = "Saturday";
    private final String strSunday= "Sunday";

    String[] HourlyXLabelValues = new String[]{
            "6", "7", "8", "9", "10", "11","12", "13", "14","15", "16", "17", "18","19", "20", "21", "22", "23", "24"
    };
    String[] DailyXLabelValues = new String[]{
            "1", "", "3", "", "5", "","7", "", "9","", "11", "", "13","", "15", "", "17", "", "19", "", "21", "", "23","", "25", "", "27", "", "29", "", "31"
    };
    String[] WeeklyXLabelValues = new String[]{
            "1", "2", "3", "4", "5", "6","7", "8", "9","10", "11", "12", "13","14", "15", "16", "17", "18", "19", "20"
    };
    String[] MothlyXLabelValues = new String[]{
            "May", "June", "July", "August", "September", "October"
    };
    String[] YearlyXLabelValues = new String[]{
            "2018", "2019"
    };
    private ArrayList<ReportHoulyObject> totalHourlyObjects;
    private ArrayList<ReportHoulyObject> mondayHourlyObjects;
    private ArrayList<ReportHoulyObject> tuesdayHourlyObjects;
    private ArrayList<ReportHoulyObject> wednesdayHourlyObjects;
    private ArrayList<ReportHoulyObject> thursdayHourlyObjects;
    private ArrayList<ReportHoulyObject> fridayHourlyObjects;
    private ArrayList<ReportHoulyObject> saturdayHourlyObjects;
    private ArrayList<ReportHoulyObject> sundayHourlyObjects;

    private ArrayList<ReportDailyObject> totalDailyObjects;
    private ArrayList<ReportDailyObject> firstDailyObjects;
    private ArrayList<ReportDailyObject> secondDailyObjects;
    private ArrayList<ReportDailyObject> thirdDailyObjects;
    private ArrayList<ReportDailyObject> fourthDailyObjects;
    private ArrayList<ReportDailyObject> fifthDailyObjects;
    private String firstMonthName = "";
    private String secondMonthName = "";
    private String thirdMonthName = "";
    private String fourthMonthName = "";
    private String fifthMonthName = "";
    private int monthCount = 0;

    private ArrayList<ReportWeeklyObject> totalWeeklyObjects;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_xreading, container, false);
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

        lstReport = root.findViewById(R.id.lst_sale_item);
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
                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                            try {
                                Date date = format.parse(dtStart);
                                SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
                                String dateOne = spf.format(date);
                                txtCalendar.setText(dateOne);
                                selectedDate = year + "-" + month + "-" + day;
                                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
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
                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
            }
        });

        layDaily = root.findViewById(R.id.lay_daily);
        layDaily.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layDaily.setSelected(true);
                selectedCategory = DAILY_CATEGORY;
                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
            }
        });

        layWeekly = root.findViewById(R.id.lay_weekly);
        layWeekly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layWeekly.setSelected(true);
                selectedCategory = WEEKLY_CATEGORY;
                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
            }
        });

        layMonthly = root.findViewById(R.id.lay_monthly);
        layMonthly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layMonthly.setSelected(true);
                selectedCategory = MONTHLY_CATEGORY;
                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
            }
        });

        layYearly = root.findViewById(R.id.lay_yearly);
        layYearly.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                makeCategoryNoSelected();
                layYearly.setSelected(true);
                selectedCategory = YEARLY_CATEGORY;
                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
            }
        });
        layHourly.setSelected(true);

        chtSoldReport = root.findViewById(R.id.cht_sold_report);
        chtSoldReport.removeAllViews();

        showReportList();
        initSoldChart(16,  HourlyXLabelValues);
        selectedDate = EMPTY_STRING;
        selectedCategory = HOURLY_CATEGORY;

        showBottomInformation();
        sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);

        return root;
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

    private void showReportList(){
        reportObjectArrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
            ReportObject reportObject = new ReportObject();
            reportObject.setAmount(100 + "");
            reportObject.setNo(i);
            reportObject.setTitle("title" + i);
            reportObjectArrayList.add(reportObject);
        }
        ReportAdapter reportAdapter = new ReportAdapter(getContext(), R.layout.item_sales_report, reportObjectArrayList);
        lstReport.setAdapter(reportAdapter);
    }

    // -------------------------------
    // ---- Sold Report Chart ----
    // -------------------------------
    public void initSoldChart(int xlableCount, String[] xlabelValues) {
        chtSoldReport.setVisibility(View.VISIBLE);
        chtSoldReport.removeAllViews();
        chtSoldReport.setOnChartValueSelectedListener(this);
        // enable description text
        chtSoldReport.getDescription().setEnabled(false);
        // enable touch gestures
        chtSoldReport.setTouchEnabled(false);
        // enable scaling and dragging
        chtSoldReport.setDragEnabled(true);
        chtSoldReport.setScaleEnabled(true);
        chtSoldReport.setDrawGridBackground(false);
        // if disabled, scaling can be done on x- and y-axis separately
        chtSoldReport.setPinchZoom(false);
        chtSoldReport.setAutoScaleMinMaxEnabled(false);
        // set an alternative background color
        chtSoldReport.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        chtSoldReport.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chtSoldReport.getLegend();

        // modify the legend ...
        l.setEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setTextColor(getResources().getColor(R.color.colorPrimary));
//        l.setTypeface(mTfLight);
        l.setXOffset(1f);

        XAxis xl = chtSoldReport.getXAxis();

        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        xl.setTextColor(getResources().getColor(R.color.colorPrimary));
        xl.setLabelCount(xlableCount);
        xl.setEnabled(true);

        final String[] XLabelValues = xlabelValues;
        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return XLabelValues[(int) value];
            }
        });

        YAxis leftAxis = chtSoldReport.getAxisLeft();
//        leftAxis.setAxisMaximum(ymax);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setInverted(false);
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = chtSoldReport.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addHourlyDataEntry(int addValue, int weekday) {
        LineData data = chtSoldReport.getData();
        switch (weekday) {
            case 0:
                if (data != null) {
                    ILineDataSet set = data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_00), "mon");
                        data.addDataSet(set);
                    }
                    float asa = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa ), 0);
                    data.notifyDataChanged();
                    break;
                }
            case 1:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(1);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_01), "tue");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 1);
                    data.notifyDataChanged();
                }
                break;
            case 2:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(2);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_02), "wed");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 2);
                    data.notifyDataChanged();
                }
                break;
            case 3:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(3);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_03), "thu");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 3);
                    data.notifyDataChanged();
                }
                break;
            case 4:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(4);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_04), "fri");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 4);
                    data.notifyDataChanged();
                }
                break;
            case 5:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(5);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_05), "sat");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 5);
                    data.notifyDataChanged();
                }
                break;
            case 6:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(6);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_06), "sun");
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 6);
                    data.notifyDataChanged();
                }
                break;
        }
        // let the chart know it's data has changed
        chtSoldReport.notifyDataSetChanged();
        // limit the number of visible entries
        chtSoldReport.setVisibleXRangeMaximum(100);
        chtSoldReport.moveViewToX(data.getEntryCount());

    }

    private void addDailyDataEntry(int addValue, int monthFlag, String monthName) {
        LineData data = chtSoldReport.getData();
        switch (monthFlag) {
            case 0:
                if (data != null) {
                    ILineDataSet set = data.getDataSetByIndex(0);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_00), monthName);
                        data.addDataSet(set);
                    }
                    float asa = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa ), 0);
                    data.notifyDataChanged();
                    break;
                }
            case 1:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(1);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_01), monthName);
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 1);
                    data.notifyDataChanged();
                }
                break;
            case 2:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(2);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_02), monthName);
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 2);
                    data.notifyDataChanged();
                }
                break;
            case 3:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(3);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_03), monthName);
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 3);
                    data.notifyDataChanged();
                }
                break;
            case 4:
                if (data != null){
                    ILineDataSet set = data.getDataSetByIndex(4);
                    if (set == null) {
                        set = createSet(getResources().getColor(R.color.clr_graph_04), monthName);
                        data.addDataSet(set);
                    }
                    float asa1 = (float) addValue;
                    data.addEntry(new Entry(set.getEntryCount(),asa1 ), 4);
                    data.notifyDataChanged();
                }
                break;
        }
        // let the chart know it's data has changed
        chtSoldReport.notifyDataSetChanged();
        // limit the number of visible entries
        chtSoldReport.setVisibleXRangeMaximum(100);
        chtSoldReport.moveViewToX(data.getEntryCount());

    }

    private void sendRequestToServer(int sqlNo, String categoryName, String selectedDate){
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

    private void getSoldReportData(String uniqueId){
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
                        connectionSuccess();
                        if (selectedCategory.equals(HOURLY_CATEGORY)){
                            totalHourlyObjects = new ArrayList<>();
                            for (int i = 0; i < tempResult.length(); i++){
                                JSONArray tempArray = tempResult.getJSONArray(i);
                                ReportHoulyObject reportHoulyObject = new ReportHoulyObject();
                                reportHoulyObject.setDateTime(tempArray.getString(0));
                                reportHoulyObject.setWeekday(tempArray.getString(1));
                                reportHoulyObject.setTime(tempArray.getInt(2));
                                reportHoulyObject.setAmount(tempArray.getInt(3));
                                totalHourlyObjects.add(reportHoulyObject);
                            }
                            if (totalHourlyObjects.size() > 0){
                                showHourlyInformation();
                            }
                        }
                        else if (selectedCategory.equals(DAILY_CATEGORY)){
                            totalDailyObjects = new ArrayList<>();
                            firstDailyObjects = new ArrayList<>();
                            secondDailyObjects = new ArrayList<>();
                            thirdDailyObjects = new ArrayList<>();
                            fourthDailyObjects = new ArrayList<>();
                            fifthDailyObjects = new ArrayList<>();
                            String tempMonthName = "";
                            int monthFlag = 0;
                            for (int i = 0; i < tempResult.length(); i++){
                                JSONArray tempArray = tempResult.getJSONArray(i);
                                ReportDailyObject reportDailyObject = new ReportDailyObject();
                                reportDailyObject.setWeekday(tempArray.getString(0));
                                reportDailyObject.setMonth(tempArray.getString(1));
                                reportDailyObject.setDay(tempArray.getInt(2));
                                reportDailyObject.setWeekNo(tempArray.getString(3));
                                reportDailyObject.setWeekday(tempArray.getString(4));
                                reportDailyObject.setAmount(tempArray.getInt(5));
                                if (!tempMonthName.equals(tempArray.getString(1))){
                                    monthFlag++;
                                    tempMonthName = tempArray.getString(1);
                                    monthCount = monthFlag;
                                }
                                if (monthFlag == 1){
                                    firstMonthName = tempMonthName;
                                    firstDailyObjects.add(reportDailyObject);
                                }
                                else if (monthFlag ==2){
                                    secondMonthName = tempMonthName;
                                    secondDailyObjects.add(reportDailyObject);
                                }
                                else if (monthFlag == 3){
                                    thirdMonthName = tempMonthName;
                                    thirdDailyObjects.add(reportDailyObject);
                                }
                                else if (monthFlag == 4){
                                    fourthMonthName = tempMonthName;
                                    fourthDailyObjects.add(reportDailyObject);
                                }
                                else if (monthFlag == 5){
                                    fifthMonthName = tempMonthName;
                                    fifthDailyObjects.add(reportDailyObject);
                                }
                                totalDailyObjects.add(reportDailyObject);
                            }
                            if (totalDailyObjects.size() > 0){
                                showDailyInformation();
                            }
                        }
                        else if (selectedCategory.equals(WEEKLY_CATEGORY)){
                            totalWeeklyObjects = new ArrayList<>();
                            for (int i = 0; i < tempResult.length(); i++){
                                JSONArray temArray = tempResult.getJSONArray(i);
                                ReportWeeklyObject reportWeeklyObject = new ReportWeeklyObject();
                                reportWeeklyObject.setWeekNo(temArray.getInt(0));
                                reportWeeklyObject.setAmount(temArray.getInt(1));
                                totalWeeklyObjects.add(reportWeeklyObject);
                            }
                            showWeeklyInformation();
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

    private void showHourlyInformation(){
        initSoldChart(16,  HourlyXLabelValues);
        mondayHourlyObjects = new ArrayList<>();
        tuesdayHourlyObjects = new ArrayList<>();
        wednesdayHourlyObjects = new ArrayList<>();
        thursdayHourlyObjects = new ArrayList<>();
        fridayHourlyObjects = new ArrayList<>();
        saturdayHourlyObjects = new ArrayList<>();
        sundayHourlyObjects = new ArrayList<>();
        for (int i = 0; i < totalHourlyObjects.size(); i++){
            ReportHoulyObject tempObj = totalHourlyObjects.get(i);
            String weekday = tempObj.getWeekday();
            if (weekday.equals(strMonday)){
                mondayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strTuesday)){
                tuesdayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strWednesday)){
                wednesdayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strThursday)){
                thursdayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strFriday)){
                fridayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strSaturday)){
                saturdayHourlyObjects.add(tempObj);
            }
            else if (weekday.equals(strSunday)){
                sundayHourlyObjects.add(tempObj);
            }
        }
        showHourlyToGraph(mondayHourlyObjects, MONDAY);
        showHourlyToGraph(tuesdayHourlyObjects, TUESDAY);
        showHourlyToGraph(wednesdayHourlyObjects, WEDNESDAY);
        showHourlyToGraph(thursdayHourlyObjects, THURSDAY);
        showHourlyToGraph(fridayHourlyObjects, FRIDAY);
        showHourlyToGraph(saturdayHourlyObjects, SATURDAY);
        showHourlyToGraph(sundayHourlyObjects, SUNDAY);
    }

    private void showDailyInformation(){
        switch (monthCount){
            case 0:
                break;
            case 1:
                initSoldChart(31, DailyXLabelValues);
                showDailyToGraph(firstDailyObjects, 0, firstMonthName);
                break;
            case 2:
                initSoldChart(31, DailyXLabelValues);
                showDailyToGraph(firstDailyObjects, 0, firstMonthName);
                showDailyToGraph(secondDailyObjects, 1, secondMonthName);
                break;
            case 3:
                initSoldChart(31, DailyXLabelValues);
                showDailyToGraph(firstDailyObjects, 0, firstMonthName);
                showDailyToGraph(secondDailyObjects, 1, secondMonthName);
                showDailyToGraph(thirdDailyObjects, 2, thirdMonthName);
                break;
            case 4:
                initSoldChart(31, DailyXLabelValues);
                showDailyToGraph(firstDailyObjects, 0, firstMonthName);
                showDailyToGraph(secondDailyObjects, 1, secondMonthName);
                showDailyToGraph(thirdDailyObjects, 2, thirdMonthName);
                showDailyToGraph(fourthDailyObjects, 3, fourthMonthName);
                break;
            case 5:
                initSoldChart(31, DailyXLabelValues);
                showDailyToGraph(firstDailyObjects, 0, firstMonthName);
                showDailyToGraph(secondDailyObjects, 1, secondMonthName);
                showDailyToGraph(thirdDailyObjects, 2, thirdMonthName);
                showDailyToGraph(fourthDailyObjects, 3, fourthMonthName);
                showDailyToGraph(fifthDailyObjects, 4, fifthMonthName);
                break;
        }
    }

    private void showWeeklyInformation(){
        int weekCount = totalWeeklyObjects.size();
        int firstWeek = totalWeeklyObjects.get(0).getWeekNo();
        String[] xlabels =new String[weekCount];
        for (int i = 0; i < totalWeeklyObjects.size(); i++ ){
            xlabels[i] = totalWeeklyObjects.get(i).getWeekNo() + "";
        }
        initSoldChart(weekCount, xlabels);
        for (int i = 0; i<totalWeeklyObjects.size(); i++){
            int addValue = totalWeeklyObjects.get(i).getAmount();
            addDailyDataEntry(addValue, 0, "week");
        }
    }

    private void showHourlyToGraph(ArrayList<ReportHoulyObject> reportHoulyObjects, int weekdayType){
        for (int i = 6; i < 25; i++){
            int value = 0;
            for (int j = 0; j < reportHoulyObjects.size(); j++){
                ReportHoulyObject temp = reportHoulyObjects.get(j);
                if (i == temp.getTime()){
                    value = temp.getAmount();
                    break;
                }
            }
            addHourlyDataEntry(value, weekdayType);
        }
    }

    private void showDailyToGraph(ArrayList<ReportDailyObject> reportDailyObjects, int monthType, String monthName){
        for (int i = 1; i < 32; i++){
            int value = 0;
            for (int j = 0; j < reportDailyObjects.size(); j++){
                ReportDailyObject temp = reportDailyObjects.get(j);
                if (i == temp.getDay()){
                    value = temp.getAmount();
                    break;
                }
            }
            addDailyDataEntry(value, monthType, monthName);
        }
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
                    getSoldReportData(strUUID);
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
        chtSoldReport.removeAllViews();
        stopConnectionTimer();
        showConnectSuccessUI();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}