package com.example.lostfound.Classes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Message class; holds data of one message

public class Message {

    public Message() {

    }

    public Message(String text, String user) {
        this.text = text;
        this.user = user;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        this.time = currentDateandTime;
        this.statusReadOrNot = "false";
    }

    public String getText() {
        return text;
    }

    public String getUser() {
        return user;
    }

    public String getTime() {
        return time;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setStatusReadOrNot(String statusReadOrNot) {
        this.statusReadOrNot = statusReadOrNot;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String text;
    private String user;
    private String time;
    private String statusReadOrNot;
}
