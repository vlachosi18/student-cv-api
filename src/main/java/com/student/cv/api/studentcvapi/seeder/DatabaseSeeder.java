package com.student.cv.api.studentcvapi.seeder;

import com.student.cv.api.studentcvapi.entity.AppUser;
import com.student.cv.api.studentcvapi.repository.AppUserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Seeder to initialize default database values.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;


    public DatabaseSeeder(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Runs automatically after the application context is fully loaded.
     * Checks if the default admin account exists by email, and if not, creates it
     *
     * @param args incoming application arguments passed to the main method
     */
    @Override
    public void run(String... args) {
        String adminEmail = "admin@admin.com";
        String adminPassword = "admin";

        try {
            Optional<AppUser> optionalAppUser = appUserRepository.findByEmail(adminEmail);
            if (optionalAppUser.isPresent()){
                System.out.println("Seeder: Default Admin already exists");
                return;
            }
            appUserRepository.save(new AppUser(adminEmail, BCrypt.hashpw(adminPassword, BCrypt.gensalt()), "ADMIN"));
            System.out.println("Seeder: Default Admin created successfully.");

        }catch(Exception e){
            System.out.println("Database Error: " + e.getMessage());
        }


    }
}
