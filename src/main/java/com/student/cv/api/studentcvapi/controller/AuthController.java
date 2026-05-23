package com.student.cv.api.studentcvapi.controller;

import com.student.cv.api.studentcvapi.request.LoginRequest;
import com.student.cv.api.studentcvapi.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for managing user authentication (Login, Logout, and Session checking).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }


    /**
     * Authenticates a user and stores their profile in the session.
     *
     * @param loginRequest The login credentials (email and password).
     * @param request      The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the User DTO if successful, 400 BAD REQUEST if request is null or user is already logged in, or 401 UNAUTHORIZED if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) LoginRequest loginRequest, HttpServletRequest request) {
        if (loginRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Log in request is null."));
        }
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null && oldSession.getAttribute("loggedInUser") != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "You are already logged in. Please log out first."));
        }

        HttpSession newSession = request.getSession(true);

        if (!appUserService.logInUser(loginRequest.email(), loginRequest.password(), newSession)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid email or password."));
        }
        Object loggedInUser = newSession.getAttribute("loggedInUser");
        return ResponseEntity.ok(loggedInUser);
    }

    /**
     * Logs out the currently authenticated user by invalidating their session.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a success message if logged out, or 400 BAD REQUEST if logout fails.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            appUserService.logOutUser(session);
            return ResponseEntity.ok(Map.of("message", "Logged out successfully."));

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No active session found to log out."));
    }

    /**
     * Retrieves the profile of the currently logged-in user.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the User DTO if authenticated, or 401 UNAUTHORIZED if not logged in.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("loggedInUser") != null) {
            return ResponseEntity.ok(session.getAttribute("loggedInUser"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated."));

    }
}
