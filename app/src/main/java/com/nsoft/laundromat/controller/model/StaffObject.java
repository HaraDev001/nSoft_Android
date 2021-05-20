package com.nsoft.laundromat.controller.model;


public class StaffObject {
    private String id;
    private String name;
    private String role;
    private String shiftNo;
    private String timeIn;
    private String timeOut;

    public StaffObject() {

    }

    public String  getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getShiftNo() { return shiftNo; }
    public void setShiftNo(String shiftNo) { this.shiftNo = shiftNo; }

    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }

    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }
}
