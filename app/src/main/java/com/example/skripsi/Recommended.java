package com.example.skripsi;

public class Recommended {
    public String vacancyCategory, vacancyPosition, vacancyTitle, vacancyCompanyName, vacancyLocation, vacancyId, vacancyStatus, vacancyCompanyRating, businessId, favoriteFlag;
    int vacancySalary;

    public Recommended(String vacancyCategory, String vacancyPosition, String vacancyTitle, String vacancyCompanyName, String vacancyLocation, int vacancySalary, String vacancyId, String vacancyStatus, String vacancyCompanyRating, String businessId, String favoriteFlag) {
        this.vacancyCategory = vacancyCategory;
        this.vacancyPosition = vacancyPosition;
        this.vacancyTitle = vacancyTitle;
        this.vacancyCompanyName = vacancyCompanyName;
        this.vacancyLocation = vacancyLocation;
        this.vacancySalary = vacancySalary;
        this.vacancyId = vacancyId;
        this.vacancyStatus = vacancyStatus;
        this.vacancyCompanyRating = vacancyCompanyRating;
        this.businessId = businessId;
        this.favoriteFlag = favoriteFlag;
    }

    public Recommended() {

    }

    public String getVacancyCategory() {
        return vacancyCategory;
    }

    public void setVacancyCategory(String vacancyCategory) {
        this.vacancyCategory = vacancyCategory;
    }

    public String getVacancyPosition() {
        return vacancyPosition;
    }

    public void setVacancyPosition(String vacancyPosition) {
        this.vacancyPosition = vacancyPosition;
    }

    public String getVacancyTitle() {
        return vacancyTitle;
    }

    public void setVacancyTitle(String vacancyTitle) {
        this.vacancyTitle = vacancyTitle;
    }

    public String getVacancyCompanyName() {
        return vacancyCompanyName;
    }

    public void setVacancyCompanyName(String vacancyCompanyName) {
        this.vacancyCompanyName = vacancyCompanyName;
    }

    public String getVacancyLocation() {
        return vacancyLocation;
    }

    public void setVacancyLocation(String vacancyLocation) {
        this.vacancyLocation = vacancyLocation;
    }

    public int getVacancySalary() {
        return vacancySalary;
    }

    public void setVacancySalary(int vacancySalary) {
        this.vacancySalary = vacancySalary;
    }

    public String getVacancyId() {
        return vacancyId;
    }

    public void setVacancyId(String vacancyId) {
        this.vacancyId = vacancyId;
    }

    public String getVacancyStatus() {
        return vacancyStatus;
    }

    public void setVacancyStatus(String vacancyStatus) {
        this.vacancyStatus = vacancyStatus;
    }

    public String getVacancyCompanyRating() {
        return vacancyCompanyRating;
    }

    public void setVacancyCompanyRating(String vacancyCompanyRating) {
        this.vacancyCompanyRating = vacancyCompanyRating;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFavoriteFlag() {
        return favoriteFlag;
    }

    public void setFavoriteFlag(String favoriteFlag) {
        this.favoriteFlag = favoriteFlag;
    }
}
