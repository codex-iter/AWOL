package com.codex_iter.www.awol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings("ALL")
public class SettingsFragment extends PreferenceFragment {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private boolean flag = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        final SwitchPreference darkmode = (SwitchPreference) findPreference("pref_dark_mode");
        final SwitchPreference notifications = (SwitchPreference) findPreference("pref_notification");

        final SharedPreferences stop = getActivity().getSharedPreferences("STOP", 0);
        final SharedPreferences.Editor editor1 = stop.edit();

        SharedPreferences device_time = getActivity().getSharedPreferences("Set_time", 0);
        final SharedPreferences.Editor set_time = device_time.edit();

        if (darkmode != null) {
            darkmode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = (Boolean) newValue;

                    toggleTheme(checked);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().recreate();
                    startActivity(intent);
                    return true;
                }
            });
        }
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Notification_date", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (notifications != null && notifications.isChecked()) {
            editor1.putBoolean("STOP_NOTIFICATION", false);
            editor1.apply();
        }

        if (notifications != null) {
            notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean checked = (Boolean) newValue;

                    if (checked) {
                        editor1.putBoolean("STOP_NOTIFICATION", false);
                        editor1.apply();
                        if (!flag) {
                            Toast.makeText(getActivity(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
                            Calendar calendar = Calendar.getInstance();
                            Date alram_time = new Date();
                            calendar.set(Calendar.HOUR_OF_DAY, 7);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            SimpleDateFormat present_date = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            String present_d = present_date.format(alram_time);

                            String fired_date = sharedPreferences.getString("Date", "");
                            if (!fired_date.equals(null) && !fired_date.isEmpty()) {
                                if (!fired_date.equals(present_d)) {
                                 //   Toast.makeText(getActivity(), "Next Day", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    if (alarmManager != null) {
                                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                    }
                                }else {
                                    Toast.makeText(getActivity(), "Notifications set for tomorrow!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Notifications set", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
                            /*Alram time*/
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 7);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            int set_t = calendar.get(Calendar.HOUR);
                            set_time.putInt("Set_Time", set_t);
                            set_time.apply();

                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            String present_d = simpleDateFormat.format(date);

                            String fired_date = sharedPreferences.getString("Date", null);
                            if (fired_date == null) {
                              //  Toast.makeText(getActivity(), "First fire", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                if (alarmManager != null) {
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                }
                            } else if (!fired_date.equals(present_d)) {
                              //  Toast.makeText(getActivity(), "Next Day", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                if (alarmManager != null) {
                                    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                }

                            } else {
                                Toast.makeText(getActivity(), "Notifications set for tomorrow!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Notifications Disabled", Toast.LENGTH_SHORT).show();
                        flag = false;
                        editor1.putBoolean("STOP_NOTIFICATION", true);
                        editor1.apply();

                        Intent intent = new Intent(getActivity(), AlramReceiver.class);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                        if (alarmManager != null) {
                            alarmManager.cancel(pendingIntent);
                        }
                    }
                    return true;
                }
            });
        }
        SharedPreferences theme = getActivity().getSharedPreferences("theme", 0);
        SharedPreferences.Editor editor2 = theme.edit();
        editor2.putBoolean("dark_theme", useDarkTheme);
        editor2.apply();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();

        Intent intent = getActivity().getIntent();
        getActivity().finish();

        startActivity(intent);
    }

}