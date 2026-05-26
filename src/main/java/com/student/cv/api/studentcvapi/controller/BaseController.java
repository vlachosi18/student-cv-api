package com.student.cv.api.studentcvapi.controller;


import com.student.cv.api.studentcvapi.dto.AppUserDTO;
import com.student.cv.api.studentcvapi.entity.AppUser;
import com.student.cv.api.studentcvapi.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;


/**
 * Abstract base class providing common security checks and helper methods for the application's REST controllers.
 */
public abstract class BaseController {

    protected final AppUserService appUserService;


    public BaseController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    /**
     * Helper method to verify session existence and user permissions.
     *
     * @param id      The ID of the profile being accessed or modified
     * @param request The HTTP request used to check the session.
     * @return A ResponseEntity with an error status (401 or 403) if checks fail, or null if the user is authorized.
     */
    protected ResponseEntity<?> checkSecurity(Integer id, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session) && !appUserService.validateId(id, session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return null;
    }

    /**
     * Helper method to update the session with fresh data.
     *
     * @param request    The HTTP request used to check the session.
     * @param updatedDTO The newly updated DTO to replace the old one in the session.
     */
    protected void updateSession(HttpServletRequest request, Object updatedDTO) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("loggedInUser", updatedDTO);
        }
    }
}
