package com.example.baiduthiass.Model;

public class WritePost {
    private int id;
    private String username;
    private String posts;
    private String recordDate;

    public WritePost(int id, String username, String posts, String recordDate) {
        this.id = id;
        this.username = username;
        this.posts = posts;
        this.recordDate = recordDate;
    }

    public WritePost() {

    }

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

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(String recordDate) {
        this.recordDate = recordDate;
    }
}
