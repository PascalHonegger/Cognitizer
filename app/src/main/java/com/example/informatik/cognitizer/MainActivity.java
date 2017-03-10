package com.example.informatik.cognitizer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.informatik.cognitizer.helper.ExceptionHandler;
import com.example.informatik.cognitizer.helper.PermissionsHelper;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

public class MainActivity extends AppCompatActivity {
    private android.app.FragmentManager manager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!PermissionsHelper.checkAndGetPermissions(this)) {
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

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        try {
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                            try {
                                selectItem(item.getItemId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return true;
                        }
                    });

            //Load default content
            selectItem(R.id.action_login);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectItem(int id) {
        String newTitle = "Cognitizer ";

        switch (id) {
            case R.id.action_login:
                manager.beginTransaction().replace(R.id.placeholder, new LoginFragment()).commit();

                newTitle += "Login";
                break;
            case R.id.action_register:
                manager.beginTransaction().replace(R.id.placeholder, new RegisterFragment()).commit();

                newTitle += "Register";
                break;
            default:
                Log.w("Navigation", "Unknown tab " + id + "cannot be selected");
                break;
        }

        ((TextView)findViewById(R.id.appBarTitle)).setText(newTitle);
    }
}
