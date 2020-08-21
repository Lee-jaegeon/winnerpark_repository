package com.now9e0n.winnerpark;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private AppManager app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (AppManager) getApplication();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (app.getUser() != null) app.saveUser();
    }
}