package com.example.informatik.cognitizer.Tasks;


import com.microsoft.cognitive.speakerrecognition.contract.Confidence;

import java.util.UUID;

/**
 * DTO for the result of the {@link AuthorizeUserTask}
 */
public class AuthorizeUserTaskResult {
    private Exception exception;
    private UUID userId;
    private Confidence confidence;

    public AuthorizeUserTaskResult(UUID userId, Confidence confidence) {
        this.confidence = confidence;
        this.userId = userId;
        this.exception = null;
    }

    public AuthorizeUserTaskResult(Exception exception) {
        this.confidence = null;
        this.userId = null;
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }

    public UUID getUserId() {
        return userId;
    }

    public boolean isSuccess() {
        return exception == null;
    }

    public Confidence getConfidence() {
        return confidence;
    }
}
