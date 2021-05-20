package com.nsoft.laundromat.controller.model;


public class ReplenishItemDetailObject {
    private int no;
    private String itemName;
    private String itemCode;
    private String unit;
    private String expiredDate;
    private int quantity;

    public ReplenishItemDetailObject() {

    }

    public int getNo() { return no; }
    public void setNo(int no) { this.no = no; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiredDate() { return expiredDate; }
    public void setExpiredDate(String expiredDate) { this.expiredDate = expiredDate; }

    public int getQuantiry() { return quantity; }
    public void setQuantiry(int quantity) { this.quantity = quantity; }


}
