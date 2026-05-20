package com.student.cv.api.studentcvapi.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a registered student.
 * Links directly to an AppUser using a shared Primary Key.
 */
@Entity
@Table(name = "students")
public class Student {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;
    @Column(unique = true, nullable = false)
    private String academicId;

    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String cvText;

    public Student() {

    }

    public Student(AppUser appUser, String academicId, String firstName, String lastName, String cvText) {
        this.appUser = appUser;
        this.academicId = academicId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cvText = cvText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getAcademicId() {
        return academicId;
    }

    public void setAcademicId(String academicId) {
        this.academicId = academicId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCvText() {
        return cvText;
    }

    public void setCvText(String cvText) {
        this.cvText = cvText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(academicId, student.academicId) && Objects.equals(firstName, student.firstName) && Objects.equals(lastName, student.lastName) && Objects.equals(cvText, student.cvText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, academicId, firstName, lastName, cvText);
    }
}
