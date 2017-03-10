package com.example.informatik.cognitizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.informatik.cognitizer.Tasks.EnrollUserTask;
import com.example.informatik.cognitizer.Tasks.EnrollUserTaskResult;
import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;

import java.io.File;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;

public class EnrollUserAudioCallback implements IConvertCallback {
    private final Context context;
    private ProgressDialog dialog;

    public EnrollUserAudioCallback(Context context, ProgressDialog dialog) {
        this.context = context;
        this.dialog = dialog;
    }

    @Override
    public void onSuccess(File convertedFile) {
        //Send audio in correct format to Microsoft API
        new EnrollUserTask(new SpeakerIdentificationRestClient(context.getString(R.string.speakerRecognitionKey)), new EnrollUserTask.PostExecuteCallback() {
            @Override
            public void onSuccess(EnrollUserTaskResult result) {
                dialog.dismiss();
                if (result.isSuccess()) {
                    //User enrolled
                    //TODO Login user
                    Toast.makeText(context, result.getEnrollmentStatus().toString(), Toast.LENGTH_LONG).show();

                    //TODO Username?

                    //Start Analyse activity
                    context.startActivity(new Intent(context, AnalyzeActivity.class));
                } else {
                    //User didn't speak long enough, no internet connection or some other error occured
                    ExceptionHandler.handleException(context, result.getException());
                }
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                ExceptionHandler.handleException(context, e);
            }
        }).execute(convertedFile);
    }

    @Override
    public void onFailure(Exception error) {
        dialog.dismiss();
        ExceptionHandler.handleException(context, error);
    }
}
