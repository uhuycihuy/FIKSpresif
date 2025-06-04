package com.example.fikspresif;
public class Aspirasi {
    private String title;
    private String content;
    private String createdAt;
    private String username;
    private boolean isAnonymous;

    public Aspirasi(String title, String content, String createdAt, String username, boolean isAnonymous) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.username = username;
        this.isAnonymous = isAnonymous;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAnonymous() {
        return isAnonymous;
    }
}
