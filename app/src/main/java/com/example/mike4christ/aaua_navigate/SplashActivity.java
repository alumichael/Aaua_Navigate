package com.example.mike4christ.aaua_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;



public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_MS = 2000;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            setContentView(R.layout.activity_splash);
            mHandler = new Handler();

            mRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(SplashActivity.this,Walk_throught.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            };

            mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
        }
}


