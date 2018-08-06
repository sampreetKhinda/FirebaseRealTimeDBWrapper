package com.yapapp.firebaserealtimedbwrapper.model;

public class ErrorModel {


    private boolean status;
    private String message;
    private int errorId = 0;

    private String errorLink;
    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrorLink() {
        return errorLink;
    }

    public void setErrorLink(String errorLink) {
        this.errorLink = errorLink;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public ErrorModel(int code, String message) {
        this.message = message;
        this.errorId = code;
    }

    public ErrorModel(int code, String message, String details) {
        this.message = message;
        this.errorId = code;
    }
}
