package com.student.cv.api.studentcvapi.dto;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for the AppUser entity.
 */
public class AppUserDTO {
    private Integer id;
    private String email;
    private String role;

    public AppUserDTO(){
    }

    public AppUserDTO(Integer id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUserDTO that = (AppUserDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(email, that.email) && Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, role);
    }
}
