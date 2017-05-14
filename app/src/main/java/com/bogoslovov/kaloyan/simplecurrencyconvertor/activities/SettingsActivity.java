package com.bogoslovov.kaloyan.simplecurrencyconvertor.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.bogoslovov.kaloyan.simplecurrencyconvertor.R;
import com.bogoslovov.kaloyan.simplecurrencyconvertor.fragments.SettingsFragment;

/**
 * Created by kaloqn on 3/23/17.
 */

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.action_settings);
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
