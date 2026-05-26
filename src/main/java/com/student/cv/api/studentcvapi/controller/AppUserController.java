package com.student.cv.api.studentcvapi.controller;

import com.student.cv.api.studentcvapi.dto.AppUserDTO;
import com.student.cv.api.studentcvapi.request.UpdateEmailRequest;
import com.student.cv.api.studentcvapi.request.UpdatePasswordRequest;
import com.student.cv.api.studentcvapi.service.AppUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing user profiles and data.
 */
@RestController
@RequestMapping("/api/users")
public class AppUserController extends BaseController {

    public AppUserController(AppUserService appUserService) {
        super(appUserService);
    }


    /**
     * Retrieves a user by their ID.
     * Only accessible by an Admin or the owner of the requested profile.
     *
     * @param id      The ID of the user to retrieve.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the User DTO, 401 UNAUTHORIZED if not logged in, 403 FORBIDDEN if no permission, or 404 NOT FOUND if user not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        Optional<AppUserDTO> appUserDTO = appUserService.getUserById(id);
        if (appUserDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "No user found with the given ID."));

        }
        return ResponseEntity.ok(appUserDTO.get());
    }


    /**
     * Updates the email address of a specific user.
     * Logs out the user if successful.
     * Only accessible by an Admin or the owner of the requested profile.
     *
     * @param id                 The ID of the user whose email is being updated.
     * @param updateEmailRequest The DTO containing the new email address.
     * @param request            The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a success message, 400 BAD REQUEST if data is invalid/null, 401 UNAUTHORIZED if not logged in or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/email")
    public ResponseEntity<?> editUserEmailById(@PathVariable Integer id,
                                               @RequestBody(required = false) UpdateEmailRequest updateEmailRequest,
                                               HttpServletRequest request) {
        if (updateEmailRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update email request is null."));
        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!appUserService.editUserEmail(id, updateEmailRequest.newEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Email cannot be updated."));
        }

        request.getSession().invalidate();
        return ResponseEntity.ok(Map.of("message", "Email updated successfully. User logged out."));
    }


    /**
     * Updates the password of a specific user.
     * Logs out the user if successful.
     * Only accessible by an Admin or the owner of the requested profile.
     *
     * @param id                    The ID of the user whose password is being updated.
     * @param updatePasswordRequest The DTO containing the new password.
     * @param request               The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a success message, 400 BAD REQUEST if data is invalid/null, 401 UNAUTHORIZED if not logged in or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<?> editUserPasswordById(@PathVariable Integer id,
                                                  @RequestBody(required = false) UpdatePasswordRequest updatePasswordRequest,
                                                  HttpServletRequest request) {
        if (updatePasswordRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update password request is null."));
        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!appUserService.editUserPassword(id, updatePasswordRequest.newPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Password cannot be updated."));
        }
        request.getSession().invalidate();
        return ResponseEntity.ok(Map.of("message", "Password updated successfully. User logged out."));

    }


}
