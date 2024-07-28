package com.example.baiduthiass.Model;

public class StepRecord {
    private int id;
    private String username;
    private long steps;
    private double distance;
    private long duration;
    private long recordDate;

    public StepRecord(int id, String username, long steps, double distance, long duration, long recordDate) {
        this.id = id;
        this.username = username;
        this.steps = steps;
        this.distance = distance;
        this.duration = duration;
        this.recordDate = recordDate;
    }

    // Getters and setters
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

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(long recordDate) {
        this.recordDate = recordDate;
    }
}
