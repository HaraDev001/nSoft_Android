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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nsoft.laundromat.R;
import com.nsoft.laundromat.common.Global;
import com.nsoft.laundromat.common.OnMultiClickListener;
import com.nsoft.laundromat.controller.base.BaseFragment;
import com.nsoft.laundromat.controller.model.ReportDailyObject;
import com.nsoft.laundromat.controller.model.ReportHoulyObject;
import com.nsoft.laundromat.controller.model.ReportMonthlyObject;
import com.nsoft.laundromat.controller.model.ReportObject;
import com.nsoft.laundromat.controller.model.ReportWeeklyObject;
import com.nsoft.laundromat.controller.model.ReportYearlyObject;
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
import java.util.Timer;
import java.util.TimerTask;

import static com.nsoft.laundromat.common.Global.EMAIL_REPORTS_SALES;
import static com.nsoft.laundromat.common.Global.EMPTY_STRING;
import static com.nsoft.laundromat.common.Global.NOTICE_GET;
import static com.nsoft.laundromat.common.Global.REPORTS_SALES;
import static com.nsoft.laundromat.common.Global.SHOP_BRANCH;
import static com.nsoft.laundromat.common.Global.SHOP_NAME;
import static com.nsoft.laundromat.common.Global.connectCount;
import static com.nsoft.laundromat.common.Global.connectionTimer;
import static com.nsoft.laundromat.common.Global.reportObjectArrayList;

public class SalesReportFragment extends BaseFragment implements OnChartValueSelectedListener {
    private TextView txtPageTitle;
    private ImageView imgHome;
    private LinearLayout layEmail;
    private ImageView imgSendEmail;
    private ListView lstReport;
    private ListView lstReportThree;
    private ImageView imgCalendar;
    private LinearLayout layHourly;
    private LinearLayout layDaily;
    private LinearLayout layWeekly;
    private LinearLayout layMonthly;
    private LinearLayout layYearly;
    private TextView txtCalendar;
    private LineChart chtSoldReport;
    private BarChart barChartYearlyReport;
    private LinearLayout layMainContent;
    private LinearLayout layLoading;
    private LinearLayout layDisconnect;
    private TextView txtError;
    private Button btnTryAgain;
    private TextView txtShopName;
    private TextView txtShopBranch;
    private TextView txtListTitle;
    private TextView txtListTitleThree;
    private TextView txtListSubTitleThree;

    private LinearLayout layListItem;
    private LinearLayout layListItemThree;

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

    public static int mainColor = Color.rgb(163,179, 219);
    public static int mainTransparentColor = Color.argb(0,163,179, 219);

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
    private boolean isSendEmail = false;

    private ArrayList<ReportWeeklyObject> totalWeeklyObjects;
    private ArrayList<ReportMonthlyObject> totalMonthlyObjects;
    private ArrayList<ReportYearlyObject> totalYearlyObjects;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sales_report, container, false);
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
                sendRequestToServer(EMAIL_REPORTS_SALES, selectedCategory, selectedDate);
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
                            sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
//                            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
//                            try {
//                                Date date = format.parse(dtStart);
//                                SimpleDateFormat spf= new SimpleDateFormat("EEE, d MMM yyyy");
//                                String dateOne = spf.format(date);
//                                txtCalendar.setText(dateOne);
//                                selectedDate = year + "-" + month + "-" + day;
//                                sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);
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
        barChartYearlyReport = root.findViewById(R.id.cht_solid_yearly);
        barChartYearlyReport.setVisibility(View.GONE);

        lstReport = root.findViewById(R.id.lst_sale_item);
        lstReportThree = root.findViewById(R.id.lst_sale_item_three);
        txtListTitle = root.findViewById(R.id.txt_list_title);
        layListItem = root.findViewById(R.id.lay_list_item);
        layListItemThree = root.findViewById(R.id.lay_list_item_three);
        txtListTitleThree= root.findViewById(R.id.txt_list_title_three);
        txtListSubTitleThree = root.findViewById(R.id.txt_list_sub_title_three);

        mSlidingUpPanelLayout = root.findViewById(R.id.fra_sales_report);
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

        initSoldChart(16,  HourlyXLabelValues);
        selectedDate = EMPTY_STRING;
        selectedCategory = HOURLY_CATEGORY;

        getCurrentDate();

        showBottomInformation();
        sendRequestToServer(REPORTS_SALES, selectedCategory, selectedDate);

        return root;
    }

    public void invisibleTopIcons(){
        txtPageTitle = getActivity().findViewById(R.id.txt_page_title);
        txtPageTitle.setText(getResources().getString(R.string.menu_report_sale));
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

    private void showReportList(){
        reportObjectArrayList = new ArrayList<>();
        if (selectedCategory.equals(HOURLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_time));
            txtListTitleThree.setText(getContext().getResources().getString(R.string.str_date));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_time));
            for (int i = 0; i < totalHourlyObjects.size(); i++){
                ReportObject reportObject = new ReportObject();
                reportObject.setAmount(standardDecimalFormat(totalHourlyObjects.get(i).getAmount() + ""));
                reportObject.setNo(i);
                reportObject.setTitle(totalHourlyObjects.get(i).getWeekday());
                reportObject.setSubTitle(totalHourlyObjects.get(i).getTime() + "");
                reportObjectArrayList.add(reportObject);
            }
        }
        else if (selectedCategory.equals(DAILY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_date));
            txtListTitleThree.setText(getContext().getResources().getString(R.string.str_month));
            txtListSubTitleThree.setText(getContext().getResources().getString(R.string.str_date));
            for (int i = 0; i < totalDailyObjects.size(); i++){
                ReportObject reportObject = new ReportObject();
                reportObject.setAmount(standardDecimalFormat(totalDailyObjects.get(i).getAmount() + ""));
                reportObject.setNo(i);
                reportObject.setTitle(totalDailyObjects.get(i).getMonth());
                reportObject.setSubTitle(totalDailyObjects.get(i).getDay() + "");
                reportObjectArrayList.add(reportObject);
            }
        }
        else if (selectedCategory.equals(WEEKLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_week));
            for (int i = 0; i < totalWeeklyObjects.size(); i++){
                ReportObject reportObject = new ReportObject();
                reportObject.setAmount(standardDecimalFormat(totalWeeklyObjects.get(i).getAmount() + ""));
                reportObject.setNo(i);
                reportObject.setTitle(totalWeeklyObjects.get(i).getWeekNo() + "");
                reportObjectArrayList.add(reportObject);
            }
        }
        else if (selectedCategory.equals(MONTHLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_month));
            for (int i = 0; i < totalMonthlyObjects.size(); i++){
                ReportObject reportObject = new ReportObject();
                reportObject.setAmount(standardDecimalFormat(totalMonthlyObjects.get(i).getAmount() + ""));
                reportObject.setNo(i);
                reportObject.setTitle(totalMonthlyObjects.get(i).getYear() + " " + totalMonthlyObjects.get(i).getMonthName());
                reportObjectArrayList.add(reportObject);
            }
        }
        else if (selectedCategory.equals(YEARLY_CATEGORY)){
            txtListTitle.setText(getContext().getResources().getString(R.string.str_year));
            for (int i = 0; i < totalYearlyObjects.size(); i++){
                ReportObject reportObject = new ReportObject();
                reportObject.setAmount(standardDecimalFormat(totalYearlyObjects.get(i).getAmount() + ""));
                reportObject.setNo(i);
                reportObject.setTitle(totalYearlyObjects.get(i).getYear() + "");
                reportObjectArrayList.add(reportObject);
            }
        }
        if (selectedCategory.equals(HOURLY_CATEGORY) || selectedCategory.equals(DAILY_CATEGORY)){
            layListItemThree.setVisibility(View.VISIBLE);
            layListItem.setVisibility(View.GONE);
            ReportThreeAdapter reportThreeAdapter = new ReportThreeAdapter(getContext(), R.layout.item_sales_report_three, reportObjectArrayList);
            lstReportThree.setAdapter(reportThreeAdapter);
        }
        else {
            layListItemThree.setVisibility(View.GONE);
            layListItem.setVisibility(View.VISIBLE);
            ReportAdapter reportAdapter = new ReportAdapter(getContext(), R.layout.item_sales_report, reportObjectArrayList);
            lstReport.setAdapter(reportAdapter);
        }
    }

    // -------------------------------
    // ---- Sold Report Chart --------
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

    // -------------------------
    // ----Yearly Bar Chart ----
    // -------------------------
    private void initYearlyBarChart(final String [] xLabelValues) {

        barChartYearlyReport.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChartYearlyReport.setMaxVisibleValueCount(60);
        barChartYearlyReport.setTouchEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        barChartYearlyReport.setPinchZoom(false);
        barChartYearlyReport.setAutoScaleMinMaxEnabled(false);

        barChartYearlyReport.setDrawBarShadow(false);
        barChartYearlyReport.setDrawGridBackground(false);

        XAxis xAxis = barChartYearlyReport.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(xLabelValues.length);
        xAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setTextSize(9);
        xAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xLabelValues[(int) value];
            }
        });

        //barChartEEG.getAxisLeft().setDrawGridLines(false);
        YAxis leftAxis = barChartYearlyReport.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setInverted(false);
        leftAxis.setAxisLineColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setTextColor(getResources().getColor(R.color.colorPrimary));
        leftAxis.setAxisMinimum(0f);
        leftAxis.setInverted(false);
        leftAxis.setSpaceTop(3f);
        leftAxis.setDrawGridLines(false);

        YAxis rightAxis = barChartYearlyReport.getAxisRight();
        rightAxis.setEnabled(false);


        // add a nice and smooth animation
        barChartYearlyReport.animateY(500);

        barChartYearlyReport.getLegend().setEnabled(false);

//        setBarChartValue(0,0);
    }


    private void setBarChartValue(int[] varChartValues, int[] varColors) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i<varChartValues.length; i++){
            yVals1.add(new BarEntry(i, varChartValues[i]));
        }
        BarDataSet set1;

        if (barChartYearlyReport.getData() != null && barChartYearlyReport.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet)barChartYearlyReport.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            barChartYearlyReport.getData().notifyDataChanged();
            barChartYearlyReport.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "year");
            set1.setColors(varColors);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            barChartYearlyReport.setData(data);
            barChartYearlyReport.getData().setHighlightEnabled(false);
            barChartYearlyReport.setFitBars(true);
        }
        barChartYearlyReport.invalidate();
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
        if (sqlNo == EMAIL_REPORTS_SALES){
            isSendEmail = true;
        }
        else
            isSendEmail = false;
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
                        if (tempResult.length() == 0){
                            connectionEmpty();
                        }
                        else {
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
                            else if (selectedCategory.equals(MONTHLY_CATEGORY)){
                                totalMonthlyObjects = new ArrayList<>();
                                for (int i = 0; i < tempResult.length(); i++){
                                    JSONArray tempArray = tempResult.getJSONArray(i);
                                    ReportMonthlyObject reportMonthlyObject = new ReportMonthlyObject();
                                    reportMonthlyObject.setYear(tempArray.getInt(0));
                                    reportMonthlyObject.setMonthName(tempArray.getString(1));
                                    reportMonthlyObject.setMonthNo(tempArray.getInt(2));
                                    reportMonthlyObject.setAmount(tempArray.getInt(3));
                                    totalMonthlyObjects.add(reportMonthlyObject);
                                }
                                showMonthlyInformation();
                            }
                            else if (selectedCategory.equals(YEARLY_CATEGORY)){
                                totalYearlyObjects = new ArrayList<>();
                                for (int i = 0; i < tempResult.length(); i++){
                                    JSONArray tempArray = tempResult.getJSONArray(i);
                                    ReportYearlyObject reportYearlyObject = new ReportYearlyObject();
                                    reportYearlyObject.setYear(tempArray.getInt(0));
                                    reportYearlyObject.setAmount(tempArray.getInt(1));
                                    totalYearlyObjects.add(reportYearlyObject);
                                }
                                showYearlyInformation();
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

    private void showHourlyInformation(){
        showReportList();
        showLineChart();
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
        showReportList();
        showLineChart();
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
        showReportList();
        showLineChart();
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

    private void showMonthlyInformation(){
        showReportList();
        showLineChart();
        int monthCount = totalMonthlyObjects.size();
        int firstMonth = totalMonthlyObjects.get(0).getMonthNo();
        String[] xlabels = new String[monthCount];
        for (int i = 0; i < totalMonthlyObjects.size(); i++ ){
            xlabels[i] = totalMonthlyObjects.get(i).getMonthName().substring(0,3) + "";
        }
        initSoldChart(monthCount, xlabels);
        for (int i = 0; i < totalMonthlyObjects.size(); i++){
            int addValue = totalMonthlyObjects.get(i).getAmount();
            addDailyDataEntry(addValue, 0, "month");
        }
    }

    private void showYearlyInformation(){
        showReportList();
        showBarChart();
        int [] barChartValues = new int[totalYearlyObjects.size() * 2];
        String [] barChartXLabelValues = new String[totalYearlyObjects.size() * 2];
        int [] barChartColors = new int[totalYearlyObjects.size() * 2];
        int colorBarChart1 = getContext().getResources().getColor(R.color.clr_graph_00);
        //<<<<todo for graph color
//        int colorBarChart2 = getContext().getResources().getColor(R.color.clr_graph_01);
//        int colorBarChart3 = getContext().getResources().getColor(R.color.clr_graph_02);
//        int colorBarChart4 = getContext().getResources().getColor(R.color.clr_graph_03);
//        int colorBarChart5 = getContext().getResources().getColor(R.color.clr_graph_04);
        //>>>>
        for (int i = 0; i < totalYearlyObjects.size(); i++){
            barChartValues[2*i] = totalYearlyObjects.get(i).getAmount();
            barChartValues[2*i + 1] = 0;
            barChartXLabelValues[2*i] = totalYearlyObjects.get(i).getYear() + "";
            barChartXLabelValues[2*i + 1] = "";
            barChartColors[2*i] = colorBarChart1;
            barChartColors[2*i + 1] = mainTransparentColor;
        }

        initYearlyBarChart(barChartXLabelValues);
        setBarChartValue(barChartValues, barChartColors);
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

    private void showLineChart(){
        barChartYearlyReport.setVisibility(View.GONE);
        chtSoldReport.setVisibility(View.VISIBLE);

    }

    private void showBarChart(){
        barChartYearlyReport.setVisibility(View.VISIBLE);
        chtSoldReport.setVisibility(View.GONE);
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
                    if (isSendEmail)
                        getEmailSendStatus(strUUID);
                    else
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

    private void connectionEmpty(){
        chtSoldReport.removeAllViews();
        initSoldChart(16,  HourlyXLabelValues);
        barChartYearlyReport.removeAllViews();
        showEmptyList();
        stopConnectionTimer();
        showConnectSuccessUI();
    }

    private void showEmptyList(){
//        reportObjectArrayList = new ArrayList<>();
//        ReportObject reportObject = new ReportObject();
//        reportObject.setAmount( "");
//        reportObject.setNo(0);
//        reportObject.setTitle("");
//        reportObjectArrayList.add(reportObject);
//        ReportAdapter reportAdapter = new ReportAdapter(getContext(), R.layout.item_sales_report, reportObjectArrayList);
//        lstReport.setAdapter(reportAdapter);
        layListItem.setVisibility(View.INVISIBLE);
        layListItemThree.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
}