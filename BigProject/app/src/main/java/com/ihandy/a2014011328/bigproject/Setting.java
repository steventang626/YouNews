package com.ihandy.a2014011328.bigproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {

   // private CheckBoxPreference mCheckPreference;
    private SwitchPreference ss1;
    private SwitchPreference ss2;
    private SwitchPreference ss3;
    private SwitchPreference ss4;
    private SwitchPreference ss5;
    private SwitchPreference ss6;
    private SwitchPreference ss7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
    }

    private void initPreferences() {
        //mCheckPreference = (CheckBoxPreference) findPreference("checkbox_key");
        ss1 = (SwitchPreference) findPreference("s1");
        ss2 = (SwitchPreference) findPreference("s2");
        ss3 = (SwitchPreference) findPreference("s3");
        ss4 = (SwitchPreference) findPreference("s4");
        ss5 = (SwitchPreference) findPreference("s5");
        ss6 = (SwitchPreference) findPreference("s6");
        ss7 = (SwitchPreference) findPreference("s7");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
//        ss1.setSummary(sharedPreferences.getString("s1", ""));
//        mEtPreference.setSummary(sharedPreferences.getString(Consts.EDIT_KEY, "linc"));

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//        if (key.equals(Consts.EDIT_KEY)) {
//            mEtPreference.setSummary(
//                    sharedPreferences.getString(key, "20"));
//        } else if(key.equals(Consts.LIST_KEY)) {
//            mListPreference.setSummary(sharedPreferences.getString(key, ""));
//        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra("gl","Good Luck!");
        setResult(RESULT_OK, i);
        finish();
    }

}