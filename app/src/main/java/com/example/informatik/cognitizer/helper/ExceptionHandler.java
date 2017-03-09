package com.example.informatik.cognitizer.helper;

import android.content.Context;

public class ExceptionHandler {
    private ExceptionHandler() {}

    public static void handleException(Context context, Exception e) {
        UserFeedbackHelper.showError(context, e.getMessage());
        e.printStackTrace();
    }
}
