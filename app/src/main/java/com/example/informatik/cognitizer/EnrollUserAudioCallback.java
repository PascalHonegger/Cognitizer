package com.example.informatik.cognitizer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.informatik.cognitizer.Tasks.EnrollUserTask;
import com.example.informatik.cognitizer.Tasks.EnrollUserTaskResult;
import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;

public class EnrollUserAudioCallback implements IConvertCallback {
    private final Context context;

    public EnrollUserAudioCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(File convertedFile) {
        try {
            //Send audio in correct format to Microsoft API
            AsyncTask<File, Void, EnrollUserTaskResult> task = new EnrollUserTask(new SpeakerIdentificationRestClient(context.getString(R.string.speakerRecognitionKey))).execute(convertedFile);

            EnrollUserTaskResult result = task.get();

            if(result.isSuccess()) {
                //User enrolled
                //TODO Login user
                Toast.makeText(context, result.getEnrollmentStatus().toString(), Toast.LENGTH_LONG).show();

                //TODO Username?

                //Start Analyse activity
                context.startActivity(new Intent(context, AnalyseActivity.class));
            } else {
                //User didn't speak long enough, no internet connection or some other error occured
                ExceptionHandler.handleException(context, result.getException());
            }
        } catch (InterruptedException | ExecutionException e) {
            ExceptionHandler.handleException(context, e);
        }
    }

    @Override
    public void onFailure(Exception error) {
        ExceptionHandler.handleException(context, error);
    }
}
