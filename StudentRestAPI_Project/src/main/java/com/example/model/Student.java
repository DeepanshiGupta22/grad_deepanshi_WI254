package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Student {
    
    @Id
    private Long regNo;
    
    private Long rollNo;
    private String name;
    private String standard;
    private String school;
    private String gender;
    private Double percentage;

    // Getters and Setters
    public Long getRegNo() { return regNo; }
    public void setRegNo(Long regNo) { this.regNo = regNo; }
    
    public Long getRollNo() { return rollNo; }
    public void setRollNo(Long rollNo) { this.rollNo = rollNo; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }
    
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
}