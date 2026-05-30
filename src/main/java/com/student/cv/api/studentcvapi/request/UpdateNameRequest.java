package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a business's name.
 *
 * @param newName The new name to be assigned to the business.
 */
public record UpdateNameRequest(String newName) {
}
