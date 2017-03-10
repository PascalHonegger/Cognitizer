package com.example.informatik.cognitizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.informatik.cognitizer.Tasks.AuthorizeUserTask;
import com.example.informatik.cognitizer.Tasks.AuthorizeUserTaskResult;
import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.example.informatik.cognitizer.helper.UserFeedbackHelper;
import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.Confidence;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;

public class AuthorizeUserAudioCallback implements IConvertCallback {
    private final Context context;

    public AuthorizeUserAudioCallback(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(File convertedFile) {
        try {
            //Send audio in correct format to Microsoft API
            AsyncTask<File, Void, AuthorizeUserTaskResult> task = new AuthorizeUserTask(new SpeakerIdentificationRestClient(context.getString(R.string.speakerRecognitionKey))).execute(convertedFile);

            AuthorizeUserTaskResult result = task.get();

            if(result.isSuccess()) {
                if(result.getConfidence() != Confidence.HIGH || result.getUserId().equals(new UUID(0L, 0L))) {
                    UserFeedbackHelper.showWarning(context, "Login failed", "Couldn't verify you!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.i("Button clicked", "OK - Login failed");
                        }
                    });
                    return;
                }

                //User enrolled
                //TODO Login user
                Toast.makeText(context, result.getUserId().toString() + " - " + result.getConfidence().toString(), Toast.LENGTH_LONG).show();

                //TODO Username?

                //Start Analyse activity
                context.startActivity(new Intent(context, AnalyzeActivity.class));
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
