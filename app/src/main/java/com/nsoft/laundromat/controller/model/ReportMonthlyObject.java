package com.nsoft.laundromat.controller.model;


public class ReportMonthlyObject {
    private int year;
    private String monthName;
    private int monthNo;
    private int amount;

    public ReportMonthlyObject() {

    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getMonthNo(){return monthNo;}
    public void setMonthNo(int monthNo){this.monthNo = monthNo;}

    public String getMonthName(){return monthName;}
    public void setMonthName(String monthName){this.monthName = monthName;}

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
