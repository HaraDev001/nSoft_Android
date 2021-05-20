package com.nsoft.laundromat.controller.menu.ui.dashboard;

public class DashboardCategoryView {
    public String categoryName;
    public int categoryNo;
    public boolean selectedStatus;

    public DashboardCategoryView(String inputName, int inputNo, boolean inputStatus){
        this.categoryName = inputName;
        this.categoryNo = inputNo;
        this.selectedStatus = inputStatus;
    }
}
