package com.student.cv.api.studentcvapi.repository;

import com.student.cv.api.studentcvapi.entity.Keyword;
import com.student.cv.api.studentcvapi.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for managing Keyword entities.
 */
public interface KeywordRepository extends JpaRepository<Keyword,Integer> {


    List<Keyword> findByStudentId (Integer studentId);

    @Query("SELECT DISTINCT k.text FROM Keyword k")
    List<String> findDistinctKeywordTexts();

    @Query("SELECT k.student FROM Keyword k WHERE k.text = :text")
    List<Student> findStudentsByKeywordText(@Param("text") String text);

    boolean existsByStudentIdAndText (Integer studentId, String text);
    void deleteAllByStudentId(Integer studentId);
}
