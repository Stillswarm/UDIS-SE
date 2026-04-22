package com.udis.model;

public class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private int semester;
    private String prerequisiteId;

    public Course() { }

    public Course(String courseId, String courseName, int credits, int semester, String prerequisiteId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.prerequisiteId = prerequisiteId;
    }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getPrerequisiteId() { return prerequisiteId; }
    public void setPrerequisiteId(String prerequisiteId) { this.prerequisiteId = prerequisiteId; }

    @Override
    public String toString() { return courseId + " - " + courseName; }
}
