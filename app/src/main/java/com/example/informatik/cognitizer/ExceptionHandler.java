package com.example.informatik.cognitizer;

import android.content.Context;
import android.widget.Toast;

public class ExceptionHandler {
    public static void handleException(Context context, Exception e) {
        //TODO shared messaging class
        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
