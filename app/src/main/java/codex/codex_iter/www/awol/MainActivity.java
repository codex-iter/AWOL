package codex.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.activity.AttendanceActivity;
import codex.codex_iter.www.awol.activity.BaseThemedActivity;
import codex.codex_iter.www.awol.utilities.Constants;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.DETAILS;
import static codex.codex_iter.www.awol.utilities.Constants.LOGIN;
import static codex.codex_iter.www.awol.utilities.Constants.NOATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENTBRANCH;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;


public class MainActivity extends BaseThemedActivity {

    @BindView(R.id.mainLayout)
    CoordinatorLayout mainLayout;
    @BindView(R.id.user)
    EditText user;
    @BindView(R.id.pass)
    EditText pass;
    @BindView(R.id.login_button)
    Button login;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.passordLayout)
    TextInputLayout passLayout;
    @BindView(R.id.bottomSheet_view)
    ConstraintLayout bottomSheetView;
    @BindView(R.id.hello)
    TextView welcomeMessage;
    @BindView(R.id.manual)
    MaterialTextView maual;
    @BindView(R.id.manaul_layout)
    ConstraintLayout manual_layout;

    private SharedPreferences userm, logout, apiUrl;
    private SharedPreferences.Editor edit;
    private boolean track;
    private String param_1, response_d;
    private String studentName, student_branch;
    private String api;
    private static final String TAG = "MainActivity";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 1011;
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean updateAvailable;
    private int read_database;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Constants.offlineDataPreference = this.getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        logout = getSharedPreferences("sub",
                Context.MODE_PRIVATE);
        preferences = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);

        //In-App Update
//        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
//
//        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
//
//        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
//            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
//                if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
//                    try {
//                        appUpdateManager.startUpdateFlowForResult(
//                                appUpdateInfo,
//                                IMMEDIATE,
//                                MainActivity.this,
//                                MY_REQUEST_CODE);
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    //FLEXIBLE
//                    try {
//                        appUpdateManager.startUpdateFlowForResult(
//                                appUpdateInfo,
//                                AppUpdateType.FLEXIBLE,
//                                MainActivity.this,
//                                MY_REQUEST_CODE);
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else {
//                autofill();
//            }
//
//        });
//        InstallStateUpdatedListener updatedListener = state -> {
//            if (state.installStatus() == InstallStatus.DOWNLOADED) {
//                updateAvailable = false;
//                popupSnackbarForCompleteUpdate();
//            }
//        };
//
//        appUpdateManager.registerListener(updatedListener);
//


        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Handler handler = new Handler();
        handler.postDelayed(() -> bottomSheetBehavior.setPeekHeight(convertDpToPixel(600)), 400);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        Bundle extras = getIntent().getExtras();
        String status = "";
        if (extras != null) {
            status = extras.getString("logout_status");
        }
        SharedPreferences status_lg = this.getSharedPreferences("status", 0);
        SharedPreferences.Editor editor = status_lg.edit();

        editor.putString("status", status);
        editor.apply();

        apiUrl = getSharedPreferences(API, MODE_PRIVATE);

//        FirebaseConfig firebaseConfig = new FirebaseConfig();
//        read_database = firebaseConfig.read_database(this);

//        if (apiUrl.getInt(READ_DATABASE, 0) < read_database) {
//            apiUrl.edit().putInt(READ_DATABASE, read_database).apply();
        CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(DETAILS);
        apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    api = documentChange.getDocument().getString(API);
                    edit = apiUrl.edit();
                    edit.putString(API, api);
                    edit.apply();
                    Log.d(TAG, api);
                }
            }
        });

        login.setOnClickListener(view -> {
            String u = user.getText().toString().trim();
            String p = pass.getText().toString().trim();

            if (u.equals("") || p.equals("")) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Enter your Details", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                welcomeMessage.setVisibility(View.VISIBLE);
                login.setVisibility(View.GONE);
                user.setEnabled(false);
                pass.setEnabled(false);
                user.setFocusable(false);
                pass.setFocusable(false);
                passLayout.setPasswordVisibilityToggleEnabled(false);
                if (!preferences.contains(STUDENT_NAME)) {
                    getName(api, u, p);
                } else {
                    getData(api, u, p);
                }
                edit = userm.edit();
                edit.putString("user", u);
                edit.putString(u + "pass", p);
                edit.putString("pass", p);
                edit.apply();
                edit = logout.edit();
                edit.putBoolean("logout", false);
                edit.apply();
            }
        });
        autofill();
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MY_REQUEST_CODE) {
//            if (resultCode != RESULT_OK) {
//                log("Update flow failed! Result code: " + resultCode);
//                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
//                        appUpdateInfo -> {
//                            if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
//                                finish();
//                            } else {
//                                Snackbar snackbar =
//                                        Snackbar.make(
//                                                findViewById(android.R.id.content),
//                                                "Update has been failed.",
//                                                Snackbar.LENGTH_INDEFINITE);
//                                snackbar.setAction("RETRY", view -> appUpdateManager.completeUpdate());
//                                snackbar.setActionTextColor(Color.RED);
//                                snackbar.show();
//                            }
//                        }
//                );
//            }
//        }
//    }

//    private void popupSnackbarForCompleteUpdate() {
//        Snackbar snackbar =
//                Snackbar.make(
//                        findViewById(android.R.id.content),
//                        "An update has just been downloaded.",
//                        Snackbar.LENGTH_INDEFINITE);
//        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
//        snackbar.setActionTextColor(Color.RED);
//        snackbar.show();
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        appUpdateManager
//                .getAppUpdateInfo()
//                .addOnSuccessListener(
//                        appUpdateInfo -> {
//                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
//                                popupSnackbarForCompleteUpdate();
//                            }
//                            if (appUpdateInfo.updateAvailability()
//                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                                // If an in-app update is already running, resume the update.
//                                try {
//                                    appUpdateManager.startUpdateFlowForResult(
//                                            appUpdateInfo,
//                                            IMMEDIATE,
//                                            this,
//                                            MY_REQUEST_CODE);
//                                } catch (IntentSender.SendIntentException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                        });
//    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public void autofill() {
        if (userm.contains("user") && userm.contains("pass") && logout.contains("logout") && !logout.getBoolean("logout", false)) {
            user.setFocusable(false);
            pass.setFocusable(false);
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            this.login.performClick();
        }
    }

    private void getData(final String... param) {
        if (param[0] == null) {
            param[0] = apiUrl.getString(API, "");
        }
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/attendance",
                response -> {
                    response_d = response;
                    if (response.equals("404")) {
                        //User Credential wrong or user doesn't exists.
                        progressBar.setVisibility(View.INVISIBLE);
                        welcomeMessage.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        passLayout.setPasswordVisibilityToggleEnabled(true);
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong credentials", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (response.equals("390")) {
                        //Attendance not present
                        Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(NOATTENDANCE, true);
                        intent.putExtra(LOGIN, true);
                        intent.putExtra(API, api);
                        intent.putExtra(READ_DATABASE, read_database);
                        startActivity(intent);
                    } else {
                        //User exists and attendance too.
                        Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                        param_1 = param[1];
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(LOGIN, true);
                        intent.putExtra(STUDENT_NAME, studentName);
                        intent.putExtra(API, api);
                        intent.putExtra(READ_DATABASE, read_database);
                        edit.putString(param[1], response);
                        edit.apply();
                        startActivity(intent);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    welcomeMessage.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    passLayout.setPasswordVisibilityToggleEnabled(true);

                    if (error instanceof AuthFailureError) {
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (Constants.offlineDataPreference.getString("StudentAttendance", null) == null) {
                            user.setEnabled(true);
                            pass.setEnabled(true);
                            user.setFocusableInTouchMode(true);
                            user.setFocusable(true);
                            pass.setFocusableInTouchMode(true);
                            pass.setFocusable(true);
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Constants.Offlin_mode = true;
                            Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (Constants.offlineDataPreference.getString("StudentAttendance", null) == null) {
                            user.setEnabled(true);
                            pass.setEnabled(true);
                            user.setFocusableInTouchMode(true);
                            user.setFocusable(true);
                            pass.setFocusableInTouchMode(true);
                            pass.setFocusable(true);
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Constants.Offlin_mode = true;
                            Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof TimeoutError) {
                        if (!track) {
                            progressBar.setVisibility(View.VISIBLE);
                            welcomeMessage.setVisibility(View.VISIBLE);
                            login.setVisibility(View.GONE);
                            user.setEnabled(false);
                            pass.setEnabled(false);
                            user.setFocusable(true);
                            pass.setFocusable(true);
                            passLayout.setPasswordVisibilityToggleEnabled(false);
                            track = true;
                            login.performClick();
                        } else {
                            if (Constants.offlineDataPreference.getString("StudentAttendance", null) == null) {
                                user.setEnabled(true);
                                pass.setEnabled(true);
                                user.setFocusableInTouchMode(true);
                                user.setFocusable(true);
                                pass.setFocusableInTouchMode(true);
                                pass.setFocusable(true);
                                Snackbar snackbar = Snackbar.make(mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                track = false;
                            } else {
                                Constants.Offlin_mode = true;
                                Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                                startActivity(intent);
                            }
                        }
                    }

                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);
                return params;
            }
        };
        queue.add(postRequest);
    }

    private void getName(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/studentinfo",
                response -> {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        Log.d("response",jobj.toString());
                        JSONArray jarr = jobj.getJSONArray("detail");
                        JSONObject jobj1 = jarr.getJSONObject(0);
                        studentName = jobj1.getString("name");
                        student_branch = jobj1.getString(STUDENTBRANCH);
                        editor = preferences.edit();
                        editor.putString(STUDENT_NAME, studentName);
                        editor.putString(STUDENTBRANCH, student_branch);
                        editor.apply();
                        getData(api, param[1], param[2]);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Cannot fetch name!!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.d("error_name", error.toString());
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);
                return params;
            }
        };
        queue.add(postRequest);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
