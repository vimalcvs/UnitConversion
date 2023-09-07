package com.vimalcvs.calculator;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.github.mikephil.charting.BuildConfig;
import com.google.android.material.color.DynamicColors;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        SharedPreferences defaultSp = getDefaultSharedPreferences(getApplicationContext());
        int theme = defaultSp.getInt("themeSetting", 2);
        switch (theme) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            default:
                break;
        }
        NeverCrash.getInstance()
                .setDebugMode(BuildConfig.DEBUG)
                .setMainCrashHandler((t, e) -> {

                })
                .setUncaughtCrashHandler((t, e) -> {

                })
                .register(this);
    }
}
