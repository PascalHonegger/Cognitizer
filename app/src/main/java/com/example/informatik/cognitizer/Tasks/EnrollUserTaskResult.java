package com.example.informatik.cognitizer.Tasks;


import com.microsoft.cognitive.speakerrecognition.contract.EnrollmentStatus;

/**
 * DTO for the result of the {@link EnrollUserTask}
 */
public class EnrollUserTaskResult {
    private Exception exception;
    private EnrollmentStatus enrollmentStatus;

    public EnrollUserTaskResult(EnrollmentStatus enrollmentStatus) {
        this.exception = null;
        this.enrollmentStatus = enrollmentStatus;
    }

    public EnrollUserTaskResult(Exception exception) {
        this.exception = exception;
        this.enrollmentStatus = null;
    }

    public Exception getException() {
        return exception;
    }

    public EnrollmentStatus getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public boolean isSuccess() {
        return exception == null;
    }
}
