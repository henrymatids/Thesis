package com.example.henrymatidios.thesis.Models;

/**
 * @author Henry Matidios
 * @since  28/08/2017
 */

public class LogsInfo {
    private String key;
    private String date;
    private String location;
    private String time;
    private int image;

    public LogsInfo(){}

    public LogsInfo(String date, String time, String location, int image) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.image = image;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}