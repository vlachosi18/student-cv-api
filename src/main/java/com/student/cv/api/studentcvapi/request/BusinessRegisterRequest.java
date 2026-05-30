package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object  for registering a new business.
 * Contains all necessary information to create a business profile and user account.
 *
 * @param email              The email address of the new business.
 * @param password           The password for the new business's account.
 * @param registrationNumber The unique registration number.
 * @param name               The business's name.
 * @param description        The business's description.
 */
public record BusinessRegisterRequest(String email,
                                      String password,
                                      String registrationNumber,
                                      String name,
                                      String description) {
}
