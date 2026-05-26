package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating a student's academic ID.
 *
 * @param newAcademicId The new academic identifier to be assigned to the student.
 */
public record UpdateAcademicIdRequest(String newAcademicId) {
}
