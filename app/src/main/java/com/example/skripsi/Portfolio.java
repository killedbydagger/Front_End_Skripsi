package com.example.skripsi;

public class Portfolio {

    public String imgId;
    public String imgURL;

    public Portfolio() {

    }

    public Portfolio(String imgId, String imgURL) {
        this.imgId = imgId;
        this.imgURL = imgURL;
    }

    public String getImgId() {
        return imgId;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
