package com.example.fikspresif;

public class Aspirasi {
    private String title;
    private String content;
    private String createdAt;

    public Aspirasi(String title, String content, String createdAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
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
}
