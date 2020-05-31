package com.example.skripsi;

public class History {
    public String category, position, title, companyName, location, vacId, status, rating;
    public int salary;

    public History(){

    }

    public History(String category, String position, String title, String companyName, String location, int salary, String vacId, String status, String rating){
        this.category = category;
        this.position = position;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.salary = salary;
        this.vacId = vacId;
        this.status = status;
        this.rating = rating;
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
}
