package com.nsoft.laundromat.controller.model;


public class ItemSoldMonthlyObject {
    private int monthNo;
    private String itemName;
    private String monthName;
    private int amount;
    private int itemCount;

    public ItemSoldMonthlyObject() {

    }

    public int getMonthNo(){return monthNo;}
    public void setMonthNo(int monthNo){this.monthNo = monthNo;}

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
