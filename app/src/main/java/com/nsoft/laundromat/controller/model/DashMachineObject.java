package com.nsoft.laundromat.controller.model;

public class DashMachineObject {
    private String id;
    private String name;
    private String status;
    private String kind;
    private String remainTime;
    private String registerTime;
    private String duration;
    private String machineNo;

    public DashMachineObject() {

    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public String getRemainTime() { return remainTime; }
    public void setRemainTime(String remainTime) { this.remainTime = remainTime; }

    public String getDuration(){ return duration;}
    public void setDuration(String duration){this.duration = duration;}

    public String getRegisterTime() { return registerTime; }
    public void setRegisterTime(String registerTime) { this.registerTime = registerTime; }

    public String getMachineNo(){ return machineNo;}
    public void setMachineNo(String machineNo){this.machineNo = machineNo;}
}
