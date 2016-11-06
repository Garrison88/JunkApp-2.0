package com.garrisonthomas.junkapp;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

    //    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_layout);

        Preference versionName = findPreference("version_name");
        Preference versionCode = findPreference("version_code");

        versionName.setSummary(BuildConfig.VERSION_NAME);
        versionCode.setSummary(String.valueOf(BuildConfig.VERSION_CODE));


    }
}
