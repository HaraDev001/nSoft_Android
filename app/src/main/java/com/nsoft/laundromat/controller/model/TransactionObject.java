package com.nsoft.laundromat.controller.model;

public class TransactionObject {
    private int id;
    private String userName;
    private String photoUrl;
    private String operationId;
    private String amount;

    public TransactionObject() {

    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}
