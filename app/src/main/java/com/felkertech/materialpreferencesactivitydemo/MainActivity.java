package com.felkertech.materialpreferencesactivitydemo;

import android.app.Activity;

import com.felkertech.materialpreferencesactivity.MaterialPreferencesActivity;
import com.felkertech.settingsmanager.SettingsManager;
import com.jenzz.materialpreference.Preference;

public class MainActivity extends MaterialPreferencesActivity {

    @Override
    public void onPreferencesLoaded(final MaterialPreferencesFragment fragment) {
        fragment.bindSummary(R.string.sm_text1, EDIT_TEXT_PREF);
        fragment.bindSummary(R.string.sm_number1, EDIT_NUMBER_PREF);
        fragment.bindMarshmallowCategory(R.string.marsh);
        fragment.bindSummary(R.string.sm_radio1, RADIO_LIST_PREF, R.array.radiokeys1, R.array.radio1);
        fragment.enablePreference(R.string.sm_switch2, R.string.sm_switch1);
        fragment.enablePreference(R.string.secret, R.string.SM_DEVELOPER, true);
        fragment.bindAbout(R.string.action_about, new DeveloperMode() {
            @Override
            public void onDeveloperModeEnabled() {
                fragment.enableDeveloperMode();
            }
        });
        fragment.setRefreshTargets(new com.jenzz.materialpreference.Preference[] {
                (Preference) fragment.findPreference(getString(R.string.sm_switch2))
        });
        fragment.findPreference(getString(R.string.marsh)).setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(android.preference.Preference preference) {
                fragment.gotoAppDetails();
                return true;
            }
        });
    }

    @Override
    public int getBackgroundColor() {
        return R.color.colorPrimary;
    }

    @Override
    public int getPreferencesXml() {
        return R.xml.prefs;
    }

    @Override
    public SettingsManager getSettingsManager() {
        return new SettingsManager(MainActivity.this);
    }

    @Override
    public Activity getActivity() {
        return MainActivity.this;
    }

    @Override
    public Class getStringsClass() {
        return R.string.class;
    }
}
