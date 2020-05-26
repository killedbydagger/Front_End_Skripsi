package com.example.skripsi;

public class Vacancy {

    public String title;
    public String category;
    public String salary;
    public String locationName;

    public Vacancy(){

    }

    public Vacancy(String title, String category, String salary, String locationName){
        this.title = title;
        this.category = category;
        this.salary = salary;
        this.locationName = locationName;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getCategory(){
        return category;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getSalary(){
        return salary;
    }

    public void setSalary(String salary){
        this.salary = salary;
    }

    public String getLocationName(){
        return locationName;
    }

    public void setLocationName(String locationName){
        this.locationName = locationName;
    }

}
