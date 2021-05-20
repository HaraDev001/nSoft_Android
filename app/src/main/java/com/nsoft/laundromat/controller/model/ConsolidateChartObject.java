package com.nsoft.laundromat.controller.model;


import java.util.ArrayList;

public class ConsolidateChartObject {
    private String shopName;
    private String branchName;
    private int no;
    private ArrayList<BarChartObject> barChartObjects;
    private int color;
    private boolean selectStatus;

    public ConsolidateChartObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getShopName(){return shopName;}
    public void setShopName(String shopName){this.shopName = shopName;}

    public String getBranchName(){return branchName;}
    public void setBranchName(String branchName){this.branchName = branchName;}

    public ArrayList<BarChartObject> getBarChartObjects(){return barChartObjects;}
    public void setBarChartObjects(ArrayList<BarChartObject> barChartObjects) {
        this.barChartObjects = barChartObjects;
    }

    public int getColor(){return color;}
    public void setColor(int color){this.color = color;}

    public boolean getSelectStatus(){return selectStatus;}
    public void setSelectStatus(boolean selectStatus){this.selectStatus = selectStatus;}

}
