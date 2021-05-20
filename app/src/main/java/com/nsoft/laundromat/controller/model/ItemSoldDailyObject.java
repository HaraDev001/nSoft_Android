package com.nsoft.laundromat.controller.model;


public class ItemSoldDailyObject {
    private String itemName;
    private String dateTime;
    private String weekDay;
    private int day;
    private int amount;
    private int itemCount;

    public ItemSoldDailyObject() {

    }

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getDateTime(){ return dateTime; }
    public void setDateTime(String dateTime){this.dateTime = dateTime;}

    public String getWeekDay() { return weekDay; }
    public void setWeekDay(String weekDay) { this.weekDay = weekDay; }

    public int getDay() { return day; }
    public void setDay(int day) { this.day = day; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
