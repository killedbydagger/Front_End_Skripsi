package com.example.skripsi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.widget.ImageView;

public class Vacancy {

    public String title;
    public String category;
    public int salary;
    public String locationName;
    public String id;
    public String locationId;
    public String categoryId;
    public String description;
    public String position;
    public String dueDate;

    public Vacancy(){

    }



    public Vacancy(String title, String category, int salary, String locationName, String id, String locationId, String categoryId, String description, String position, String dueDate){
        this.title = title;
        this.category = category;
        this.salary = salary;
        this.locationName = locationName;
        this.id = id;
        this.locationId = locationId;
        this.categoryId = categoryId;
        this.description = description;
        this.position = position;
        this.dueDate = dueDate;
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

    public int getSalary(){
        return salary;
    }

    public void setSalary(int salary){
        this.salary = salary;
    }

    public String getLocationName(){
        return locationName;
    }

    public void setLocationName(String locationName){
        this.locationName = locationName;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public String getLocationId(){
        return locationId;
    }

    public void setLocationId(String locationId){
        this.locationId = locationId;
    }

    public String getCategoryId(){
        return categoryId;
    }

    public void setCategoryId(String categoryId){
        this.categoryId = categoryId;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
