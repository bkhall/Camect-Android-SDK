package com.camect.android.sdk.example.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.camect.android.sdk.BuildConfig;
import com.camect.android.sdk.CamectSDK;
import com.camect.android.sdk.R;
import com.camect.android.sdk.example.fragments.MainFragment;

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