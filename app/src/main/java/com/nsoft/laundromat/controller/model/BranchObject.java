package com.nsoft.laundromat.controller.model;

public class BranchObject {
    private int no;
    private String shopName;
    private boolean status;
    private String branchName;
    private String machineId;

    public BranchObject() {

    }

    public int getNo(){return  no;}
    public void setNo(int no){this.no = no;}

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getStatus() { return String.valueOf(status); }
    public void setStatus(boolean status) { this.status = status; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getMachineId() { return machineId; }
    public void setMachineId(String machineId) { this.machineId = machineId; }
}
