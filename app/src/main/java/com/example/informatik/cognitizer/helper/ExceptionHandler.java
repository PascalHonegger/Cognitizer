package com.example.informatik.cognitizer.helper;

import android.content.Context;

public class ExceptionHandler {
    private ExceptionHandler() {
    }

    /**
     * Handle an exception by logging it and showing a feedback to the user
     *
     * @param context Context used for user feedback
     * @param e       exception to handle
     */
    public static void handleException(Context context, Exception e) {
        UserFeedbackHelper.showError(context, e.getMessage());
        e.printStackTrace();
    }
}
