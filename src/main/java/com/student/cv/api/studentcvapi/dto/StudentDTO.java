package com.student.cv.api.studentcvapi.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the Student entity.
 */
public class StudentDTO {
    private Integer id;
    private String email;
    private String academicId;
    private String firstName;
    private String lastName;
    private final String role;

    public StudentDTO(){
      role="STUDENT";
    }

    public StudentDTO(Integer id, String email, String academicId, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.academicId = academicId;
        this.firstName = firstName;
        this.lastName = lastName;
        role="STUDENT";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getRole(){
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentDTO that = (StudentDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(academicId, that.academicId) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, academicId, firstName, lastName, role);
    }
}
