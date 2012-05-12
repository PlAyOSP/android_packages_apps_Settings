/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.cyanogenmod;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.widget.EditText;

import java.io.IOException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener{

	CheckBoxPreference mEnableNavigationBar;
    	ListPreference mNavigationBarHeight;
    	ListPreference mNavigationBarWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navbar_settings);
        PreferenceScreen prefs = getPreferenceScreen();

	boolean hasNavBarByDefault = getActivity().getBaseContext().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        mEnableNavigationBar = (CheckBoxPreference) findPreference("nav_bar_enabled");
        mEnableNavigationBar.setChecked(Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, hasNavBarByDefault ? 1 : 0) == 1);

        // don't allow devices that must use a navigation bar to disable it
        if (hasNavBarByDefault) {
            prefs.removePreference(mEnableNavigationBar);
        }
        mNavigationBarHeight = (ListPreference) findPreference("navigation_bar_height");
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        mNavigationBarWidth = (ListPreference) findPreference("navigation_bar_width");
        mNavigationBarWidth.setOnPreferenceChangeListener(this);
    }
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mNavigationBarWidth) {
		    String newVal = (String) newValue;
		    int dp = Integer.parseInt(newVal);
		    int width = mapChosenDpToPixels(dp);
		    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_WIDTH,
		            width);
		    toggleBar();
		    return true;
		} else if (preference == mNavigationBarHeight) {
		    String newVal = (String) newValue;
		    int dp = Integer.parseInt(newVal);
		    int height = mapChosenDpToPixels(dp);
		    Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_HEIGHT,
		            height);
		    toggleBar();
		    return true;
		}
		return false;
	}
	    public int mapChosenDpToPixels(int dp) {
		switch (dp) {
		    case 48:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
		    case 42:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_42);
		    case 36:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_36);
		    case 30:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_30);
		    case 24:
		        return getResources().getDimensionPixelSize(R.dimen.navigation_bar_24);
		}
		return -1;
	    }
	 @Override
	    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
	            Preference preference) {

		if (preference == mEnableNavigationBar) {

		    Settings.System.putInt(getContentResolver(),
		            Settings.System.NAVIGATION_BAR_SHOW,
		            ((CheckBoxPreference) preference).isChecked() ? 1 : 0);

		    /*new AlertDialog.Builder(getActivity())
		            .setTitle("Reboot required!")
		            .setMessage("Please reboot to enable/disable the navigation bar properly!")
		            .setNegativeButton("I'll reboot later", null)
		            .setCancelable(false)
		            .setPositiveButton("Reboot now!", new DialogInterface.OnClickListener() {
		                @Override
		                public void onClick(DialogInterface dialog, int which) {
		                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		                    pm.reboot("New navbar");
		                }
		            })
		            .create()
		            .show();*/

		    return true;
		}
		 return super.onPreferenceTreeClick(preferenceScreen, preference);

	 }
	private void restartSystemUI() {
        	try {
            		Runtime.getRuntime().exec("pkill -TERM -f  com.android.systemui");
        	} catch (IOException e) {
                	e.printStackTrace();
        	}
    	}
	public void toggleBar() {
		boolean isBarOn = Settings.System.getInt(getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, 1) == 1;
		Settings.System.putInt(getActivity().getBaseContext().getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, isBarOn ? 0 : 1);
		Settings.System.putInt(getActivity().getBaseContext().getContentResolver(),
		        Settings.System.NAVIGATION_BAR_SHOW, isBarOn ? 1 : 0);
	}

}
