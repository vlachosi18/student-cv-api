package com.student.cv.api.studentcvapi.request;

/**
 * Represents a data transfer object for updating the text of a keyword.
 *
 * @param newText The new text to be assigned to the keyword.
 */
public record UpdateKeywordTextRequest(String newText) {
}
