package com.nsoft.laundromat.controller.model;


public class BarChartObject {
    private int labelName;
    private int amount;

    public BarChartObject() {

    }

    public int getLabelName(){return labelName;}
    public void setLabelName(int labelName){this.labelName = labelName;}

    public int getAmount(){return amount;}
    public void setAmount(int amount){this.amount = amount;}
}
