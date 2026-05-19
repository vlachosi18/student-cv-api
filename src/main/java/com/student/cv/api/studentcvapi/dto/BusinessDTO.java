package com.student.cv.api.studentcvapi.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the Business entity.
 */
public class BusinessDTO {
    private Integer id;
    private String email;
    private String registrationNumber;
    private String name;
    private String description;
    private final String role;

    public BusinessDTO(){
        role="BUSINESS";
    }

    public BusinessDTO(Integer id, String email, String registrationNumber, String name, String description) {
        this.id = id;
        this.email = email;
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.description = description;
        role="BUSINESS";
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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRole(){
        return role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessDTO that = (BusinessDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(registrationNumber, that.registrationNumber) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, registrationNumber, name, description, role);
    }
}
