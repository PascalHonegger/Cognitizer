package com.example.informatik.cognitizer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;


public class RegisterFragment extends VoiceFragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        view.findViewById(R.id.record_register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecordingState(v);
            }
        });

        return view;
    }

    @Override
    protected IConvertCallback getConvertCallback(Context context) {
        return new EnrollUserAudioCallback(context);
    }
}
