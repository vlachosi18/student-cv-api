package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a user's email address.
 *
 * @param newEmail The new email address requested by the user.
 */
public record UpdateEmailRequest(String newEmail) {
}
