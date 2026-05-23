package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for user login requests.
 *
 * @param email    The user's email address.
 * @param password The user's password.
 */
public record LoginRequest(String email, String password) {

}
