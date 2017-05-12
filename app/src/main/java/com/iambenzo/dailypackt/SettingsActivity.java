package com.iambenzo.dailypackt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;

import java.util.Calendar;

/**
 * Shows the settings page
 * and creates a preference change listener
 *
 * @author Ben
 * @version 1.0
 * @since 11/05/2017
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if(s.equals("pref_notify")) {

            //Set up alarm manager with a pending intent
            if(sharedPreferences.getBoolean("pref_notify", false)) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
                alarmManager.cancel(pendingIntent);

                //set up intent trigger time (09:00)
                Calendar alarmStartTime = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                alarmStartTime.set(Calendar.HOUR_OF_DAY, 9);
                alarmStartTime.set(Calendar.MINUTE, 00);
                alarmStartTime.set(Calendar.SECOND, 0);
                if (now.after(alarmStartTime)) {
                    //add a day
                    alarmStartTime.add(Calendar.DATE, 1);
                }

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
            } else {
                //cancel all pending intents with alarm manager
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent alarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmIntent.setData((Uri.parse("custom://"+System.currentTimeMillis())));
                alarmManager.cancel(pendingIntent);
            }

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        public void onCreate(Bundle bundle)
        {
            super.onCreate(bundle);
            addPreferencesFromResource(R.xml.preferences);
        }
    }


}
