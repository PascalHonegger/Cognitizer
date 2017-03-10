package com.example.informatik.cognitizer;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaRecorder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.example.informatik.cognitizer.helper.PermissionsHelper;
import com.example.informatik.cognitizer.helper.UserFeedbackHelper;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;

public abstract class VoiceFragmentBase extends Fragment {
    private File outputFile;

    private MediaRecorder recorder;

    protected void switchRecordingState(View v) {
        if(!PermissionsHelper.checkAndGetPermissions(getActivity())) {
            return;
        }

        FloatingActionButton fab = (FloatingActionButton) v;
        if(recorder == null) {
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.mic_activate));
            startRecording();
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.mic_deactivate));
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

        try {
            outputFile = File.createTempFile("Cognitizer_", "mp4", getContext().getExternalCacheDir());

            recorder.setOutputFile(outputFile.getPath());

            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            ExceptionHandler.handleException(getContext(), e);
        }
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;

        ProgressDialog dialog = UserFeedbackHelper.showProgress(getContext(), getString(R.string.analyzing_voice));

        AndroidAudioConverter.with(getContext())
                // Your current audio file
                .setFile(outputFile)

                // Your desired audio format
                .setFormat(cafe.adriel.androidaudioconverter.model.AudioFormat.WAV)

                // An callback to know when conversion is finished
                .setCallback(getConvertCallback(getContext(), dialog))

                // Start conversion
                .convert();
    }

    protected abstract IConvertCallback getConvertCallback(Context context, ProgressDialog dialog);
}
