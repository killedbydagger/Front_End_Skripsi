package com.example.skripsi;

public class Applicant {

    public String name, email, vac_id, applicant_id;

    public Applicant(){

    }

    public Applicant(String name, String email, String vac_id, String applicant_id) {
        this.name = name;
        this.email = email;
        this.vac_id = vac_id;
        this.applicant_id = applicant_id;
    }

    public String getVac_id() {
        return vac_id;
    }

    public void setVac_id(String vac_id) {
        this.vac_id = vac_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApplicant_id() {
        return applicant_id;
    }

    public void setApplicant_id(String applicant_id) {
        this.applicant_id = applicant_id;
    }
}
