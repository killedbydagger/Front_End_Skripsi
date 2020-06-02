package com.example.skripsi;

public class SearchVacancy {
    public String vacancyCategory, vacancyPosition, vacancyTitle, vacancyCompanyName, vacancyLocation, vacancyId, vacancyStatus, vacancyCompanyRating;
    public int vacancySalary;

    public SearchVacancy(String vacancyCategory, String vacancyPosition, String vacancyTitle, String vacancyCompanyName, String vacancyLocation, String vacancyId, String vacancyStatus, String vacancyCompanyRating, int vacancySalary) {
        this.vacancyCategory = vacancyCategory;
        this.vacancyPosition = vacancyPosition;
        this.vacancyTitle = vacancyTitle;
        this.vacancyCompanyName = vacancyCompanyName;
        this.vacancyLocation = vacancyLocation;
        this.vacancyId = vacancyId;
        this.vacancyStatus = vacancyStatus;
        this.vacancyCompanyRating = vacancyCompanyRating;
        this.vacancySalary = vacancySalary;
    }

    public SearchVacancy() {

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
}
