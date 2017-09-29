package com.example.henrymatidios.thesis.Models;

/**
 * @author Henry Matidios.
 * @since 26/08/2017
 */

public class Logs {
    private String key;
    public LogsInfo values = new LogsInfo();

    public Logs(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
