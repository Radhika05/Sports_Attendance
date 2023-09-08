package com.example.sportsattendance;

public class StudentItem {
    private long sid;
    private long cid;

    private int roll;
    private String name;
    private String status;


    public StudentItem(long sid, long cid, int roll, String name) {
        this.cid = cid;
        this.sid = sid;
        this.name = name;
        this.roll = roll;
        this.status = "";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }


    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }
}
