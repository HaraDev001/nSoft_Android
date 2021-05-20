package com.nsoft.laundromat.controller.menu.ui.transactions;

public class TransactionView {
    public String name;
    public String photoUrl;
    public String operationId;
    public String amount;

    public TransactionView(String inputName, String inputPhotoUrl, String inputOperationId,
                           String inputAmount){
        this.name = inputName;
        this.photoUrl = inputPhotoUrl;
        this.operationId = inputOperationId;
        this.amount = inputAmount;
    }
}
