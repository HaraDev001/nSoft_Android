package com.nsoft.laundromat.controller.model;


public class ReplenishItemObject {
    private int no;
    private String itemName;
    private String itemCode;

    public ReplenishItemObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCode(){ return  itemCode;}
    public void setItemCode(String itemCode){this.itemCode = itemCode;}

}
