package com.example.skripsi;

public class RatingData {
    public String userName;
    public String value;
    public String comment;

    public RatingData() {

    }

    public RatingData(String userName, String value, String comment) {
        this.userName = userName;
        this.value = value;
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }

    public String getValue() {
        return value;
    }

    public String getComment() {
        return comment;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
