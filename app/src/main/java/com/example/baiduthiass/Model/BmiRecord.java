package com.example.baiduthiass.Model;

public class BmiRecord {
    private int id;
    private String username;
    private float bmi;
    private String recordDate;

    // Constructor mới
    public BmiRecord(String username, float bmi, String recordDate) {
        this.username = username;
        this.bmi = bmi;
        this.recordDate = recordDate;
    }

    public BmiRecord(int id, String username, float bmi, String recordDate) {
        this.id = id;
        this.username = username;
        this.bmi = bmi;
        this.recordDate = recordDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
