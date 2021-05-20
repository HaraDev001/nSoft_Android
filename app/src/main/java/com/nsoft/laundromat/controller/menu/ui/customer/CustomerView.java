package com.nsoft.laundromat.controller.menu.ui.customer;

public class CustomerView {
    public String name;
    public String amount;
    public String phoneNumber;
    public String customerTime;
    public int dayNumber;

    public CustomerView(String inputName, String inputAmount, String inputPhoneNumber,
                        String inputTime, int inputDayNumber){
        this.name = inputName;
        this.amount = inputAmount;
        this.phoneNumber = inputPhoneNumber;
        this.customerTime = inputTime;
        this.dayNumber = inputDayNumber;
    }
}
