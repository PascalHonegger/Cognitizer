package com.example.informatik.cognitizer.helper;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class UserFeedbackHelper {
    private UserFeedbackHelper() {
    }

    /**
     * shows a error message to the user
     *
     * @param context context for user feedback
     * @param message massage to display
     */
    public static void showError(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * shows a warning message to the user
     *
     * @param context         context for user feedback
     * @param title           titel of warning
     * @param message         message of warning
     * @param okClickListener listener for the ok button
     */
    public static void showWarning(Context context, String title, String message, DialogInterface.OnClickListener okClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle(title).setMessage(message).setCancelable(false).setNeutralButton("Ok", okClickListener).show();
    }

    /**
     * shows a information message to the user
     *
     * @param context context for user feedback
     * @param message information message
     */
    public static void showInformation(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * displays a progress indicator
     *
     * @param context context for user feedback
     * @param title   titel of the progression
     * @return
     */
    public static ProgressDialog showProgress(Context context, String title) {
        ProgressDialog dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);

        //Don't cancel on back button
        dialog.setCancelable(false);

        //Can take up as long as it wants
        dialog.setIndeterminate(true);

        dialog.setTitle(title);

        dialog.show();

        //Return dialog, this way it can be closed again using the dismiss() function
        return dialog;
    }
}
