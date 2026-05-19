package com.student.cv.api.studentcvapi.repository;

import com.student.cv.api.studentcvapi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for managing Student entities.
 */
public interface StudentRepository extends JpaRepository<Student,Integer> {
    Optional<Student> findByAcademicId(String academicId);

}
