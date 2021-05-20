package com.nsoft.laundromat.controller.model;


public class ItemSoldYearlyObject {
    private String itemName;
    private int year;
    private int amount;
    private int itemCount;

    public ItemSoldYearlyObject() {

    }

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
