package com.example.informatik.cognitizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.microsoft.cognitive.speakerrecognition.SpeakerIdentificationRestClient;
import com.microsoft.cognitive.speakerrecognition.contract.identification.EnrollmentOperation;
import com.microsoft.cognitive.speakerrecognition.contract.identification.OperationLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MainActivity extends Activity {

    private SpeakerIdentificationRestClient speakerIdentificationClient;
    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });

        speakerIdentificationClient = new SpeakerIdentificationRestClient(getString(R.string.speakerRecognitionKey));
    }

    private MediaRecorder recorder;

    public void startRecording(View v) {
        recorder = new MediaRecorder();

        int recordAudioPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if(recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    //TODO Const value
                    42);
            return;
        }

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioSamplingRate(16000);
        recorder.setAudioEncodingBitRate(16000);


        int storageWritePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(storageWritePermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    //TODO Const value
                    24);
            return;
        }

        int storageReadPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if(storageReadPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    //TODO Const value
                    22);
            return;
        }

        try {


            outputFile = File.createTempFile("Cognitizer_", "mp4", getExternalCacheDir());

            recorder.setOutputFile(outputFile.getPath());

            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording(View v) {
        if(recorder == null) {
            return;
        }

        int internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);

        if(internetPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    //TODO Const value
                    22);
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;

        final Context context = this;

        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                try {
                    AsyncTask<File, Void, String> task = new RetrieveFeedTask().execute(convertedFile);
                    Toast.makeText(context, task.get(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
                //TODO
                error.printStackTrace();
            }
        };

        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(outputFile)

                // Your desired audio format
                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }

    private static UUID createUserUUID;

    private class RetrieveFeedTask extends AsyncTask<File, Void, String> {

        private Exception exception;

        @Override
        protected String doInBackground(File... params) {
            try {
                if(createUserUUID == null) {
                    createUserUUID = speakerIdentificationClient.createProfile("en-US").identificationProfileId;
                }

                OperationLocation result = speakerIdentificationClient.enroll(new FileInputStream(params[0]), createUserUUID, true);

                EnrollmentOperation test = speakerIdentificationClient.checkEnrollmentStatus(result);
                
                return test.processingResult != null ? String.valueOf(test.processingResult.enrollmentStatus) : test.message;
            } catch (Exception e) {
                this.exception = e;

                return null;
            }
        }

        protected void onPostExecute(String result) {
            // TODO: check this.exception
            // TODO: do something with the feed
            if(exception != null) {
                exception.printStackTrace();
            }
        }
    }
}
