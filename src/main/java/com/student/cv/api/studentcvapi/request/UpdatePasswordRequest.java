package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a user's password.
 *
 * @param newPassword The new password chosen by the user
 */
public record UpdatePasswordRequest(String newPassword) {
}
