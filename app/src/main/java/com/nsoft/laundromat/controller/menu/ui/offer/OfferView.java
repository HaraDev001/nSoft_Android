package com.nsoft.laundromat.controller.menu.ui.offer;

public class OfferView {
    public String code;
    public String category;
    public String kind;
    public String description;
    public String price;
    public String cost;
    public String vatType;

    public OfferView(String inputCode, String inputCategory, String inputKind, String inputDesc,
                     String inputPrice, String inputCost, String inputType){
        this.code = inputCode;
        this.category = inputCategory;
        this.kind = inputKind;
        this.description = inputDesc;
        this.price = inputPrice;
        this.cost = inputCost;
        this.vatType = inputType;
    }
}
