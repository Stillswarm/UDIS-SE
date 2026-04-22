package com.udis.model;

import java.time.LocalDate;

public class Student {
    private String rollNo;
    private String name;
    private LocalDate dob;
    private String gender;
    private String address;
    private String contact;
    private String program;
    private String batch;

    public Student() { }

    public Student(String rollNo, String name, LocalDate dob, String gender,
                   String address, String contact, String program, String batch) {
        this.rollNo = rollNo;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.contact = contact;
        this.program = program;
        this.batch = batch;
    }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }
}
