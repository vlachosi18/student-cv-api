package com.student.cv.api.studentcvapi.repository;

import com.student.cv.api.studentcvapi.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing Business entities.
 */
public interface BusinessRepository extends JpaRepository<Business,Integer> {
    Optional<Business> findByRegistrationNumber(String registrationNumber);
}
