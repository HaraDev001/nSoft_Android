package com.nsoft.laundromat.controller.model;


public class ReportDailyObject {
    private int no;
    private String weekday;
    private String month;
    private String weekNo;
    private int day;
    private int amount;

    public ReportDailyObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getWeekday() { return weekday; }
    public void setWeekday(String weekday) { this.weekday = weekday; }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getWeekNo(){ return weekNo; }
    public void setWeekNo(String weekNo) {this.weekNo = weekNo;}
}
