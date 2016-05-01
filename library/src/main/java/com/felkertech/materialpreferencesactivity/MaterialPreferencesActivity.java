package com.felkertech.materialpreferencesactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.prefs.MaterialEditTextPreference;
import com.felkertech.settingsmanager.SettingsManager;
import com.jenzz.materialpreference.PreferenceCategory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A {@link PreferenceActivity} that presents a set of application settings with Material Design
 * libraries already integrated.
 */
public abstract class MaterialPreferencesActivity extends AppCompatActivity {
    /**
     * A constant for stating this preference is for text input
     */
    public static final int EDIT_TEXT_PREF = 2;
    /**
     * A constant stating this preference is for number input
     */
    public static final int EDIT_NUMBER_PREF = 5;
    /**
     * A constant stating this preference is for radio buttons
     */
    public static final int RADIO_LIST_PREF = 3;
    /**
     * A constant stating this preference is for checkboxes
     */
    public static final int CHECK_LIST_PREF = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_settings);
        findViewById(R.id.pref_fragment).setBackgroundColor(getResources().getColor(android.R.color.background_light));
        View pref_background_view = findViewById(R.id.pref_background_view);
        if(pref_background_view != null) {
            pref_background_view.setBackgroundColor(getBackgroundColor());
        }
        getFragmentManager().beginTransaction().replace(R.id.pref_fragment, getPreferenceFragment()).addToBackStack(null).commit();
    }

    /**
     * When preferences are displayed, this method will assign them behaviors
     * @param fragment The MaterialPreferenceFragment containing relevant methods
     */
    public abstract void onPreferencesLoaded(MaterialPreferencesFragment fragment);

    /**
     * Returns the color that will be displayed in the background on larger screens
     * @return A resource int pointing to your color
     */
    public abstract int getBackgroundColor();

    /**
     * Returns the xml file that displays your preferences
     * @return A resource int pointing to your xml
     */
    public abstract int getPreferencesXml();

    /**
     * Allows you to set a SettingsManager or a child of that class such as WearSettingsManager
     */
    public abstract SettingsManager getSettingsManager();

    /**
     * Get your app's context and resources. Return `this`
     * @return Your MaterialPreferencesActivity
     */
    public abstract Activity getActivity();

    /**
     * This is for compatibility. Return `R.string.class`
     * @return Strings resources class
     */
    public abstract Class getStringsClass();

    /**
     * Changes the fragment that is displayed; defaults to built-in one
     */
    public PreferenceFragment getPreferenceFragment() {
        return new MaterialPreferencesFragment();
    }

    public static class MaterialPreferencesFragment extends PreferenceFragment {
        private SettingsManager sm;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(((MaterialPreferencesActivity) getActivity()).getPreferencesXml());
            sm = getSettingsManager();
            ((MaterialPreferencesActivity) getActivity()).onPreferencesLoaded(this);
        }

        public Activity getAppActivity() {
            return ((MaterialPreferencesActivity) getActivity()).getActivity();
        }
        public Class getStringsClass() {
            return ((MaterialPreferencesActivity) getActivity()).getStringsClass();
        }

        /**
         * Will display the current value as the preference's summary and give an action to edit
         * @param preference_key The resId of the preference you want to bind
         * @param preference_type A constant indicating the preference type
         */
        public void bindSummary(final int preference_key, final int preference_type) {
            final String TAG = "AppSettings";
            switch(preference_type) {
                case EDIT_TEXT_PREF:
                    final com.jenzz.materialpreference.Preference p = (com.jenzz.materialpreference.Preference) findPreference(getString(preference_key));
                    p.setSummary(sm.getString(preference_key));
                    p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            new MaterialDialog.Builder(getActivity())
                                    .title(p.getTitle())
                                    .content("")
                                    .inputType(InputType.TYPE_CLASS_TEXT)
                                    .input("", sm.getString(preference_key), new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            sm.setString(preference_key, input.toString());
                                            p.setSummary(sm.getString(preference_key));
                                            Log.d(TAG, "New value " + input);
                                        }
                                    }).show();
                            return false;
                        }
                    });
                    break;
                case EDIT_NUMBER_PREF:
                    final com.jenzz.materialpreference.Preference p2 = (com.jenzz.materialpreference.Preference) findPreference(getString(preference_key));
                    p2.setSummary(sm.getString(preference_key));
                    p2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            new MaterialDialog.Builder(getActivity())
                                    .title(p2.getTitle())
                                    .content("")
                                    .inputType(InputType.TYPE_CLASS_NUMBER)
                                    .input("", sm.getString(preference_key), new MaterialDialog.InputCallback() {
                                        @Override
                                        public void onInput(MaterialDialog dialog, CharSequence input) {
                                            sm.setString(preference_key, input.toString());
                                            p2.setSummary(sm.getString(preference_key));
                                            Log.d(TAG, "New value " + input);
                                        }
                                    }).show();
                            return false;
                        }
                    });
                    break;
            }
        }
        /**
         * Will display the current value as the preference's summary and give an action to edit
         * @param preference_key The resId of the preference you want to bind
         * @param pref_type A constant indicating the preference type
         * @param keys_resid A resource id pointng to the array of keys which correspond to the particular value
         * @param content_resid For radio buttons or check lists, you can supply a resource id pointing to the array
         */
        public void bindSummary(final int preference_key, final int pref_type, final int keys_resid, final int content_resid) {
            final String TAG = "AppSettings3";
            Log.d(TAG, "Pref type " + pref_type);
            switch(pref_type) {
                case RADIO_LIST_PREF:
                    final String[] radio_keys = getResources().getStringArray(keys_resid);
                    final com.jenzz.materialpreference.Preference lp = (com.jenzz.materialpreference.Preference) findPreference(getString(preference_key));
                    try {
                        int a = sm.getInt(preference_key);
                    } catch(Exception e) {
                        Log.e(TAG, "Cast problems");
                        sm.setInt(preference_key, 0);
                    }
                    if(sm.getInt(preference_key) > 0) {
                        try {
                            lp.setSummary(getString(sm.getInt(preference_key)));
                        } catch(Exception e) {
                            e.printStackTrace();
                            Log.e("Settings", "Trying to print "+preference_key+" "+sm.getInt(preference_key));
                            sm.setInt(preference_key, 0);
                        }
                    }

                    lp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Log.d(TAG, "New Radio: "+preference_key + " " + getString(preference_key));
                            final String[] items = getResources().getStringArray(content_resid);
                            Log.d(TAG, Arrays.asList(items).toString());
                            int preselected = -1;
                            int preselectedIndex = 0;
                            for(String i: radio_keys) {
                                Log.d(TAG, i+" "+getStringResourceFromValue(i, getAppActivity(), getStringsClass())+" "+sm.getString(preference_key));
                                if(i.equals(sm.getString(preference_key))) {
                                    //getImageResourceIDFromName(i, getActivity()
                                    preselected = preselectedIndex;
                                }
                                preselectedIndex++;
                            }
                            Log.d(TAG, "Selected "+preselected);

                            new MaterialDialog.Builder(getAppActivity())
                                    .title(lp.getTitle())
                                    .items(items)
                                    .itemsCallbackSingleChoice(preselected, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                                            Log.d(TAG, charSequence.toString());
                                            Log.d(TAG, getStringResourceFromValue(charSequence.toString(), getAppActivity(), getStringsClass())+"");
                                            sm.setString(preference_key, radio_keys[i]); //FIXME Retrieve correct value
                                            lp.setSummary(charSequence.toString());
                                            return true;
                                        }
                                    })
                                    .show();

                            return false;
                        }
                    });
                    break;
                case CHECK_LIST_PREF:
                    Log.d(TAG, "Check list");
                    final com.jenzz.materialpreference.Preference lp2 = (com.jenzz.materialpreference.Preference) findPreference(getString(preference_key));
                    try {
                        String a = sm.getString(preference_key);
                        Log.d(TAG, a);
                    } catch(Exception e) {
                        Log.e(TAG, "Cast isssue");
                        sm.setString(preference_key, "");
                    }
                    final String[] items2 = getResources().getStringArray(content_resid);
                    final ArrayList<Integer> preselected2 = new ArrayList<>();
                    int preselectedIndex2 = 0;
                    ArrayList<String> summaryItems = new ArrayList<String>();
                    for(String i: items2) {
                        if(sm.getString(preference_key).contains(getStringResourceFromValue(i, getAppActivity(), getStringsClass())+"")) {
                            preselected2.add(preselectedIndex2);
                            summaryItems.add(getString(getStringResourceFromValue(i, getAppActivity(), getStringsClass())));
                        }
                        preselectedIndex2++;
                    }
                    if(!sm.getString(preference_key).isEmpty())
                        lp2.setSummary(TextUtils.join(",",summaryItems));

                    lp2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            Log.d(TAG, "Check listt");
                            final String[] items2 = getResources().getStringArray(content_resid);
                            Log.d(TAG, Arrays.asList(items2).toString());

                            final ArrayList<Integer> preselected2 = new ArrayList<>();
                            int preselectedIndex2 = 0;
                            ArrayList<String> summaryItems = new ArrayList<String>();
                            for (String i : items2) {
                                if (sm.getString(preference_key).contains(getStringResourceFromValue(i, getAppActivity(), getStringsClass()) + "")) {
                                    preselected2.add(preselectedIndex2);
                                    summaryItems.add(getString(getStringResourceFromValue(i, getAppActivity(), getStringsClass())));
                                }
                                preselectedIndex2++;
                            }
                            if (!sm.getString(preference_key).isEmpty())
                                lp2.setSummary(TextUtils.join(",", summaryItems));

                            new MaterialDialog.Builder(getAppActivity())
                                    .title(lp2.getTitle())
                                    .items(items2)
                                    .itemsCallbackMultiChoice(preselected2.toArray(new Integer[preselected2.size()]), new MaterialDialog.ListCallbackMultiChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog materialDialog, Integer[] integers, CharSequence[] charSequences) {
                                            Log.d(TAG, "Selected some jawn");
                                            String stored = "";
                                            for (CharSequence c : charSequences) {
                                                stored += getStringResourceFromValue(c.toString(), getAppActivity(), getStringsClass()) + "";
                                            }
                                            sm.setString(preference_key, stored);
                                            lp2.setSummary(TextUtils.join(",", charSequences));
                                            return true;
                                        }
                                    })
                                    .positiveText("OK")
                                    .cancelable(false)
                                    .show();

                            return false;
                        }
                    });
                    break;
            }
        }

        /**
         * Enables a preference depending if another preference (switch) is enabled
         * @param resId The preference you want to enable/disable
         * @param boolId The key for the preference you're testing
         * @return true if the preference is enabled
         */
        public boolean enablePreference(int resId, int boolId) {
            return enablePreference(resId, boolId, false);
        }
        /**
         * Enables a preference depending if another preference (switch) is enabled
         * @param resId The preference you want to enable/disable
         * @param boolId The key for the preference you're testing
         * @param hidePreference If true, the preference will be both disabled and hidden from the user
         * @return true if the preference is enabled
         */
        public boolean enablePreference(int resId, int boolId, boolean hidePreference) {
            final com.jenzz.materialpreference.Preference lp = (com.jenzz.materialpreference.Preference) findPreference(getString(resId));
            lp.setEnabled(sm.getBoolean(boolId));
//            lp.setShouldDisableView(hidePreference);
            if(hidePreference && !sm.getBoolean(boolId)) {
                getPreferenceScreen().removePreference(lp);
            }
            Log.d("weather:settings", "For " + getString(resId) + " enabled value is " + sm.getBoolean(boolId));
            return sm.getBoolean(boolId);
        }

        /**
         * Displays the version number and build number in a key
         * @param resId The preference key corresponding to your about preference
         * @throws PackageManager.NameNotFoundException
         */
        public void bindAbout(int resId) throws PackageManager.NameNotFoundException {
            final com.jenzz.materialpreference.Preference p =
                    (com.jenzz.materialpreference.Preference) findPreference(getString(resId));
            PackageInfo pInfo = getAppActivity().getPackageManager().getPackageInfo(getAppActivity().getPackageName(), 0);
            String v = "Version " + pInfo.versionName;
            String b = "Build " + pInfo.versionCode;
            p.setSummary(v + "\n" + b);
        }
        /**
         * Displays the version number and build number in a key. If you tap on it 7 times something can happen.
         * @param resId The preference key corresponding to your about preference
         * @param dm A callback which is run if the user taps on the preference 7 times
         * @throws PackageManager.NameNotFoundException
         */
        public void bindAbout(int resId, final DeveloperMode dm) {
            try {
                bindAbout(resId, new SettingsManager(getAppActivity()).getBoolean(R.string.SM_DEVELOPER), dm);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        /**
         * Displays the version number and build number in a key. If you tap on it 7 times something can happen.
         * @param resId The preference key corresponding to your about preference
         * @param alreadyEnabled If developer mode is already enabled, the callback won't run
         * @param dm A callback which is run if the user taps on the preference 7 times
         * @throws PackageManager.NameNotFoundException
         */
        public void bindAbout(int resId, final boolean alreadyEnabled, final DeveloperMode dm) throws PackageManager.NameNotFoundException {
            final com.jenzz.materialpreference.Preference p =
                    (com.jenzz.materialpreference.Preference) findPreference(getString(resId));
            PackageInfo pInfo = getAppActivity().getPackageManager().getPackageInfo(getAppActivity().getPackageName(), 0);
            String v = "Version " + pInfo.versionName;
            String b = "Build " + pInfo.versionCode;
            final int[] taps = {0};
            final boolean developerEnabled = sm.getBoolean(R.string.SM_DEVELOPER);
            p.setSummary(v + "\n" + b);
            p.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    taps[0]++;
                    if (taps[0] > 7 && !alreadyEnabled) {
                        Toast.makeText(getAppActivity(), "Debug mode enabled", Toast.LENGTH_SHORT).show();
                        dm.onDeveloperModeEnabled();
                        onCreate(null);
                    } else if (taps[0] > 3 && !alreadyEnabled) {
                        Toast.makeText(getAppActivity(), "You are " + (8 - taps[0]) + " steps away from debug mode", Toast.LENGTH_SHORT).show();
                    } else if (alreadyEnabled)
                        Toast.makeText(getAppActivity(), "Debug mode already enabled", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        /**
         * Removes a preference category if your device is on a lower API version
         * @param catId key for the category
         * @param sdk_int The API version you're testing against
         */
        public void bindMinApiCategory(int catId, int sdk_int) {
            String TAG = "AppSett";
            com.jenzz.materialpreference.PreferenceCategory category = (PreferenceCategory) getParent(findPreference(getString(catId)));
            Log.d(TAG, "Got category");
//            category.setShouldDisableView((Build.VERSION.SDK_INT < Build.VERSION_CODES.M));
            Log.d(TAG, "Disable view " + (Build.VERSION.SDK_INT < Build.VERSION_CODES.M));
            if(Build.VERSION.SDK_INT < sdk_int)
                getPreferenceScreen().removePreference(category);
        }

        /**
         * Removes a preference category if your device's version is older than Marshmallow, 6.0
         * @param catId key for the category
         */
        public void bindMarshmallowCategory(int catId) {
            bindMinApiCategory(catId, Build.VERSION_CODES.M);
        }
        private PreferenceGroup getParent(Preference preference) {
            return getParent(getPreferenceScreen(), preference);
        }
        public void gotoAppDetails() {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", getAppActivity().getPackageName(), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        private PreferenceGroup getParent(PreferenceGroup root, Preference preference) {
            for (int i = 0; i < root.getPreferenceCount(); i++)
            {
                Preference p = root.getPreference(i);
                if (p == preference)
                    return root;
                if (PreferenceGroup.class.isInstance(p))
                {
                    PreferenceGroup parent = getParent((PreferenceGroup)p, preference);
                    if (parent != null)
                        return parent;
                }
            }
            return null;
        }

        /**
         * Enables developer mode if you're using the built-in system
         */
        public void enableDeveloperMode() {
            getSettingsManager().setBoolean(R.string.SM_DEVELOPER, true);
        }

        /**
         * If certain preferences affect others, then the activity needs to be refreshed when a
         * preference is changed.
         * @param preferences An array of preferences, each of which will refresh the activity when
         *                    their value changes
         */
        public void setRefreshTargets(com.jenzz.materialpreference.Preference[] preferences) {
            for(com.jenzz.materialpreference.Preference p: preferences) {
                p.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Handler h = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message m) {
                                onCreate(null);
                            }
                        };
                        h.sendEmptyMessageDelayed(0, 750);
                        return true;
                    }
                });
            }
        }

        public SettingsManager getSettingsManager() {
            return ((MaterialPreferencesActivity) getAppActivity()).getSettingsManager();
        }
    }
    /*private static int getImageResourceIDFromName(String tag,
                                                 Context context) {

        int identifier = context.getResources().getIdentifier(tag,"string",context.getPackageName());
        return identifier;
    }*/
    private static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    private static int getStringResourceFromValue(String value, Context c, Class<?> stringsClass) {
        Field[] fields = stringsClass.getFields();
        String[] allStringNames = new String[fields.length];
        for (int  i =0; i < fields.length; i++) {
            allStringNames[i] = fields[i].getName();
//            Log.d("weather:a", fields[i].getName());
//            Log.d("weather:b", getResId(fields[i].getName(), R.string.class)+"");
            String s = c.getString(getResId(fields[i].getName(), stringsClass));
//            Log.d("weather:c", c.getString(getResId(fields[i].getName(), R.string.class)));
            if(s.equals(value)) {
                return getResId(fields[i].getName(), stringsClass);
            }
        }
        return -1;
    }
    public interface DeveloperMode {
        void onDeveloperModeEnabled();
    }
}
