package com.nsoft.laundromat.controller.menu.ui.staff;

public class StaffView {
    public String name;
    public String role;
    public String timeIn;
    public String timeOut;
    public String shiftNo;

    public StaffView(String inputName, String inputRole, String inputShiftNo, String inputTimeIn,
                     String inputTimeOut){
        this.name = inputName;
        this.role = inputRole;
        this.shiftNo = inputShiftNo;
        this.timeIn = inputTimeIn;
        this.timeOut = inputTimeOut;
    }
}
