package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a student's last name.
 *
 * @param newLastName The new last name to be assigned to the student.
 */
public record UpdateLastNameRequest(String newLastName) {
}
