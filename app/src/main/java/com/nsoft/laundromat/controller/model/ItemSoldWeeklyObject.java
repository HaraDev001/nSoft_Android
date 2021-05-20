package com.nsoft.laundromat.controller.model;


public class ItemSoldWeeklyObject {
    private String itemName;
    private String monthName;
    private int week;
    private int amount;
    private int itemCount;

    public ItemSoldWeeklyObject() {

    }

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }

    public int getWeek() { return week; }
    public void setWeek(int week) { this.week = week; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
