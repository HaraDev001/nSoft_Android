package com.nsoft.laundromat.controller.model;


public class ReportObject {
    private int no;
    private String title;
    private String subTitle;
    private String amount;

    public ReportObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubTitle(){return  subTitle;}
    public void setSubTitle(String subTitle){this.subTitle = subTitle;}

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

}
