package com.nsoft.laundromat.controller.model;

public class DashUserObject {
    private String name;
    private String role;
    private String timeIn;
    private String timeOut;

    public DashUserObject() {

    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getTimeIn() { return timeIn; }
    public void setTimeIn(String timeIn) { this.timeIn = timeIn; }

    public String getTimeOut() { return timeOut; }
    public void setTimeOut(String timeOut) { this.timeOut = timeOut; }

}
