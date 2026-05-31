package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for handling keyword creation requests.
 * Contains the necessary data to add a new keyword to a student's profile.
 *
 * @param studentId   The ID of the student to whom the keyword will be added.
 * @param keywordText The text of the keyword being created
 */
public record CreateKeywordRequest(Integer studentId,
                                   String keywordText) {
}
