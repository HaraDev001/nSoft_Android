package com.nsoft.laundromat.controller.model;


public class NoticeObject {
    private int no;
    private String type;
    private String dateTime;
    private String title;
    private String content;
    private String viewStatus;
    private String actionStatus;

    public NoticeObject() {

    }

    public int getNo(){return  no;}
    public void setNo(int no){this.no = no;}

    public String getType(){return type;}
    public void setType(String type){this.type = type;}

    public String getTitle(){return title;}
    public void setTitle(String title){this.title = title;}

    public String getDateTime(){return dateTime;}
    public void setDateTime(String dateTime){this.dateTime = dateTime;}

    public String getContent(){return content;}
    public void setContent(String content){this.content = content;}

    public String getViewStatus(){return viewStatus;}
    public void setViewStatus(String viewStatus){this.viewStatus = viewStatus;}

    public String getActionStatus(){return actionStatus;}
    public void setActionStatus(String actionStatus){this.actionStatus = actionStatus;}
}
