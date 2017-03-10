package com.example.informatik.cognitizer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cafe.adriel.androidaudioconverter.callback.IConvertCallback;

public class LoginFragment extends VoiceFragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        view.findViewById(R.id.record_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchRecordingState(v);
            }
        });

        return view;
    }

    @Override
    protected IConvertCallback getConvertCallback(Context context, ProgressDialog dialog) {
        return new AuthorizeUserAudioCallback(context, dialog);
    }
}
