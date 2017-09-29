package com.example.henrymatidios.thesis.Models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * @author Henry Matidios
 * @since 26/07/2017.
 */

@IgnoreExtraProperties
public class User {
    private String email;
    private String name;
    private String type;
    private int image;

    public User() {}

    public User(String email, String type, String name, int image) {
        this.email = email;
        this.type = type;
        this.name = name;
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
