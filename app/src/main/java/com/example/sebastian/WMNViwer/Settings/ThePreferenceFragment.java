package com.example.sebastian.WMNViwer.Settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.sebastian.mapbox.R;

/**
 * Created by sebastian on 30.01.17.
 */
public class ThePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_preference);
    }
}
