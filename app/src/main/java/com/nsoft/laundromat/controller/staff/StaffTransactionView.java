package com.nsoft.laundromat.controller.staff;

public class StaffTransactionView {
    public String date;
    public String no;
    public String timeIn;
    public String timeOut;

    public StaffTransactionView(String inputDate, String inputNo, String inputTimeIn,
                                String inputTimeOut){
        this.date = inputDate;
        this.no = inputNo;
        this.timeIn = inputTimeIn;
        this.timeOut = inputTimeOut;
    }
}
