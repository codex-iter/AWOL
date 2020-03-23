package mohit.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
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

import static com.crashlytics.android.Crashlytics.log;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;
import static mohit.codex_iter.www.awol.Constants.API;
import static mohit.codex_iter.www.awol.Constants.DETAILS;
import static mohit.codex_iter.www.awol.Constants.LOGIN;
import static mohit.codex_iter.www.awol.Constants.NOATTENDANCE;
import static mohit.codex_iter.www.awol.Constants.REGISTRATION_NUMBER;
import static mohit.codex_iter.www.awol.Constants.RESULTS;
import static mohit.codex_iter.www.awol.Constants.STUDENT_NAME;


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
    private String studentName;
    private String api;
    private static final String TAG = "MainActivity";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 1011;
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean updateAvailable;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        logout = getSharedPreferences("sub",
                Context.MODE_PRIVATE);
        preferences = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);

        appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                IMMEDIATE,
                                MainActivity.this,
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    //FLEXIBLE
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.FLEXIBLE,
                                MainActivity.this,
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                autofill();
            }

        });
        maual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autofill();
            }
        });
        InstallStateUpdatedListener updatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                updateAvailable = false;
                popupSnackbarForCompleteUpdate();
            }
        };

        appUpdateManager.registerListener(updatedListener);

        Bundle extras = getIntent().getExtras();

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

        String status = "";
        if (extras != null) {
            status = extras.getString("logout_status");
        }
        SharedPreferences status_lg = this.getSharedPreferences("status", 0);
        SharedPreferences.Editor editor = status_lg.edit();

        editor.putString("status", status);
        editor.apply();

        apiUrl = getSharedPreferences(API, MODE_PRIVATE);

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
        if (preferences.contains(STUDENT_NAME)) {
            String str = preferences.getString(STUDENT_NAME, "");
            String[] split = str.split("\\s+");
            welcomeMessage.setText("Welcome {" + convertToTitleCaseIteratingChars(split[0]) + "}");
        } else {
            manual_layout.setVisibility(View.INVISIBLE);
            welcomeMessage.setText("Welcome {User}");
        }
        login.setOnClickListener(view -> {
            String u = user.getText().toString().trim();
            String p = pass.getText().toString().trim();

            if (u.equals("") || p.equals("")) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Enter your Details", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                if (haveNetworkConnection()) {
                    progressBar.setVisibility(View.VISIBLE);
                    login.setVisibility(View.GONE);
                    user.setEnabled(false);
                    pass.setEnabled(false);
                    passLayout.setPasswordVisibilityToggleEnabled(false);
                    if (!preferences.contains(STUDENT_NAME)) {
                        getname(api, u, p);
                    }
                    getData(api, u, p);
                    edit = userm.edit();
                    edit.putString("user", u);
                    edit.putString(u + "pass", p);
                    edit.putString("pass", p);
                    edit.apply();
                    edit = logout.edit();
                    edit.putBoolean("logout", false);
                    edit.apply();
                } else {
                    Snackbar snackbar = Snackbar.make(mainLayout, "Something, went wrong.Try Again", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
            }
        });
    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                log("Update flow failed! Result code: " + resultCode);
                appUpdateManager.getAppUpdateInfo().addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                                finish();
                            } else {
                                Snackbar snackbar =
                                        Snackbar.make(
                                                findViewById(android.R.id.content),
                                                "Update has been failed.",
                                                Snackbar.LENGTH_INDEFINITE);
                                snackbar.setAction("RETRY", view -> appUpdateManager.completeUpdate());
                                snackbar.setActionTextColor(Color.RED);
                                snackbar.show();
                            }
                        }
                );
            }
        }
    }

    public void autofill() {
        if (userm.contains("user") && userm.contains("pass") && logout.contains("logout") && !logout.getBoolean("logout", false)) {
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            manual_layout.setVisibility(View.INVISIBLE);
            this.login.performClick();
        }
    }

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(android.R.id.content),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                                popupSnackbarForCompleteUpdate();
                            }
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            MY_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            }

                        });
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
                        progressBar.setVisibility(View.INVISIBLE);
                        login.setVisibility(View.VISIBLE);
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        passLayout.setPasswordVisibilityToggleEnabled(true);
                        welcomeMessage.setText("Welcome {User}");
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong credentials", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (response.equals("390")) {
                        Intent intent = new Intent(MainActivity.this, home.class);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(NOATTENDANCE, true);
                        intent.putExtra(LOGIN, true);
                        Toast.makeText(this, studentName, Toast.LENGTH_SHORT).show();
                        intent.putExtra(API, api);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MainActivity.this, home.class);
                        param_1 = param[1];
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(LOGIN, true);
                        intent.putExtra(STUDENT_NAME, studentName);
                        intent.putExtra(API, api);
                        edit.putString(param[1], response);
                        edit.apply();
                        startActivity(intent);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                    passLayout.setPasswordVisibilityToggleEnabled(true);
                    if (error instanceof AuthFailureError) {
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        welcomeMessage.setText("Welcome {User}");
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        welcomeMessage.setText("Welcome {User}");
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof NetworkError) {
                        Log.e("Volley_error", String.valueOf(error));
                        Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof TimeoutError) {
                        if (!track) {
                            progressBar.setVisibility(View.VISIBLE);
                            login.setVisibility(View.GONE);
                            user.setEnabled(false);
                            pass.setEnabled(false);
                            passLayout.setPasswordVisibilityToggleEnabled(false);
                            track = true;
                            login.performClick();
                        } else {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            track = false;
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

    private void getname(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/studentinfo",
                response -> {
                    try {
                        JSONObject jobj = new JSONObject(response);
                        JSONArray jarr = jobj.getJSONArray("detail");
                        JSONObject jobj1 = jarr.getJSONObject(0);
                        studentName = jobj1.getString("name");
                        editor = preferences.edit();
                        editor.putString(STUDENT_NAME, studentName);
                        editor.apply();
                        Log.d("Student", studentName);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Cannot fetch name!!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
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

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // connected to the internet


        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
