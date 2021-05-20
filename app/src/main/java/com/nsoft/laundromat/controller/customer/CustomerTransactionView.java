package com.nsoft.laundromat.controller.customer;

public class CustomerTransactionView {
    public int no;
    public String operationId;
    public String dateTime;
    public String amount;
    public boolean status;

    public CustomerTransactionView(int inputNo, String inputOperationId, String inputDateTime, String inputAmount,
                                   boolean inputStatus){
        this.no = inputNo;
        this.operationId = inputOperationId;
        this.dateTime = inputDateTime;
        this.amount = inputAmount;
        this.status = inputStatus;
    }
}
