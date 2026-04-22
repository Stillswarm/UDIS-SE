package com.udis.model;

public class Registration {
    private int regId;
    private String rollNo;
    private String courseId;
    private int semester;
    private int year;
    private String status; // REGISTERED, COMPLETED, BACKLOG

    public Registration() { }

    public Registration(int regId, String rollNo, String courseId, int semester, int year, String status) {
        this.regId = regId;
        this.rollNo = rollNo;
        this.courseId = courseId;
        this.semester = semester;
        this.year = year;
        this.status = status;
    }

    public int getRegId() { return regId; }
    public void setRegId(int regId) { this.regId = regId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
