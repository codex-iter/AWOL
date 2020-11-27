package codex.codex_iter.www.awol.setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.SwitchPreference;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.Student;
import codex.codex_iter.www.awol.reciever.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.POWER_SERVICE;
import static codex.codex_iter.www.awol.utilities.Constants.API;

@SuppressWarnings("ALL")
public class SettingsFragment extends PreferenceFragment {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private boolean flag = true;
    private boolean dark = false;
    private FirebaseAnalytics firebaseAnalytics;
    ScrollView scrollViewLayout;
    private SharedPreferences sharedPreferences;
    private LocalDB localDB;
    private Student preferred_student;
    private AlertDialog resetPasswordDialog;
    private TextInputEditText new_password, confirm_password;
    private MaterialButton update_password;
    private ProgressBar progressBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        localDB = new LocalDB(getContext());

        sharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());

        preferred_student = localDB.getStudent(sharedPreferences.getString("pref_student", null));

        scrollViewLayout = (ScrollView) getActivity().findViewById(R.id.bottomSheet_view);

        final SwitchPreference notifications = (SwitchPreference) findPreference("pref_notification");
        final SwitchPreference pref_show_attendance_stats = (SwitchPreference) findPreference("pref_show_attendance_stats");
        final SwitchPreference pref_extended_stats = (SwitchPreference) findPreference("pref_extended_stats");
        ListPreference pref_minimum_attendance = (ListPreference) findPreference("pref_minimum_attendance");
        final Preference pref_reset_password = (Preference) findPreference("pref_reset_password");

        if (!sharedPreferences.getBoolean("pref_show_attendance_stats", false)) {
            pref_minimum_attendance.setVisible(false);
            pref_extended_stats.setVisible(false);
        } else {
            pref_minimum_attendance.setVisible(true);
            pref_extended_stats.setVisible(true);
        }
        if (pref_show_attendance_stats != null) {
            pref_show_attendance_stats.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Boolean checked = (Boolean) newValue;
                    if (!checked) {
                        pref_minimum_attendance.setVisible(false);
                        pref_extended_stats.setVisible(false);
                    } else {
                        pref_minimum_attendance.setVisible(true);
                        pref_extended_stats.setVisible(true);
                    }
                    sharedPreferences.edit().putBoolean("pref_show_attendance_stats", checked).apply();
                    return true;
                }
            });
        }

        pref_reset_password.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                View resetPasswordView = LayoutInflater.from(getContext()).inflate(R.layout.layout_reset_password, null);
                resetPasswordDialog = new MaterialAlertDialogBuilder(getContext())
                        .setCancelable(true)
                        .setView(resetPasswordView).create();

                new_password = resetPasswordView.findViewById(R.id.newPasswordEditText);
                confirm_password = resetPasswordView.findViewById(R.id.confirmPasswordEditText);
                update_password = resetPasswordView.findViewById(R.id.updatePassword);
                progressBar = resetPasswordView.findViewById(R.id.progress_bar);

                new_password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (new_password.getText().toString().equals(confirm_password.getText().toString())
                                && !new_password.getText().toString().isEmpty()) {
                            update_password.setVisibility(View.VISIBLE);
                            update_password.setEnabled(true);
                        } else {
                            update_password.setVisibility(View.INVISIBLE);
                            update_password.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (new_password.getText().toString().equals(confirm_password.getText().toString())
                                && !new_password.getText().toString().isEmpty()) {
                            update_password.setVisibility(View.VISIBLE);
                            update_password.setEnabled(true);
                        } else {
                            update_password.setVisibility(View.INVISIBLE);
                            update_password.setEnabled(false);
                        }
                    }
                });

                confirm_password.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (confirm_password.getText().toString().equals(new_password.getText().toString())
                                && !confirm_password.getText().toString().isEmpty()) {
                            update_password.setVisibility(View.VISIBLE);
                            update_password.setEnabled(true);
                        } else {
                            update_password.setVisibility(View.INVISIBLE);
                            update_password.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (new_password.getText().toString().equals(confirm_password.getText().toString())
                                && !confirm_password.getText().toString().isEmpty()) {
                            update_password.setVisibility(View.VISIBLE);
                            update_password.setEnabled(true);
                        } else {
                            update_password.setVisibility(View.INVISIBLE);
                            update_password.setEnabled(false);
                        }
                    }
                });
                resetPasswordDialog.show();

                update_password.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (preferred_student == null)
                            throw new InvalidResponseException();
                        try {
                            resetPasswordDialog.setCancelable(false);
                            progressBar.setVisibility(View.VISIBLE);
                            update_password.setVisibility(View.GONE);
                            updatePasswordAPI(sharedPreferences.getString(API, null), preferred_student.getRedgNo(), preferred_student.getPassword(), new_password.getText().toString());
                        } catch (InvalidResponseException e) {
                            resetPasswordDialog.dismiss();
                            Snackbar.make(scrollViewLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            resetPasswordDialog.dismiss();
                            Log.d("Setting", e.getMessage());
                            Snackbar.make(scrollViewLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        });

        final SharedPreferences stop = getContext().getSharedPreferences("STOP", 0);
        final SharedPreferences.Editor editor1 = stop.edit();

        SharedPreferences device_time = getContext().getSharedPreferences("Set_time", 0);
        final SharedPreferences.Editor set_time = device_time.edit();

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
                    // TODO check powerManager onResume()
                    boolean checked = (Boolean) newValue;
                    String packageName = getContext().getPackageName();
                    PowerManager pm = (PowerManager) getContext().getSystemService(POWER_SERVICE);
                    Snackbar allowBatterySnackBar = Snackbar.make(scrollViewLayout, "Allow app to run in background to get Notifications", Snackbar.LENGTH_INDEFINITE);

                    if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                        if (scrollViewLayout != null) {
                            notifications.setChecked(false);
                            allowBatterySnackBar.setActionTextColor(Color.RED).setAction("Allow", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    openPowerSettings(getContext());
                                }
                            }).show();
                        }
                    }

                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
                        if (checked) {
                            editor1.putBoolean("STOP_NOTIFICATION", false);
                            editor1.apply();
                            if (!flag) {
                                if (scrollViewLayout != null) {
                                    Snackbar.make(scrollViewLayout, "Notifications Enabled", Snackbar.LENGTH_SHORT).show();
                                }
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
                                        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            Intent intent1 = new Intent();
                                            pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
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

                                    }
                                } else {
                                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent intent1 = new Intent();
                                        pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
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
                                        intent = new Intent(getActivity(), AlarmReceiver.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }
                                }
                            } else {
                                if (scrollViewLayout != null) {
                                    Snackbar.make(scrollViewLayout, "Notifications Enabled", Snackbar.LENGTH_LONG).show();
                                }
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
                                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent intent1 = new Intent();
                                        pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
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
                                        intent = new Intent(getActivity(), AlarmReceiver.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }

                                } else if (!fired_date.equals(present_d)) {
                                    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Intent intent1 = new Intent();
                                        pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
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
                                        intent = new Intent(getActivity(), AlarmReceiver.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                                        if (alarmManager != null) {
                                            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                        }
                                    }

                                }
                            }
                        } else {
                            if (scrollViewLayout != null) {
                                Snackbar.make(scrollViewLayout, "Notifications Disabled", Snackbar.LENGTH_LONG).show();
                            }
                            flag = false;
                            editor1.putBoolean("STOP_NOTIFICATION", true);
                            editor1.apply();

                            Intent intent = new Intent(getActivity(), AlarmReceiver.class);

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                            if (alarmManager != null) {
                                alarmManager.cancel(pendingIntent);
                            }
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
        dark = true;
        editor.apply();
    }

    private void openPowerSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        context.startActivity(intent);
    }

    private void updatePasswordAPI(String... param) {
        if (param[0] == null)
            param[0] = this.sharedPreferences.getString(API, "");

        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/updatePassword", response -> {

            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            resetPasswordDialog.setCancelable(true);
            resetPasswordDialog.dismiss();

            if (response.equals("404")) {
                Log.d("Setting", response);
                resetPasswordDialog.dismiss();
                Snackbar.make(scrollViewLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject res = new JSONObject(response);
                    preferred_student.setPassword(new_password.getText().toString());
                    localDB.setStudent(sharedPreferences.getString("pref_student", null), preferred_student);
                    Snackbar.make(scrollViewLayout, res.getString("Success"), Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Log.d("Setting", e.getMessage());
                    resetPasswordDialog.dismiss();
                    Snackbar.make(scrollViewLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            resetPasswordDialog.setCancelable(true);
            resetPasswordDialog.dismiss();
            Log.d("Setting", error.getMessage());
            Snackbar.make(scrollViewLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);
                params.put("npass", param[3]);
                return params;
            }
        };
        queue.add(postRequest);
    }
}