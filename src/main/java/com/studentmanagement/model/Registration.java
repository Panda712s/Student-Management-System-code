package com.studentmanagement.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A row in the registrations table, joined with the student and course it links.
 */
public class Registration {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int id;
    private final int studentId;
    private final String studentName;
    private final int courseId;
    private final String courseCode;
    private final String courseName;
    private final LocalDateTime registrationDate;

    public Registration(int id, int studentId, String studentName, int courseId,
                        String courseCode, String courseName, LocalDateTime registrationDate) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.registrationDate = registrationDate;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public String toString() {
        String date = registrationDate == null ? "-" : registrationDate.format(DATE_FORMAT);
        return String.format("%-4d %-25s %-10s %-30s %s",
                id, studentName, courseCode, courseName, date);
    }
}
