package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a student's first name.
 *
 * @param newFirstName The new first name to be assigned to the student.
 */
public record UpdateFirstNameRequest(String newFirstName) {
}
