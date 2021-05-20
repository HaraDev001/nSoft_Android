package com.nsoft.laundromat.controller.model;


public class ItemSoldHoulyObject {
    private String itemName;
    private String dateTime;
    private String weekday;
    private int time;
    private int amount;
    private int itemCount;

    public ItemSoldHoulyObject() {

    }

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getWeekday() { return weekday; }
    public void setWeekday(String weekday) { this.weekday = weekday; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
