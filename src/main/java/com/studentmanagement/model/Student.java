package com.studentmanagement.model;

import java.time.LocalDateTime;

/**
 * A row in the students table.
 */
public class Student {

    private int id;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;

    public Student(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public Student(int id, String name, String email, String phone, LocalDateTime createdAt) {
        this(name, email, phone);
        this.id = id;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return String.format("%-4d %-25s %-32s %-15s",
                id, name, email, phone == null ? "-" : phone);
    }
}
