package com.nsoft.laundromat.controller.model;


public class ConsolidateMonthlyObject {
    private String shopName;
    private String branchName;
    private String year;
    private String monthName;
    private int monthNo;
    private int amount;

    public ConsolidateMonthlyObject() {

    }

    public String getShopName(){return shopName;}
    public void setShopName(String shopName){this.shopName = shopName;}

    public String getBranchName(){return branchName;}
    public void setBranchName(String branchName){this.branchName = branchName;}

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public int getMonthNo(){return monthNo;}
    public void setMonthNo(int monthNo){this.monthNo = monthNo;}

    public String getMonthName(){return monthName;}
    public void setMonthName(String monthName){this.monthName = monthName;}

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
