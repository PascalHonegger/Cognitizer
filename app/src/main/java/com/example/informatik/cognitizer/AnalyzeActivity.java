package com.example.informatik.cognitizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AnalyzeActivity extends AppCompatActivity {

    private android.app.FragmentManager manager = getFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectItem(item.getItemId());

                        return true;
                    }
                });

        selectItem(R.id.action_picture);
    }

    private void selectItem(int id) {
        String newTitle = "";

        switch (id) {
            case R.id.action_text:
                manager.beginTransaction().replace(R.id.analysePlaceholder, new RecognizeTextFragment()).commit();

                newTitle = "Recognize Text";
                break;
            case R.id.action_picture:
                manager.beginTransaction().replace(R.id.analysePlaceholder, new DescribeImageFragment()).commit();

                newTitle = "Describe Image";
                break;
            default:
                Log.w("Navigation", "Unknown tab " + id + "cannot be selected");
                break;
        }

        ((TextView)findViewById(R.id.appBarTitle)).setText(newTitle);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_bar_action, menu);
        return true;
    }

    public void logout(MenuItem item) {
        finish();
    }
}
