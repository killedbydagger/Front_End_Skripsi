package com.example.skripsi;

public class Portfolio {

    public String imgId;
    public String imgURL;
    public String userId;

    public Portfolio() {

    }

    public Portfolio(String imgId, String imgURL, String userId) {
        this.imgId = imgId;
        this.imgURL = imgURL;
        this.userId = userId;
    }

    public String getImgId() {
        return imgId;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getUserId() {
        return userId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
