package com.nsoft.laundromat.controller.menu.ui.offer;

public class OfferCategoryView {
    public String categoryName;
    public int categoryNo;
    public boolean selectedStatus;

    public OfferCategoryView(String inputName, int inputNo, boolean inputStatus){
        this.categoryName = inputName;
        this.categoryNo = inputNo;
        this.selectedStatus = inputStatus;
    }
}
