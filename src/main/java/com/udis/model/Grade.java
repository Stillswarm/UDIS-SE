package com.udis.model;

public class Grade {
    private int gradeId;
    private String rollNo;
    private String courseId;
    private int semester;
    private int year;
    private String letterGrade;
    private double gradePoints;
    private int credits; // joined in, used by GPA formula

    public Grade() { }

    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public double getGradePoints() { return gradePoints; }
    public void setGradePoints(double gradePoints) { this.gradePoints = gradePoints; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
}
