package codex.codex_iter.www.awol.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.Student;

import static codex.codex_iter.www.awol.utilities.Constants.API;

@SuppressWarnings("ALL")
public class SettingsFragment extends PreferenceFragmentCompat {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    private boolean flag = true;
    private boolean dark = false;
    private FirebaseAnalytics firebaseAnalytics;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreference;
    private LocalDB localDB;
    private Student preferred_student;
    private AlertDialog resetPasswordDialog;
    private TextInputEditText new_password, confirm_password;
    private MaterialButton update_password;
    private ProgressBar progressBar;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference, rootKey);

        localDB = new LocalDB(getContext());

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());

        preferred_student = localDB.getStudent(sharedPreference.getString("pref_student", null));

        linearLayout = (LinearLayout) getActivity().findViewById(R.id.coordinator);

        final SwitchPreference notifications = (SwitchPreference) findPreference("pref_notification");
        final SwitchPreference pref_show_attendance_stats = (SwitchPreference) findPreference("pref_show_attendance_stats");
        final SwitchPreference pref_extended_stats = (SwitchPreference) findPreference("pref_extended_stats");
        ListPreference pref_minimum_attendance = (ListPreference) findPreference("pref_minimum_attendance");
        final Preference pref_reset_password = (Preference) findPreference("pref_reset_password");
        ListPreference pref_theme = (ListPreference) findPreference("pref_theme");
        Preference pref_sign_out = (Preference) findPreference("pref_sign_out");
        Preference pref_privacy = (Preference) findPreference("pref_privacy");

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
                            updatePasswordAPI(sharedPreference.getString(API, null), preferred_student.getRedgNo(), preferred_student.getPassword(), new_password.getText().toString());
                        } catch (InvalidResponseException e) {
                            resetPasswordDialog.dismiss();
                            Snackbar.make(linearLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            resetPasswordDialog.dismiss();
                            Log.d("Setting", e.getMessage());
                            Snackbar.make(linearLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }
        });

        notifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = (Boolean) newValue;
                OneSignal.setSubscription(checked);
                return true;
            }
        });

        pref_sign_out.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext())
                        .setCancelable(true)
                        .setTitle("Sign Out")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                localDB.setStudent(sharedPreference.getString("pref_student", null), null);

                                FirebaseAuth.getInstance().signOut();
                                Intent intent3 = new Intent(requireContext().getApplicationContext(), MainActivity.class);
                                startActivity(intent3);
                                Toast.makeText(getContext(), "Successfully signed out", Toast.LENGTH_SHORT).show();
                                requireActivity().finish();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();
                alertDialog.show();
                return true;
            }
        });

        pref_privacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                custom_tab("https://awol-iter.flycricket.io/privacy.html");
                return true;
            }
        });

//        pref_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//            @Override
//            public boolean onPreferenceChange(Preference preference, Object newValue) {
//                setAppTheme(String.valueOf(newValue));
//                return true;
//            }
//        });
    }

    private void custom_tab(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setStartAnimations(requireActivity(), R.anim.slide_in_right, R.anim.slide_out_left);
            builder.setExitAnimations(requireActivity(), R.anim.slide_in_left, R.anim.slide_out_right);
            builder.setToolbarColor(getResources().getColor(R.color.colorAccent));
            builder.build().launchUrl(requireActivity(), Uri.parse(url));
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePasswordAPI(String... param) {
        if (param[0] == null)
            param[0] = this.sharedPreference.getString(API, "");

        RequestQueue queue = Volley.newRequestQueue(getContext().getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/updatePassword", response -> {

            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            resetPasswordDialog.setCancelable(true);
            resetPasswordDialog.dismiss();

            if (response.equals("404")) {
                Log.d("Setting", response);
                resetPasswordDialog.dismiss();
                Snackbar.make(linearLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject res = new JSONObject(response);
                    preferred_student.setPassword(new_password.getText().toString());
                    localDB.setStudent(sharedPreference.getString("pref_student", null), preferred_student);
                    Snackbar.make(linearLayout, res.getString("Success"), Snackbar.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Log.d("Setting", e.getMessage());
                    resetPasswordDialog.dismiss();
                    Snackbar.make(linearLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
                }
            }
        }, error -> {
            progressBar.setVisibility(View.GONE);
            update_password.setVisibility(View.VISIBLE);
            resetPasswordDialog.setCancelable(true);
            resetPasswordDialog.dismiss();
            Log.d("Setting", error.getMessage());
            Snackbar.make(linearLayout, "Password not updated successfully", Snackbar.LENGTH_SHORT).show();
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