package mohit.codex_iter.www.awol;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;

@SuppressWarnings("ALL")
public class SettingsFragment extends PreferenceFragment {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private boolean flag = true;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);
        final SwitchPreference notifications = (SwitchPreference) findPreference("pref_notification");

        final SharedPreferences stop = getActivity().getSharedPreferences("STOP", 0);
        final SharedPreferences.Editor editor1 = stop.edit();

        SharedPreferences device_time = getActivity().getSharedPreferences("Set_time", 0);
        final SharedPreferences.Editor set_time = device_time.edit();

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Notification_date", 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
<<<<<<< Updated upstream

=======
        coordinatorLayout=(CoordinatorLayout) getActivity().findViewById(R.id.coordinator);
>>>>>>> Stashed changes
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
                            int set_t = calendar.get(Calendar.HOUR_OF_DAY);
                            set_time.putInt("Set_Time", set_t);
                            set_time.apply();
                            SimpleDateFormat present_date = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            String present_d = present_date.format(alram_time);

                            String fired_date = sharedPreferences.getString("Date", "");
                            if (!fired_date.equals(null) && !fired_date.isEmpty()) {
                                if (!fired_date.equals(present_d)) {
                                    Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent intent1 = new Intent();
                                        String packageName = getActivity().getPackageName();
                                        PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                                        if (pm.isDeviceIdleMode()) {
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                            if (alarmManager != null) {
                                                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                            }
                                        } else {
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                            if (alarmManager != null) {
                                                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                            }
                                        }
                                    } else {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }

                                } else {
                                    Toast.makeText(getActivity(), "Notifications set for tomorrow!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent1 = new Intent();
                                    String packageName = getActivity().getPackageName();
                                    PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                                    if (pm.isDeviceIdleMode()) {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                        }
                                    } else {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }
                                } else {

                                    Toast.makeText(getActivity(), "Notifications set", Toast.LENGTH_SHORT).show();
                                    intent = new Intent(getActivity(), AlramReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    if (alarmManager != null) {
                                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Notifications Enabled", Toast.LENGTH_SHORT).show();
                            /*Alram time*/
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, 7);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            int set_t = calendar.get(Calendar.HOUR_OF_DAY);
                            set_time.putInt("Set_Time", set_t);
                            set_time.apply();

                            Date date = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                            String present_d = simpleDateFormat.format(date);

                            String fired_date = sharedPreferences.getString("Date", null);
                            if (fired_date == null) {
                                Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent1 = new Intent();
                                    String packageName = getActivity().getPackageName();
                                    PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                                    if (pm.isDeviceIdleMode()) {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                        }
                                    } else {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }
                                } else {
                                    //AutoStartPermissionHelper.getInstance().getAutoStartPermission(getActivity();
                                    intent = new Intent(getActivity(), AlramReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    if (alarmManager != null) {
                                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                    }
                                }

                            } else if (!fired_date.equals(present_d)) {
                                Intent intent = new Intent(getActivity(), AlramReceiver.class);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Intent intent1 = new Intent();
                                    String packageName = getActivity().getPackageName();
                                    PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                                    if (pm.isDeviceIdleMode()) {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                                        }
                                    } else {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }
                                } else {
                                    intent = new Intent(getActivity(), AlramReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                    if (alarmManager != null) {
                                        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                    }
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

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void toggleTheme(boolean darkTheme) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
    }

}