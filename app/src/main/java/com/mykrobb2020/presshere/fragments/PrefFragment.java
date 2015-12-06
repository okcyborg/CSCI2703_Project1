package com.mykrobb2020.presshere.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mykrobb2020.presshere.R;

/**
 * Created by Mike on 9/21/2015.
 */
public class PrefFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
