package com.nsoft.laundromat.controller.model;


public class ConsolidateBranchObject {
    private String shopName;
    private String branchName;
    private int no;

    public ConsolidateBranchObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getShopName(){return shopName;}
    public void setShopName(String shopName){this.shopName = shopName;}

    public String getBranchName(){return branchName;}
    public void setBranchName(String branchName){this.branchName = branchName;}
}
