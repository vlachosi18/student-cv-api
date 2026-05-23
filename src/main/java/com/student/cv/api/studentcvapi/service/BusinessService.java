package com.student.cv.api.studentcvapi.service;

import com.student.cv.api.studentcvapi.dto.BusinessDTO;
import com.student.cv.api.studentcvapi.entity.AppUser;
import com.student.cv.api.studentcvapi.entity.Business;
import com.student.cv.api.studentcvapi.repository.AppUserRepository;
import com.student.cv.api.studentcvapi.repository.BusinessRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service methods of Entity Business
 */
@Service
public class BusinessService {

    private final AppUserRepository appUserRepository;
    private final BusinessRepository businessRepository;

    public BusinessService(AppUserRepository appUserRepository, BusinessRepository businessRepository) {
        this.appUserRepository = appUserRepository;
        this.businessRepository = businessRepository;
    }

    /**
     * Retrieves all businesses from the database and returns them as a list of DTOs.
     *
     * @return A List of BusinessDTOs, or an empty list if a DB error occurs, or if there are no businesses in the database.
     */
    public List<BusinessDTO> getAllBusinesses() {
        try {
            List<Business> businesses = businessRepository.findAll();
            ArrayList<BusinessDTO> businessDTOS = new ArrayList<>();
            for (Business business : businesses) {
                businessDTOS.add(new BusinessDTO(business.getId(),
                        business.getAppUser().getEmail(),
                        business.getRegistrationNumber(),
                        business.getName(),
                        business.getDescription()));
            }
            return businessDTOS;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves a business by their ID and returns them as a DTO.
     *
     * @param id The ID of the business to retrieve.
     * @return An Optional containing the BusinessDTO if found, or an empty Optional if the ID is null, the business is not found, or a DB error occurs.
     */
    public Optional<BusinessDTO> getBusinessById(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            Optional<Business> business = businessRepository.findById(id);
            if (business.isEmpty()) {
                return Optional.empty();
            }
            Business b = business.get();
            return Optional.of(new BusinessDTO(b.getId(),
                    b.getAppUser().getEmail(),
                    b.getRegistrationNumber(),
                    b.getName(),
                    b.getDescription()));
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return Optional.empty();
        }
    }


    /**
     * Adds a new business profile linked to an existing AppUser.
     *
     * @param id                 The ID of the AppUser.
     * @param registrationNumber The businesses registration number
     * @param name               The businesses name.
     * @param description        The businesses description.
     * @return true if successfully saved, false if inputs are null, business already exists, user not found, or DB error occurs.
     */
    public boolean addBusiness(Integer id, String registrationNumber, String name, String description) {
        if (id == null || registrationNumber == null || name == null || description == null) {
            return false;
        }

        try {
            if (businessRepository.existsById(id)) {
                return false;
            }
            Optional<AppUser> user = appUserRepository.findById(id);
            if (user.isEmpty()) {
                return false;
            }
            AppUser appUser = user.get();
            businessRepository.save(new Business(appUser, registrationNumber, name, description));
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a business and their associated user account from the database.
     *
     * @param id The ID of the business (and AppUser) to be deleted.
     * @return true if the deletion is successful, false if the ID is null, the business doesn't exist, or a DB error occurs.
     */
    public boolean deleteBusinessById(Integer id) {
        if (id == null) {
            return false;
        }

        try {
            if (!businessRepository.existsById(id)) {
                return false;
            }
            businessRepository.deleteById(id);
            appUserRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the registration number of an existing business.
     *
     * @param id                    The ID of the business.
     * @param newRegistrationNumber The new registration number to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editBusinessRegistrationNumber(Integer id, String newRegistrationNumber) {
        if (id == null || newRegistrationNumber == null) {
            return false;
        }
        try {
            Optional<Business> business = businessRepository.findById(id);
            if (business.isEmpty()) {
                return false;
            }
            Business b = business.get();
            b.setRegistrationNumber(newRegistrationNumber);
            businessRepository.save(b);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the name of an existing business.
     *
     * @param id      The ID of the business.
     * @param newName The new name to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editBusinessName(Integer id, String newName) {
        if (id == null || newName == null) {
            return false;
        }
        try {
            Optional<Business> business = businessRepository.findById(id);
            if (business.isEmpty()) {
                return false;
            }
            Business b = business.get();
            b.setName(newName);
            businessRepository.save(b);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the description of an existing business.
     *
     * @param id             The ID of the business.
     * @param newDescription The new description to be set.
     * @return true if successfully updated, false if inputs are null or DB error occurs.
     */
    public boolean editBusinessDescription(Integer id, String newDescription) {
        if (id == null || newDescription == null) {
            return false;
        }
        try {
            Optional<Business> business = businessRepository.findById(id);
            if (business.isEmpty()) {
                return false;
            }
            Business b = business.get();
            b.setDescription(newDescription);
            businessRepository.save(b);
            return true;
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }
}
