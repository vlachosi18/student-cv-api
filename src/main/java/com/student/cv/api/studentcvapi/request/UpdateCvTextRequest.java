package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a student's cv text.
 *
 * @param newCvText The new cv text to be assigned to the student.
 */
public record UpdateCvTextRequest(String newCvText) {
}
