package com.example.informatik.cognitizer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MainActivity extends AppCompatActivity {

    private File outputFile;
    private BottomNavigationView bottomNavigationView;
    private android.app.FragmentManager manager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_login:
                                //TODO: manager.beginTransaction().replace(R.id.placholder, new LoginFragment()).commit();

                            case R.id.action_register:
                                //TODO: manager.beginTransaction().replace(R.id.placholder, new RegisterFragment()).commit();

                        }
                        return true;
                    }
                });
    }

    private MediaRecorder recorder;

    public void switchRecordingState(View v) {
        FloatingActionButton fab = (FloatingActionButton) v;
        if(recorder == null) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mic_activate));
            startRecording();
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.mic_deactivate));
            stopRecording();
        }
    }

    public void startRecording() {
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
            ExceptionHandler.handleException(this, e);
        }
    }

    public void stopRecording() {
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

    /*public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_action, menu);
        return true;
    }*/

}
