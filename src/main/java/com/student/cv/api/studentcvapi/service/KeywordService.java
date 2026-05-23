package com.student.cv.api.studentcvapi.service;


import com.student.cv.api.studentcvapi.dto.KeywordDTO;
import com.student.cv.api.studentcvapi.dto.StudentDTO;
import com.student.cv.api.studentcvapi.entity.Keyword;
import com.student.cv.api.studentcvapi.entity.Student;
import com.student.cv.api.studentcvapi.repository.KeywordRepository;
import com.student.cv.api.studentcvapi.repository.StudentRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service methods of Entity Keyword
 */
@Service
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final StudentRepository studentRepository;

    public KeywordService(KeywordRepository keywordRepository, StudentRepository studentRepository) {
        this.keywordRepository = keywordRepository;
        this.studentRepository = studentRepository;
    }


    /**
     * Retrieves all keywords associated with a specific student ID.
     *
     * @param studentId The ID of the student.
     * @return A List of KeywordDTOs, or an empty list if the ID is null, no keywords are found, or a DB error occurs.
     */
    public List<KeywordDTO> getAllKeywordsByStudentId(Integer studentId) {
        if (studentId == null) {
            return new ArrayList<>();
        }

        try {
            List<Keyword> keywords = keywordRepository.findByStudentId(studentId);
            if (keywords.isEmpty()) {
                return new ArrayList<>();
            }

            ArrayList<KeywordDTO> keywordDTOS = new ArrayList<>();
            for (Keyword keyword : keywords) {
                keywordDTOS.add(new KeywordDTO(keyword.getId(), keyword.getText()));
            }
            return keywordDTOS;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return new ArrayList<>();
        }

    }


    /**
     * Retrieves all unique keyword texts from the database.
     *
     * @return A List of unique keyword strings, or an empty list if no keywords exist or a DB error occurs.
     */
    public List<String> getAllKeywordTexts() {
        try {
            return keywordRepository.findDistinctKeywordTexts();
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    /**
     * Retrieves a list of students who have a specific keyword.
     *
     * @param keywordText The exact text of the keyword to search for.
     * @return A List of StudentDTOs matching the keyword, or an empty list if the text is null, no matches are found, or a DB error occurs.
     */
    public List<StudentDTO> getAllStudentsByKeywordText(String keywordText) {
        if (keywordText == null) {
            return new ArrayList<>();
        }
        try {
            List<Student> students = keywordRepository.findStudentsByKeywordText(keywordText);
            if (students.isEmpty()) {
                return new ArrayList<>();
            }
            List<StudentDTO> studentDTOS = new ArrayList<>();
            for (Student student : students) {
                studentDTOS.add(new StudentDTO(student.getId(),
                        student.getAppUser().getEmail(),
                        student.getAcademicId(),
                        student.getFirstName(),
                        student.getLastName()));
            }
            return studentDTOS;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Adds a new keyword to a specific student.
     *
     * @param studentId   The ID of the student.
     * @param keywordText The text of the keyword to add.
     * @return true if successfully added, false if inputs are null, the student doesn't exist, the keyword already exists for this student, or a DB error occurs.
     */
    public boolean addKeyword(Integer studentId, String keywordText) {
        if (studentId == null || keywordText == null) {
            return false;
        }

        try {
            if (keywordRepository.existsByStudentIdAndText(studentId, keywordText)) {
                return false;
            }

            Optional<Student> student = studentRepository.findById(studentId);
            if (student.isEmpty()) {
                return false;
            }

            keywordRepository.save(new Keyword(student.get(), keywordText));
            return true;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the text of an existing keyword.
     *
     * @param keywordId      The ID of the keyword to update.
     * @param newKeywordText The new text to be set.
     * @return true if successfully updated, false if inputs are null, the keyword doesn't exist, the student already has a keyword with this text, or a DB error occurs.
     */
    public boolean editKeywordText(Integer keywordId, String newKeywordText) {
        if (keywordId == null || newKeywordText == null) {
            return false;
        }
        try {
            Optional<Keyword> keyword = keywordRepository.findById(keywordId);
            if (keyword.isEmpty()) {
                return false;
            }
            Keyword k = keyword.get();
            if (keywordRepository.existsByStudentIdAndText(k.getStudent().getId(), newKeywordText)) {
                return false;
            }
            k.setText(newKeywordText);
            keywordRepository.save(k);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }


    /**
     * Deletes a keyword from the database based on its ID.
     *
     * @param keywordId The ID of the keyword to be deleted.
     * @return true if the deletion is successful, false if the ID is null, the keyword doesn't exist, or a DB error occurs.
     */
    public boolean deleteKeyword(Integer keywordId) {
        if (keywordId == null) {
            return false;
        }
        try {
            if (!keywordRepository.existsById(keywordId)) {
                return false;
            }
            keywordRepository.deleteById(keywordId);
            return true;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }


    /**
     * Retrieves the ID of the student associated with a specific keyword.
     *
     * @param keywordId The ID of the keyword.
     * @return The ID of the student who owns the keyword, or -1 if the keywordId is null, the keyword doesn't exist, or a DB error occurs.
     */
    public Integer getStudentIdByKeywordId(Integer keywordId) {
        if (keywordId == null) {
            return -1;
        }
        try {
            Optional<Keyword> keyword = keywordRepository.findById(keywordId);
            if (keyword.isEmpty()) {
                return -1;
            }
            Keyword k = keyword.get();
            return k.getStudent().getId();
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return -1;
        }
    }
}
