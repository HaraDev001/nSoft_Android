package com.nsoft.laundromat.controller.model;

public class DashInventoryObject {
    private String name;
    private String unit;
    private String first;
    private String second;
    private String third;
    private boolean criticalStatus;
    private int storage;

    public DashInventoryObject() {

    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getFirst() { return first; }
    public void setFirst(String first) { this.first = first; }

    public String getSecond() { return second; }
    public void setSecond(String second) { this.second = second; }

    public String getThird() { return third; }
    public void setThird(String third) { this.third = third; }

    public boolean getCriticalStatus(){return  criticalStatus;}
    public void setCriticalStatus(boolean criticalStatus){this.criticalStatus = criticalStatus;}

    public int getStorage(){return  storage;}
    public void setStorage(int storage){this.storage = storage;}
}
