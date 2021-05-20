package com.nsoft.laundromat.controller.item;

public class ItemView {
    public int itemId;
    public String itemName;
    public String itemCode;
    public String itemUnit;
    public String expiredDate;
    public String itemQty;

    public ItemView(int inputId, String inputItemName, String inputUnit, String inputExpiredDate, String inputQty, String inputCode){
        this.itemId = inputId;
        this.itemName = inputItemName;
        this.itemUnit = inputUnit;
        this.expiredDate = inputExpiredDate;
        this.itemQty = inputQty;
        this.itemCode = inputCode;
    }
}
