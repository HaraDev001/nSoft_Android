package com.nsoft.laundromat.controller.model;


public class ConsolidateHoulyObject {
    private String shopName;
    private String branchName;
    private int no;
    private String dateTime;
    private String weekday;
    private int time;
    private int amount;

    public ConsolidateHoulyObject() {

    }

    public String getShopName(){return shopName;}
    public void setShopName(String shopName){this.shopName = shopName;}

    public String getBranchName(){return branchName;}
    public void setBranchName(String branchName){this.branchName = branchName;}

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
