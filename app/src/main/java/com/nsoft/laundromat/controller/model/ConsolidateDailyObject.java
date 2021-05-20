package com.nsoft.laundromat.controller.model;


public class ConsolidateDailyObject {
    private String shopName;
    private String branchName;
    private int no;
    private String dateTime;
    private String month;
    private int day;
    private int amount;

    public ConsolidateDailyObject() {

    }

    public String getShopName(){return shopName;}
    public void setShopName(String shopName){this.shopName = shopName;}

    public String getBranchName(){return branchName;}
    public void setBranchName(String branchName){this.branchName = branchName;}

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getDateTime(){ return dateTime; }
    public void setDateTime(String dateTime){this.dateTime = dateTime;}

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
