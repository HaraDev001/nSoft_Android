package com.nsoft.laundromat.controller.model;


public class ReportHoulyObject {
    private int no;
    private String dateTime;
    private String weekday;
    private int time;
    private int amount;

    public ReportHoulyObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getWeekday() { return weekday; }
    public void setWeekday(String weekday) { this.weekday = weekday; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
