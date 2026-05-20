package com.student.cv.api.studentcvapi.service;


import com.student.cv.api.studentcvapi.dto.StudentDTO;
import com.student.cv.api.studentcvapi.entity.AppUser;
import com.student.cv.api.studentcvapi.entity.Student;
import com.student.cv.api.studentcvapi.repository.AppUserRepository;
import com.student.cv.api.studentcvapi.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service methods of Entity Student
 */
@Service
public class StudentService {
    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;

    public StudentService(AppUserRepository appUserRepository, StudentRepository studentRepository) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Retrieves all students from the database and returns them as a list of DTOs.
     *
     * @return A List of StudentDTOs, or an empty list if a DB error occurs, or if there are no students in the database.
     */
    public List<StudentDTO> getAllStudents() {
        try {
            List<Student> students = studentRepository.findAll();
            ArrayList<StudentDTO> studentDTOS = new ArrayList<>();
            for (Student student : students) {
                studentDTOS.add(new StudentDTO(student.getId(), student.getAppUser().getEmail(), student.getAcademicId(), student.getFirstName(), student.getLastName()));
            }
            return studentDTOS;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a student by their ID and returns them as a DTO.
     *
     * @param id The ID of the student to retrieve.
     * @return An Optional containing the StudentDTO if found, or an empty Optional if the ID is null, the student is not found, or a DB error occurs.
     */
    public Optional<StudentDTO> getStudentById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return Optional.empty();
            }
            Student s = student.get();
            return Optional.of(new StudentDTO(s.getId(), s.getAppUser().getEmail(), s.getAcademicId(), s.getFirstName(), s.getLastName()));
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return Optional.empty();
        }

    }

    /**
     * Retrieves the CV text of a student by their ID.
     *
     * @param id The ID of the student.
     * @return An Optional containing the CV text if found, or an empty Optional if the ID is null, the student is not found, or a DB error occurs.
     */
    public Optional<String> getStudentCvById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return Optional.empty();
            }
            Student s = student.get();
            return Optional.of(s.getCvText());
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Adds a new student profile linked to an existing AppUser.
     *
     * @param id         The ID of the AppUser.
     * @param academicId The student's academic ID
     * @param firstName  The student's first name.
     * @param lastName   The student's last name.
     * @param cvText     The student's CV text.
     * @return true if successfully saved, false if inputs are null, student already exists, user not found, or DB error occurs.
     */
    public boolean addStudent(Integer id, String academicId, String firstName, String lastName, String cvText) {
        if (id == null || academicId == null || firstName == null || lastName == null || cvText == null) {
            return false;
        }
        if (studentRepository.existsById(id)) {
            return false;
        }
        try {
            Optional<AppUser> user = appUserRepository.findById(id);
            if (user.isEmpty()) {
                return false;
            }
            AppUser appUser = user.get();
            studentRepository.save(new Student(appUser, academicId, firstName, lastName, cvText));
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a student and their associated user account from the database.
     *
     * @param id The ID of the student (and AppUser) to be deleted.
     * @return true if the deletion is successful, false if the ID is null, the student doesn't exist, or a DB error occurs.
     */
    public boolean deleteStudentById(Integer id) {
        if (id == null) {
            return false;
        }
        if (!studentRepository.existsById(id)) {
            return false;
        }
        try {
            studentRepository.deleteById(id);
            appUserRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }

    }

    /**
     * Updates the academic ID of an existing student.
     *
     * @param id            The ID of the student.
     * @param newAcademicId The new academic ID to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editStudentAcademicId(Integer id, String newAcademicId) {
        if (id == null || newAcademicId == null) {
            return false;
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return false;
            }
            Student s = student.get();
            s.setAcademicId(newAcademicId);
            studentRepository.save(s);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }

    }

    /**
     * Updates the first name of an existing student.
     *
     * @param id           The ID of the student.
     * @param newFirstName The new first name to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editStudentFirstName(Integer id, String newFirstName) {
        if (id == null || newFirstName == null) {
            return false;
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return false;
            }
            Student s = student.get();
            s.setFirstName(newFirstName);
            studentRepository.save(s);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the last name of an existing student.
     *
     * @param id          The ID of the student.
     * @param newLastName The new last name to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editStudentLastName(Integer id, String newLastName) {
        if (id == null || newLastName == null) {
            return false;
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return false;
            }
            Student s = student.get();
            s.setLastName(newLastName);
            studentRepository.save(s);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the cv text of an existing student.
     *
     * @param id        The ID of the student.
     * @param newCvText The new cv text to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editStudentCvText(Integer id, String newCvText) {
        if (id == null || newCvText == null) {
            return false;
        }
        try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isEmpty()) {
                return false;
            }
            Student s = student.get();
            s.setCvText(newCvText);
            studentRepository.save(s);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

}
