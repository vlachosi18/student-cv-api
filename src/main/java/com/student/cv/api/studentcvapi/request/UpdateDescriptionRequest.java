package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a business's description.
 *
 * @param newDescription The new description to be assigned to the business.
 */
public record UpdateDescriptionRequest(String newDescription) {
}
