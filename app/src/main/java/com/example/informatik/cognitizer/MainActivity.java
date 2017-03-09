package com.example.informatik.cognitizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MainActivity extends Activity {

    private File outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkAndGetPermissions()) {
            Toast.makeText(this, "Permissions are required for this app to function properly!", Toast.LENGTH_LONG).show();
        }

        final Context context = this;

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception e) {
                ExceptionHandler.handleException(context, e);
            }
        });
    }

    private MediaRecorder recorder;

    /**
     * @return True if all permissions are received
     */
    private boolean checkAndGetPermissions() {

        List<String> missingPermissions = new ArrayList<>();

        addPermissionIfNotGranted(missingPermissions, Manifest.permission.INTERNET);
        addPermissionIfNotGranted(missingPermissions, Manifest.permission.READ_EXTERNAL_STORAGE);
        addPermissionIfNotGranted(missingPermissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        addPermissionIfNotGranted(missingPermissions, Manifest.permission.CAMERA);
        addPermissionIfNotGranted(missingPermissions, Manifest.permission.RECORD_AUDIO);

        if(missingPermissions.size() > 0) {
            requestPermissions((String[]) missingPermissions.toArray(), 42);
            return false;
        } else {
            return true;
        }
    }

    private void addPermissionIfNotGranted(List<String> missingPermissions, String permission) {
        if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(permission);
        }
    }

    public void switchRecordingState(View v) {
        if(!checkAndGetPermissions()) {
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) v;
        if(recorder == null) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mic_activate));
            startRecording();
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mic_deactivate));
            stopRecording();
        }
    }

    private void startRecording() {
        recorder = new MediaRecorder();

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
            ExceptionHandler.handleException(this, e);
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(outputFile)

                // Your desired audio format
                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(new ConvertAudioCallback(this))

                // Start conversion
                .convert();
    }
}
