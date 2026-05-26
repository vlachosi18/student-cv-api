package com.student.cv.api.studentcvapi.controller;


import com.student.cv.api.studentcvapi.dto.StudentDTO;
import com.student.cv.api.studentcvapi.request.*;
import com.student.cv.api.studentcvapi.service.AppUserService;
import com.student.cv.api.studentcvapi.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


/**
 * REST Controller for managing student profiles, CVs, and related data.
 */
@RestController()
@RequestMapping("/api/students")
public class StudentController extends BaseController {

    private final StudentService studentService;

    public StudentController(AppUserService appUserService, StudentService studentService) {
        super(appUserService);
        this.studentService = studentService;
    }


    /**
     * Retrieves a list of all students.
     * Only accessible by Admins and Businesses.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a list of StudentDTOs, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping
    public ResponseEntity<?> getAllStudents(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.checkIfBusiness(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return ResponseEntity.ok(studentService.getAllStudents());
    }


    /**
     * Retrieves a student's profile by their ID.
     * Accessible by Admins, Businesses, or the specific Student who owns the profile.
     *
     * @param id      The ID of the student to retrieve.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the StudentDTO, 404 NOT FOUND if student not found, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkStudentProfileSecurity(id, request);
        if (securityError != null) return securityError;

        Optional<StudentDTO> studentDTO = studentService.getStudentById(id);
        if (studentDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Student cannot be retrieved."));
        }
        return ResponseEntity.ok(studentDTO.get());

    }

    /**
     * Retrieves a student's CV by their ID.
     * Accessible by Admins, Businesses, or the specific Student who owns the profile.
     *
     * @param id      The ID of the student whose CV is being retrieved.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the CV data, 404 NOT FOUND if the CV cannot be retrieved, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/{id}/cv")
    public ResponseEntity<?> getStudentCvById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkStudentProfileSecurity(id, request);
        if (securityError != null) return securityError;

        Optional<String> studentCv = studentService.getStudentCvById(id);
        if (studentCv.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Student CV cannot be retrieved."));
        }
        return ResponseEntity.ok(Map.of("message", studentCv.get()));
    }

    /**
     * Registers a new student in the system.
     * This is a public endpoint (no session or permissions required).
     *
     * @param studentRegisterRequest The DTO containing the student's registration details.
     * @return 201 CREATED if successful, or 400 BAD REQUEST if data is missing or profile creation fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody(required = false) StudentRegisterRequest studentRegisterRequest) {
        if (studentRegisterRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Student register request is null."));

        }
        Integer studentId = appUserService.addUser(studentRegisterRequest.email(),
                studentRegisterRequest.password(),
                "STUDENT");
        if (studentId == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Registration failed. Email might already exist."));
        }
        if (!studentService.addStudent(studentId,
                studentRegisterRequest.academicId(),
                studentRegisterRequest.firstName(),
                studentRegisterRequest.lastName(),
                studentRegisterRequest.cvText())) {
            appUserService.deleteUserById(studentId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not create student profile. Academic ID might already exist."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Student registered successfully."));

    }

    /**
     * Deletes a student profile by their ID.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id      The ID of the student to be deleted.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK if deleted successfully, 400 BAD REQUEST if deletion fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudentById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;
        if (!studentService.deleteStudentById(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not delete student profile. Student ID might not exist."));
        }
        return ResponseEntity.ok(Map.of("message", "Student deleted successfully."));
    }


    /**
     * Updates a student's academic ID.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                      The ID of the student whose academic ID is being updated.
     * @param updateAcademicIdRequest The DTO containing the new academic ID.
     * @param request                 The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/academic-id")
    public ResponseEntity<?> editStudentAcademicIdById(@PathVariable Integer id,
                                                       @RequestBody(required = false) UpdateAcademicIdRequest updateAcademicIdRequest,
                                                       HttpServletRequest request) {
        if (updateAcademicIdRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update academic ID request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!studentService.editStudentAcademicId(id, updateAcademicIdRequest.newAcademicId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update student's academic ID. Academic ID might already exist."));
        }
        updateStudentSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Student's academic ID updated successfully."));

    }


    /**
     * Updates a student's first name.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                     The ID of the student whose first name is being updated.
     * @param updateFirstNameRequest The DTO containing the new first name.
     * @param request                The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/first-name")
    public ResponseEntity<?> editStudentFirstNameById(@PathVariable Integer id,
                                                      @RequestBody(required = false) UpdateFirstNameRequest updateFirstNameRequest,
                                                      HttpServletRequest request) {
        if (updateFirstNameRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update first name request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!studentService.editStudentFirstName(id, updateFirstNameRequest.newFirstName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update student's first name."));
        }
        updateStudentSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Student's first name updated successfully."));

    }

    /**
     * Updates a student's last name.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                    The ID of the student whose last name is being updated.
     * @param updateLastNameRequest The DTO containing the new last name.
     * @param request               The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/last-name")
    public ResponseEntity<?> editStudentLastNameById(@PathVariable Integer id,
                                                     @RequestBody(required = false) UpdateLastNameRequest updateLastNameRequest,
                                                     HttpServletRequest request) {
        if (updateLastNameRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update last name request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!studentService.editStudentLastName(id, updateLastNameRequest.newLastName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update student's last name."));
        }
        updateStudentSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Student's last name updated successfully."));

    }

    /**
     * Updates a student's cv text.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                  The ID of the student whose cv text is being updated.
     * @param updateCvTextRequest The DTO containing the new cv text.
     * @param request             The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/cv-text")
    public ResponseEntity<?> editStudentCvTextById(@PathVariable Integer id,
                                                   @RequestBody(required = false) UpdateCvTextRequest updateCvTextRequest,
                                                   HttpServletRequest request) {
        if (updateCvTextRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update cv text request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!studentService.editStudentCvText(id, updateCvTextRequest.newCvText())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update student's cv text."));
        }

        return ResponseEntity.ok(Map.of("message", "Student's cv text updated successfully."));

    }


    /**
     * Helper method to verify if the logged-in user has permission to access a specific student's profile.
     * Access is granted only if the user is an Admin, a Business, or the Student owning the profile.
     *
     * @param id      The ID of the student profile being accessed.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return A ResponseEntity with 401 UNAUTHORIZED or 403 FORBIDDEN if checks fail, or null if authorized.
     */
    private ResponseEntity<?> checkStudentProfileSecurity(Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.checkIfBusiness(session) && !appUserService.validateId(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return null;
    }

    /**
     * Helper method to fetch the latest student data from the database and update the current HTTP session.
     *
     * @param id      The ID of the student whose session needs updating.
     * @param request The HTTP request used to safely retrieve the current session.
     */
    private void updateStudentSession(Integer id, HttpServletRequest request) {
        Optional<StudentDTO> optionalStudentDTO = studentService.getStudentById(id);
        if (optionalStudentDTO.isPresent()) {
            updateSession(request, optionalStudentDTO.get());
        }
    }

}
