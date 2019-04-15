package com.demo.ganktest;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.demo.ganktest.R;

public class SettingFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }
}
