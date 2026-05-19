package com.student.cv.api.studentcvapi.repository;

import com.student.cv.api.studentcvapi.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing AppUser entities.
 */
public interface AppUserRepository  extends JpaRepository<AppUser,Integer> {
    Optional<AppUser> findByEmail(String email);
}
