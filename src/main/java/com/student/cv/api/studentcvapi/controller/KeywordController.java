package com.student.cv.api.studentcvapi.controller;

import com.student.cv.api.studentcvapi.dto.KeywordDTO;
import com.student.cv.api.studentcvapi.dto.StudentDTO;
import com.student.cv.api.studentcvapi.request.CreateKeywordRequest;
import com.student.cv.api.studentcvapi.request.UpdateKeywordTextRequest;
import com.student.cv.api.studentcvapi.service.AppUserService;
import com.student.cv.api.studentcvapi.service.KeywordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for managing keywords and related data.
 */
@RestController()
@RequestMapping("/api/keywords")
public class KeywordController extends BaseController {

    private final KeywordService keywordService;

    public KeywordController(AppUserService appUserService, KeywordService keywordService) {
        super(appUserService);
        this.keywordService = keywordService;
    }


    /**
     * Retrieves all keywords associated with a specific student.
     * Accessible by Admins, Businesses, or the specific Student who owns the profile.
     *
     * @param id      The ID of the student whose keywords are being retrieved.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a list of KeywordDTOs, 404 NOT FOUND if no keywords exist, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/student/{id}")
    public ResponseEntity<?> getAllKeywordsByStudentId(@PathVariable Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.checkIfBusiness(session) && !appUserService.validateId(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }

        List<KeywordDTO> keywordDTOS = keywordService.getAllKeywordsByStudentId(id);
        if (keywordDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No keywords found."));
        }
        return ResponseEntity.ok(keywordDTOS);
    }


    /**
     * Retrieves a list of all unique keyword texts in the system.
     * Accessible only by Admins and Businesses.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a list of keyword strings, 404 NOT FOUND if none exist, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/text")
    public ResponseEntity<?> getAllUniqueKeywordTexts(HttpServletRequest request) {
        ResponseEntity<?> securityError = checkAdminOrBusinessSecurity(request);
        if (securityError != null) return securityError;
        List<String> keywordTexts = keywordService.getAllKeywordTexts();
        if (keywordTexts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No keywords found."));
        }
        return ResponseEntity.ok(keywordTexts);
    }


    /**
     * Retrieves a list of students who have a specific keyword associated with their profile.
     * Accessible only by Admins and Businesses.
     *
     * @param keyword The text of the keyword to search for.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a list of StudentDTOs, 404 NOT FOUND if no students match the keyword, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> getAllStudentsByKeywordText(@PathVariable String keyword, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkAdminOrBusinessSecurity(request);
        if (securityError != null) return securityError;
        List<StudentDTO> studentDTOS = keywordService.getAllStudentsByKeywordText(keyword);
        if (studentDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Students cannot be retrieved."));
        }
        return ResponseEntity.ok(studentDTOS);
    }


    /**
     * Creates a new keyword for a specific student.
     * Accessible only by an Admin or the specific Student who owns the profile.
     *
     * @param createKeywordRequest The DTO containing the student's ID and the keyword text.
     * @param request              The HTTP request used to safely retrieve the current session.
     * @return 201 CREATED if created successfully, 400 BAD REQUEST if data is missing or creation fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PostMapping()
    public ResponseEntity<?> createKeyword(@RequestBody(required = false) CreateKeywordRequest createKeywordRequest, HttpServletRequest request) {
        if (createKeywordRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Create keyword request is null."));
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.validateId(createKeywordRequest.studentId(), session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        if (!keywordService.addKeyword(createKeywordRequest.studentId(),
                createKeywordRequest.keywordText())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not create keyword."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Keyword created successfully"));
    }


    /**
     * Updates the text of an existing keyword.
     * Accessible only by an Admin or the specific Student who owns the keyword.
     *
     * @param keywordId                The ID of the keyword to be updated.
     * @param updateKeywordTextRequest The DTO containing the new keyword text.
     * @param request                  The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{keywordId}")
    public ResponseEntity<?> editKeywordTextByKeywordId(@PathVariable Integer keywordId,
                                                        @RequestBody(required = false) UpdateKeywordTextRequest updateKeywordTextRequest,
                                                        HttpServletRequest request) {
        if (updateKeywordTextRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update keyword text request is null."));

        }
        ResponseEntity<?> securityError = verifyKeywordSecurity(keywordId, request);
        if (securityError != null) return securityError;

        if (!keywordService.editKeywordText(keywordId, updateKeywordTextRequest.newText())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Keyword cannot be updated. Keyword text might already be assigned to the student."));
        }
        return ResponseEntity.ok(Map.of("message", "Keyword text updated successfully"));
    }


    /**
     * Deletes a specific keyword by its ID.
     * Accessible only by an Admin or the specific Student who owns the keyword.
     *
     * @param keywordId The ID of the keyword to be deleted.
     * @param request   The HTTP request used to safely retrieve the current session.
     * @return OK if deleted successfully, 400 BAD REQUEST if the keyword does not exist or deletion fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<?> deleteKeywordByKeywordId(@PathVariable Integer keywordId, HttpServletRequest request) {
        ResponseEntity<?> securityError = verifyKeywordSecurity(keywordId, request);
        if (securityError != null) return securityError;

        if (!keywordService.deleteKeyword(keywordId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Keyword cannot be deleted. Keyword ID might not exist."));
        }
        return ResponseEntity.ok(Map.of("message", "Keyword deleted successfully."));

    }


    /**
     * Helper method to verify if the logged-in user is an Admin or a Business.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return A ResponseEntity with 401 UNAUTHORIZED or 403 FORBIDDEN if checks fail, or null if authorized.
     */
    private ResponseEntity<?> checkAdminOrBusinessSecurity(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.checkIfBusiness(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return null;
    }


    /**
     * Helper method to verify if a keyword exists and if the logged-in user has permission to modify or delete it.
     *
     * @param keywordId The ID of the keyword to check.
     * @param request   The HTTP request used to safely retrieve the current session.
     * @return A ResponseEntity with an error status if checks fail, or null if authorized.
     */
    private ResponseEntity<?> verifyKeywordSecurity(Integer keywordId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }

        Integer studentId = keywordService.getStudentIdByKeywordId(keywordId);

        if (!appUserService.checkIfAdmin(session) && !appUserService.validateId(studentId,session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return null;
    }
}
