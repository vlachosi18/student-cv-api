package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object  for registering a new student.
 * Contains all necessary information to create a student profile and user account.
 *
 * @param email      The email address of the new student.
 * @param password   The password for the new student's account.
 * @param academicId The unique academic identifier.
 * @param firstName  The student's first name.
 * @param lastName   The student's last name.
 * @param cvText     The student's curriculum vitae (CV) text.
 */
public record StudentRegisterRequest(String email,
                                     String password,
                                     String academicId,
                                     String firstName,
                                     String lastName,
                                     String cvText) {
}
