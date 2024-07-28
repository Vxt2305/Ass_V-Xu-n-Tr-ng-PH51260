package com.example.baiduthiass.Model;

public class UserDetails {
    private String username;
    private String fullName;
    private String birthDate;
    private float height;
    private float weight;
    private Float bmi;

    public UserDetails(String username, String fullName, String birthDate, float height, float weight, Float bmi) {
        this.username = username;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.bmi = bmi;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Float getBmi() {
        return bmi;
    }

    public void setBmi(Float bmi) {
        this.bmi = bmi;
    }
}
