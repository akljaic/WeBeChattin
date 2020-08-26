package com.air.webechattin;

public class Messages {
    String from;
    String message;
    String type;
    String date;
    String time;

    public Messages() {
    }

    public Messages(String from, String message, String type, String date, String time) {
        this.from = from;
        this.message = message;
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
