package com.student.cv.api.studentcvapi.service;

import com.student.cv.api.studentcvapi.dto.AppUserDTO;
import com.student.cv.api.studentcvapi.dto.BusinessDTO;
import com.student.cv.api.studentcvapi.dto.StudentDTO;
import com.student.cv.api.studentcvapi.entity.AppUser;
import com.student.cv.api.studentcvapi.entity.Business;
import com.student.cv.api.studentcvapi.entity.Student;
import com.student.cv.api.studentcvapi.repository.AppUserRepository;
import com.student.cv.api.studentcvapi.repository.BusinessRepository;
import com.student.cv.api.studentcvapi.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

/**
 * Service methods of Entity AppUser
 */
@Service
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final StudentRepository studentRepository;
    private final BusinessRepository businessRepository;

    public AppUserService(AppUserRepository appUserRepository, StudentRepository studentRepository, BusinessRepository businessRepository) {
        this.appUserRepository = appUserRepository;
        this.studentRepository = studentRepository;
        this.businessRepository = businessRepository;
    }

    /**
     * Authenticates a user and stores their specific profile (Student, Business or Admin) in the session.
     *
     * @param email       The user's email address.
     * @param password    The user's password.
     * @param httpSession The current HTTP session to store the logged-in user's DTO.
     * @return true if login is successful, false if credentials are wrong or profile is missing.
     */
    public boolean logInUser(String email, String password, HttpSession httpSession) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);
        if (appUser.isEmpty() || !Objects.equals(appUser.get().getPassword(), password)) {
            return false;
        }
        AppUser user = appUser.get();
        switch (user.getRole()) {
            case "STUDENT":
                Optional<Student> student = studentRepository.findById(user.getId());
                if (student.isPresent()) {
                    Student s = student.get();
                    httpSession.setAttribute("loggedInUser",
                            new StudentDTO
                                    (s.getId(),
                                            user.getEmail(),
                                            s.getAcademicId(),
                                            s.getFirstName(), s.getLastName()));
                    return true;
                } else return false;

            case "BUSINESS":
                Optional<Business> business = businessRepository.findById(user.getId());
                if (business.isPresent()) {
                    Business b = business.get();
                    httpSession.setAttribute("loggedInUser", new BusinessDTO(b.getId(),
                            user.getEmail(), b.getRegistrationNumber(), b.getName(), b.getDescription()));
                    return true;
                } else return false;

            case "ADMIN":
                httpSession.setAttribute("loggedInUser", new AppUserDTO(user.getId(), user.getEmail(), "ADMIN"));
                return true;
        }
        return false;
    }

    /**
     * Logs out the user by invalidating their current session.
     *
     * @param httpSession The current HTTP session of the user.
     * @return true if the session was successfully invalidated, false if the session was already null.
     */
    public boolean logOutUser(HttpSession httpSession){
        if(httpSession!=null){
            httpSession.invalidate();
            return true;
        }
        else return false;

    }

    /**
     * Validates if the given ID matches the ID stored in the httpSession
     * of the logged-in user.
     *
     * @param id The given ID.
     * @param httpSession The current HTTP session of the user.
     * @return true if the given ID and the session ID match, false otherwise or if the given ID or the session is null
     */
    public boolean validateId(Integer id, HttpSession httpSession){
        if(httpSession==null || id==null){
            return false;
        }
        Object user = httpSession.getAttribute("loggedInUser");
        if(user==null){
            return false;
        }
        if (user instanceof StudentDTO){
            return Objects.equals(((StudentDTO) user).getId(),id);
        }
        if (user instanceof BusinessDTO){
            return Objects.equals(((BusinessDTO) user).getId(),id);
        }
        if (user instanceof AppUserDTO){
            return Objects.equals(((AppUserDTO) user).getId(),id);

        }
        return false;
    }

    /**
     * Checks if the currently logged-in user has Admin privileges.
     *
     * @param httpSession The current HTTP session of the user.
     * @return true if the logged-in user is an Admin, false otherwise, if the session is null, or if no user is logged in.
     */
    public boolean checkIfAdmin(HttpSession httpSession){
        if (httpSession==null){
            return false;
        }
        Object user = httpSession.getAttribute("loggedInUser");
        if(user==null){
            return false;
        }
        if (user instanceof AppUserDTO){
            return Objects.equals(((AppUserDTO) user).getRole(),"ADMIN");

        }
        return false;
    }

    /**
     * Checks if the currently logged-in user is a Business.
     *
     * @param httpSession The current HTTP session of the user.
     * @return  true if the logged-in user is a Business, false otherwise, if the session is null, or if no user is logged in.
     */
    public boolean checkIfBusiness(HttpSession httpSession){
        if (httpSession==null){
            return false;
        }
        Object user = httpSession.getAttribute("loggedInUser");
        if(user==null){
            return false;
        }
        return user instanceof BusinessDTO;
    }

    /**
     * Adds a new user to the database.
     *
     * @param email The user's email.
     * @param password The user's password.
     * @param role The user's role
     * @return The ID of the newly created user, or -1 if inputs are null, email exists, or DB fails.
     */
    public Integer addUser(String email, String password, String role) {
        if (email == null || password == null || role == null) {
            return -1;
        }
        if (appUserRepository.findByEmail(email).isPresent()) {
            return -1;
        }
        try {
            AppUser savedUser = appUserRepository.save(new AppUser(email, password, role));
            return savedUser.getId();
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Deletes a user from the database based on their ID.
     *
     * @param id The ID of the user to be deleted.
     * @return true if the deletion is successful, false if the ID is null, the user doesn't exist, or a DB error occurs.
     */
    public boolean deleteUserById(Integer id){
        if (id==null){
            return false;
        }
        if(!appUserRepository.existsById(id)){
            return false;
        }
        try {
            appUserRepository.deleteById(id);
            return true;
        }catch (Exception e){
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the email address of an existing user.
     *
     * @param id The ID of the user.
     * @param newEmail The new email address to be set.
     * @return true if the email was successfully updated, false if inputs are null, the user doesn't exist, or a DB error occurs.
     */
    public boolean editUserEmail(Integer id, String newEmail) {
        if (id == null || newEmail == null) {
            return false;
        }

        try {
           Optional<AppUser> user =appUserRepository.findById(id);
           if(user.isEmpty()){
               return false;
           }
           AppUser appUser= user.get();
           appUser.setEmail(newEmail);
           appUserRepository.save(appUser);
            return true;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates the password of an existing user.
     *
     * @param id The ID of the user.
     * @param newPassword The new password to be set.
     * @return true if the password was successfully updated, false if inputs are null, the user doesn't exist, or a DB error occurs.
     */
    public boolean editUserPassword(Integer id, String newPassword){
        if (id == null || newPassword == null) {
            return false;
        }

        try {
            Optional<AppUser> user =appUserRepository.findById(id);
            if(user.isEmpty()){
                return false;
            }
            AppUser appUser= user.get();
            appUser.setPassword(newPassword);
            appUserRepository.save(appUser);
            return true;

        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a user by their ID and returns them as a DTO.
     *
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the AppUserDTO if found, or an empty Optional if the ID is null, the user is not found, or a DB error occurs.
     */
    public Optional<AppUserDTO> getUserById(Integer id){
        if (id==null){
            return Optional.empty();
        }
        try {
            Optional<AppUser> user =appUserRepository.findById(id);
            if(user.isEmpty()){
                return Optional.empty();
            }
            AppUser appUser= user.get();
            return Optional.of(new AppUserDTO(appUser.getId(), appUser.getEmail(), appUser.getRole()));
        }catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
