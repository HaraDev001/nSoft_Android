package com.nsoft.laundromat.controller.model;


public class CustomerObject {
    private int id;
    private String firstName;
    private String lastName;
    private String amount;
    private String phoneNumber;
    private String customerTime;
    private int dayNumber;

    public CustomerObject() {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCustomerTime() { return customerTime; }
    public void setCustomerTime(String customerTime) { this.customerTime = customerTime; }

    public int getDayNumber(){return dayNumber;}
    public void setDayNumber(int dayNumber){this.dayNumber = dayNumber;}
}
