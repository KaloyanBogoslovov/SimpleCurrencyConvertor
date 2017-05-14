package com.bogoslovov.kaloyan.simplecurrencyconvertor.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;

/**
 * Created by kaloqn on 3/23/17.
 */

public class SettingsFragment extends PreferenceFragment {
    public static boolean onlineMode = false;
    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        CheckBoxPreference pref = (CheckBoxPreference) findPreference("online-mode");
        onlineMode = pref.isChecked();
        System.out.println(onlineMode);
    }
}