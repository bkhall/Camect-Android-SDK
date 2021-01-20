package com.camect.android.sdk.example.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.camect.android.sdk.R;
import com.camect.android.sdk.example.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}