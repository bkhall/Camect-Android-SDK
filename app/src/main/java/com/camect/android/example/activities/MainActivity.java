package com.camect.android.example.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.camect.android.example.BuildConfig;
import com.camect.android.library.CamectSDK;
import com.camect.android.example.R;
import com.camect.android.example.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CamectSDK.setLoggingEnabled(BuildConfig.DEBUG);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }
}