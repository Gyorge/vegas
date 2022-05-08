package com.example.vegas;

public class Reserved_date {
    private String date;
    private String time;
    private String type;
    private String userEmail;

    public Reserved_date(String date,String time, String type, String userEmail){
        this.date=date;
        this.time=time;
        this.type=type;
        this.userEmail=userEmail;
    }

    public Reserved_date(){}

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getUserEmail() {
        return userEmail;
    }
}
