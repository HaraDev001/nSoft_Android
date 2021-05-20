package com.nsoft.laundromat.controller.menu.ui.home;

public class ShopView {
    public String shopId;
    public String name;
    public String branch;
    public String machineId;
    public String statusId;
    public String registeredDate;
    public String expiredDate;
    public boolean selected;
    public String amount;
    public int onlineStatus;

    public ShopView(String inputShopId, String inputName, String inputBranch, String inputMachineId, String inputAmount,
                    boolean inputSelected, int inputOnlineStatus){
        this.shopId = inputShopId;
        this.name = inputName;
        this.branch = inputBranch;
        this.machineId = inputMachineId;
        this.amount = inputAmount;
        this.selected = inputSelected;
        this.onlineStatus = inputOnlineStatus;
    }
}
