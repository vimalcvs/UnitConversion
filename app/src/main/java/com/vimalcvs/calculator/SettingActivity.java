package com.vimalcvs.calculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.vimalcvs.calculator.utils.Utils;

import java.util.Objects;


public class SettingActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    SharedPreferences defaultSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_0.getColor(this));
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(SurfaceColors.SURFACE_0.getColor(this)));
        Objects.requireNonNull(getSupportActionBar()).setElevation(0f);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.settings_activity);

        defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        defaultSharedPrefs.registerOnSharedPreferenceChangeListener(this);

        if (defaultSharedPrefs.getBoolean("screen", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        defaultSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (defaultSharedPrefs.getBoolean("vib", false)) {
            Utils.vibrate(this);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat
            implements Preference.OnPreferenceClickListener {

        Preference themePref, languagePref;
        SharedPreferences defaultSharedPrefs;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            defaultSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            themePref = findPreference("theme");
            languagePref = findPreference("language");
            languagePref.setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU);
            languagePref.setOnPreferenceClickListener(this);
            themePref.setOnPreferenceClickListener(this);
        }

        @Override
        public boolean onPreferenceClick(@NonNull Preference preference) {
            if ("theme".equals(preference.getKey())) {
                SharedPreferences.Editor editor = defaultSharedPrefs.edit();
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.theme))
                        .setSingleChoiceItems(
                                new String[]{getString(R.string.light), getString(R.string.dark), getString(R.string.systemTheme)},
                                defaultSharedPrefs.getInt("themeSetting", 2), (dialog, which) -> {
                                    if (which != defaultSharedPrefs.getInt("themeSetting", 2)) {
                                        switch (which) {
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
                                    }
                                    editor.putInt("themeSetting", which);
                                    editor.apply();
                                    dialog.dismiss();
                                })
                        .setCancelable(true)
                        .setNegativeButton(getString(android.R.string.cancel), null)
                        .show();
            } else if ("language".equals(preference.getKey())) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    try {
                        Intent intent = new Intent(Settings.ACTION_APP_LOCALE_SETTINGS);
                        intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                        startActivity(intent);
                    } catch (Exception e) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.fromParts("package", requireContext().getPackageName(), null));
                            startActivity(intent);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
            return true;
        }
    }
}