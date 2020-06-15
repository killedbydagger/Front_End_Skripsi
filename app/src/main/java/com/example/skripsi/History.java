package com.example.skripsi;

public class History {
    public String category, position, title, companyName, location, vacId, status, rating, favoriteFlag, busId, flagRating, busImage;
    public int salary, rateDariUser;

    public History() {

    }

    public History(String category, String position, String title, String companyName, String location, String vacId, String status, String rating, String favoriteFlag, String busId, String flagRating, int salary, int rateDariUser, String busImage) {
        this.category = category;
        this.position = position;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.vacId = vacId;
        this.status = status;
        this.rating = rating;
        this.favoriteFlag = favoriteFlag;
        this.busId = busId;
        this.flagRating = flagRating;
        this.salary = salary;
        this.rateDariUser = rateDariUser;
        this.busImage = busImage;
    }

    public int getRateDariUser() {
        return rateDariUser;
    }

    public void setRateDariUser(int rateDariUser) {
        this.rateDariUser = rateDariUser;
    }

    public String getFlagRating() {
        return flagRating;
    }

    public void setFlagRating(String flagRating) {
        this.flagRating = flagRating;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public void setVacId(String vacId) {
        this.vacId = vacId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setFavoriteFlag(String favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getCategory() {
        return category;
    }

    public String getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLocation() {
        return location;
    }

    public int getSalary() {
        return salary;
    }

    public String getVacId() {
        return vacId;
    }

    public String getStatus() {
        return status;
    }

    public String getRating() {
        return rating;
    }

    public String getFavoriteFlag() {
        return favoriteFlag;
    }

    public String getBusId() {
        return busId;
    }

    public String getBusImage() {
        return busImage;
    }

    public void setBusImage(String busImage) {
        this.busImage = busImage;
    }
}
