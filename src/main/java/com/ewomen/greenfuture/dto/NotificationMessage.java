package com.ewomen.greenfuture.dto;

public class NotificationMessage {

    private String title;

    private String message;

    public NotificationMessage() {
    }

    public NotificationMessage(
            String title,
            String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}