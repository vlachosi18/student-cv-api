package com.student.cv.api.studentcvapi.repository;

import com.student.cv.api.studentcvapi.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository for managing Keyword entities.
 */
public interface KeywordRepository extends JpaRepository<Keyword,Integer> {
    List<Keyword> findByStudentId (Integer studentId);
}
