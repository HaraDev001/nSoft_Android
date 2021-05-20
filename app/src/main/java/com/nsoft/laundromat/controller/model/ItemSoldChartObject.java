package com.nsoft.laundromat.controller.model;


import java.util.ArrayList;

public class ItemSoldChartObject {
    private String itemName;
    private int no;
    private ArrayList<BarChartObject> barChartObjects;
    private int color;
    private boolean selectStatus;

    public ItemSoldChartObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getItemName(){return itemName;}
    public void setItemName(String itemName){this.itemName = itemName;}

    public ArrayList<BarChartObject> getBarChartObjects(){return barChartObjects;}
    public void setBarChartObjects(ArrayList<BarChartObject> barChartObjects) {
        this.barChartObjects = barChartObjects;
    }

    public int getColor(){return color;}
    public void setColor(int color){this.color = color;}

    public boolean getSelectStatus(){return selectStatus;}
    public void setSelectStatus(boolean selectStatus){this.selectStatus = selectStatus;}

}
