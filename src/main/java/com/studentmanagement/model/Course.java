package com.studentmanagement.model;

/**
 * A row in the courses table.
 */
public class Course {

    private int id;
    private String courseName;
    private String courseCode;

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }

    public Course(int id, String courseName, String courseCode) {
        this(courseName, courseCode);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-10s %-35s", id, courseCode, courseName);
    }
}
