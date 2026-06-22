package com.student.cv.api.studentcvapi.controller;

import com.student.cv.api.studentcvapi.dto.BusinessDTO;
import com.student.cv.api.studentcvapi.request.*;
import com.student.cv.api.studentcvapi.service.AppUserService;
import com.student.cv.api.studentcvapi.service.BusinessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for managing business profiles and data and related data.
 */
@RestController()
@RequestMapping("/api/businesses")
public class BusinessController extends BaseController {
    private final BusinessService businessService;

    public BusinessController(AppUserService appUserService, BusinessService businessService) {
        super(appUserService);
        this.businessService = businessService;
    }


    /**
     * Retrieves a list of all businesses.
     * Only accessible by Admins.
     *
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with a list of BusinessDTOs, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping
    public ResponseEntity<?> getAllBusinesses(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "No active session found."));
        }
        if (!appUserService.checkIfAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No permission given."));
        }
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    /**
     * Retrieves a business's profile by their ID.
     * Accessible by Admins or the specific Business who owns the profile.
     *
     * @param id      The ID of the business to retrieve.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK with the BusinessDTO, 404 NOT FOUND if business not found, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBusinessById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        Optional<BusinessDTO> businessDTO = businessService.getBusinessById(id);
        if (businessDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Business cannot be retrieved."));
        }
        return ResponseEntity.ok(businessDTO.get());
    }


    /**
     * Registers a new business in the system.
     * This is a public endpoint (no session or permissions required).
     *
     * @param businessRegisterRequest The DTO containing the business's registration details.
     * @return 201 CREATED if successful, or 400 BAD REQUEST if data is missing or profile creation fails.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerBusiness(@RequestBody(required = false) BusinessRegisterRequest businessRegisterRequest) {
        if (businessRegisterRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Business register request is null."));

        }
        Integer businessId = appUserService.addUser(businessRegisterRequest.email(),
                businessRegisterRequest.password(),
                "BUSINESS");
        if (businessId == -1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Registration failed. Email might already exist."));
        }
        if (!businessService.addBusiness(businessId,
                businessRegisterRequest.registrationNumber(),
                businessRegisterRequest.name(),
                businessRegisterRequest.description())) {
            appUserService.deleteUserById(businessId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not create business profile. Registration number might already exist."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Business registered successfully."));

    }


    /**
     * Deletes a business profile by their ID.
     * Logs out the user only if they are the owner of the requested profile.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id      The ID of the business to be deleted.
     * @param request The HTTP request used to safely retrieve the current session.
     * @return 200 OK if deleted successfully, 400 BAD REQUEST if deletion fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBusinessById(@PathVariable Integer id, HttpServletRequest request) {
        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;
        if (!businessService.deleteBusinessById(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not delete business profile. Business ID might not exist."));
        }

        if(appUserService.checkIfAdmin(request.getSession())) {
            return ResponseEntity.ok(Map.of("message", "Business deleted successfully."));
        }
        request.getSession().invalidate();
        return ResponseEntity.ok(Map.of("message", "Business deleted successfully. User logged out."));
    }


    /**
     * Updates a business's registration number.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                              The ID of the business whose registration number is being updated.
     * @param updateRegistrationNumberRequest The DTO containing the new registration number.
     * @param request                         The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/registration-number")
    public ResponseEntity<?> editBusinessRegistrationNumberById(@PathVariable Integer id,
                                                                @RequestBody(required = false) UpdateRegistrationNumberRequest updateRegistrationNumberRequest,
                                                                HttpServletRequest request) {
        if (updateRegistrationNumberRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update registration number request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!businessService.editBusinessRegistrationNumber(id, updateRegistrationNumberRequest.newRegistrationNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update business's registration number. Registration number might already exist."));
        }
        updateBusinessSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Business's registration number updated successfully."));

    }

    /**
     * Updates a business's name.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                The ID of the business whose name is being updated.
     * @param updateNameRequest The DTO containing the new name.
     * @param request           The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/name")
    public ResponseEntity<?> editBusinessNameById(@PathVariable Integer id,
                                                  @RequestBody(required = false) UpdateNameRequest updateNameRequest,
                                                  HttpServletRequest request) {
        if (updateNameRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update name request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!businessService.editBusinessName(id, updateNameRequest.newName())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update business's name."));
        }
        updateBusinessSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Business's name updated successfully."));
    }


    /**
     * Updates a business's description.
     * Only accessible by an Admin or the profile owner.
     *
     * @param id                       The ID of the business whose description is being updated.
     * @param updateDescriptionRequest The DTO containing the new description.
     * @param request                  The HTTP request used to safely retrieve the current session.
     * @return 200 OK if updated successfully, 400 BAD REQUEST if data is missing or update fails, 401 UNAUTHORIZED if not logged in, or 403 FORBIDDEN if no permission.
     */
    @PatchMapping("/{id}/description")
    public ResponseEntity<?> editBusinessDescriptionById(@PathVariable Integer id,
                                                         @RequestBody(required = false) UpdateDescriptionRequest updateDescriptionRequest,
                                                         HttpServletRequest request) {
        if (updateDescriptionRequest == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Update description request is null."));

        }

        ResponseEntity<?> securityError = checkSecurity(id, request);
        if (securityError != null) return securityError;

        if (!businessService.editBusinessDescription(id, updateDescriptionRequest.newDescription())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Could not update business's description."));
        }
        updateBusinessSession(id, request);
        return ResponseEntity.ok(Map.of("message", "Business's description updated successfully."));
    }


    /**
     * Helper method to fetch the latest business data from the database and update the current HTTP session.
     *
     * @param id      The ID of the business whose session needs updating.
     * @param request The HTTP request used to safely retrieve the current session.
     */
    private void updateBusinessSession(Integer id, HttpServletRequest request) {
        Optional<BusinessDTO> optionalBusinessDTO = businessService.getBusinessById(id);
        if (optionalBusinessDTO.isPresent()) {
            updateSession(request, optionalBusinessDTO.get());
        }
    }
}
