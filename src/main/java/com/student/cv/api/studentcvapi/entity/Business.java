package com.student.cv.api.studentcvapi.entity;

import jakarta.persistence.*;

import java.util.Objects;

/**
 * Represents a registered business.
 * Links directly to an AppUser using a shared Primary Key.
 */
@Entity
@Table(name ="businesses")
public class Business {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private AppUser appUser;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public Business(){

    }
    public Business(AppUser appUser, String registrationNumber, String name, String description) {
        this.appUser = appUser;
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Business business = (Business) o;
        return Objects.equals(id, business.id) && Objects.equals(registrationNumber, business.registrationNumber) && Objects.equals(name, business.name) && Objects.equals(description, business.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, registrationNumber, name, description);
    }
}
