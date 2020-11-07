package codex.codex_iter.www.awol;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Status;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onesignal.OneSignal;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.activity.AttendanceActivity;
import codex.codex_iter.www.awol.activity.UnderMaintenance;
import codex.codex_iter.www.awol.exceptions.InvalidResponseFetchNameException;
import codex.codex_iter.www.awol.utilities.Constants;
import codex.codex_iter.www.awol.utilities.Utils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.DETAILS;
import static codex.codex_iter.www.awol.utilities.Constants.LOGIN;
import static codex.codex_iter.www.awol.utilities.Constants.NO_ATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_BRANCH;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;


public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {

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
    ScrollView bottomSheetView;
    @BindView(R.id.hello)
    TextView welcomeMessage;
    @BindView(R.id.manual)
    MaterialTextView maual;
    @BindView(R.id.manaul_layout)
    ConstraintLayout manual_layout;

    private SharedPreferences userm, logout, apiUrl;
    private SharedPreferences.Editor edit;
    private boolean track;
    private String studentName, student_branch, api, new_message;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private int updated_version;
    private int current_version;
    private static final int EXTERNAL_STORAGE_PERMISSION_CODE = 1002;
    private boolean isQueried = false;
    private String updatedAppID, appLink;
    private FirebaseAuth mAuth;
    private Long fileSize;
    private int downloadId;
    private boolean isDownloading;
    private File awolAppUpdateFile;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        try {
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(MainActivity.this);
        } catch (IllegalStateException e) {
            InternetAvailabilityChecker.init(this);
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(MainActivity.this);
        }

        mAuth = FirebaseAuth.getInstance();

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

        apiUrl = getSharedPreferences(API, MODE_PRIVATE);

        awolAppUpdateFile = new File(Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString() + File.separator + "awol.apk");
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Handler handler = new Handler();
        handler.postDelayed(() -> bottomSheetBehavior.setPeekHeight(convertDpToPixel(600)), 200);

        Bundle extras = getIntent().getExtras();
        String status = "";
        if (extras != null) {
            status = extras.getString("logout_status");
        }
        SharedPreferences is_logout = this.getSharedPreferences("status", 0);
        SharedPreferences.Editor editor = is_logout.edit();

        editor.putString("status", status);
        editor.apply();

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
                passLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                if (!preferences.contains(STUDENT_NAME) || !preferences.contains(STUDENT_BRANCH)) {
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
    }

    public void fetchDetails() {
        CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(DETAILS);
        apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    api = documentChange.getDocument().getString(API);
                    updated_version = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("update_available")));
                    int check = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("under_maintenance")));
                    fileSize = Long.parseLong(Objects.requireNonNull(documentChange.getDocument().getString("update_file_size")));
                    appLink = documentChange.getDocument().getString("appLink");
                    isQueried = true;
                    new_message = documentChange.getDocument().getString("what's_new");
                    updatedAppID = documentChange.getDocument().getString("download_id");
                    edit = apiUrl.edit();
                    edit.putString(API, api);
                    edit.putInt("CHECK", check);
                    edit.apply();

                    if (check == 1) {
                        Intent intent = new Intent(MainActivity.this, UnderMaintenance.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    if (updated_version > current_version && current_version > 0 && Utils.isNetworkAvailable(MainActivity.this)) {
                        downloadUpdatedApp(updatedAppID, this.new_message, appLink);
                    } else {
                        autoFill();
                        try {
                            if (awolAppUpdateFile.exists() && Utils.isNetworkAvailable(MainActivity.this)) {
                                if (awolAppUpdateFile.delete()) {
                                    Log.d("fileDeleted", "True");
                                } else {
                                    Log.d("fileDeleted", "False");
                                }
                            }
                        } catch (Exception e1) {
                            Log.d("fileDeleted", "False");
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth.getCurrentUser() != null) {
            fetchDetails();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SignIn", "Successfully");
                            fetchDetails();
                        } else {
                            Log.d("SignIn", Objects.requireNonNull(task.getException()).toString());
                            Snackbar snackbar = Snackbar.make(mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
        }
    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public void autoFill() {
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
                    if (response.equals("404")) {
                        //User Credential wrong or user doesn't exists.
                        progressBar.setVisibility(View.INVISIBLE);
                        welcomeMessage.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        passLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong credentials", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (response.equals("390")) {
                        //Attendance not present
                        Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(NO_ATTENDANCE, true);
                        intent.putExtra(LOGIN, true);
                        intent.putExtra(API, api);
                        if (mAuth.getCurrentUser() != null) {
                            startActivity(intent);
                        } else {
                            mAuth.signInAnonymously()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("SignIn", "Successfully");
                                            startActivity(intent);
                                        } else {
                                            Log.d("SignIn", Objects.requireNonNull(task.getException()).toString());
                                            Snackbar snackbar = Snackbar.make(mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    });
                        }
                    } else {
                        //User exists and attendance too.
                        Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);
                        intent.putExtra(REGISTRATION_NUMBER, user.getText().toString());
                        intent.putExtra(LOGIN, true);
                        intent.putExtra(STUDENT_NAME, studentName);
                        intent.putExtra(API, api);
                        edit.putString(param[1], response);
                        edit.apply();
                        if (mAuth.getCurrentUser() != null) {
                            startActivity(intent);
                        } else {
                            mAuth.signInAnonymously()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("SignIn", "Successfully");
                                            startActivity(intent);
                                        } else {
                                            Log.d("SignIn", Objects.requireNonNull(task.getException()).toString());
                                            Snackbar snackbar = Snackbar.make(mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    });
                        }
                    }
                },
                error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    welcomeMessage.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    passLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    if (error instanceof AuthFailureError) {
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        user.setFocusableInTouchMode(true);
                        user.setFocusable(true);
                        pass.setFocusableInTouchMode(true);
                        pass.setFocusable(true);
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
                            Constants.Offline_mode = true;
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
                            Constants.Offline_mode = true;
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
                            passLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
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
                                Constants.Offline_mode = true;
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
                        if (response.equals("404")) {
                            progressBar.setVisibility(View.INVISIBLE);
                            welcomeMessage.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            passLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                            user.setEnabled(true);
                            pass.setEnabled(true);
                            user.setFocusableInTouchMode(true);
                            user.setFocusable(true);
                            pass.setFocusableInTouchMode(true);
                            pass.setFocusable(true);
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong credentials", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            JSONObject jobj = new JSONObject(response);
                            Log.d("response", jobj.toString());
                            JSONArray jarr = jobj.getJSONArray("detail");
                            JSONObject jobj1 = jarr.getJSONObject(0);
                            if (!jobj1.has("name") || !jobj1.has(STUDENT_BRANCH)) {
                                throw new InvalidResponseFetchNameException();
                            }
                            studentName = jobj1.getString("name");
                            student_branch = jobj1.getString(STUDENT_BRANCH);
                            editor = preferences.edit();
                            Log.d("branch_portal", student_branch);
                            editor.putString(STUDENT_NAME, studentName);
                            editor.putString(STUDENT_BRANCH, student_branch);
                            editor.apply();
                            MainActivity.this.getData(api, param[1], param[2]);
                        }
                    } catch (JSONException | InvalidResponseFetchNameException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        welcomeMessage.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        passLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        user.setFocusableInTouchMode(true);
                        user.setFocusable(true);
                        pass.setFocusableInTouchMode(true);
                        pass.setFocusable(true);
                        Snackbar snackbar = Snackbar.make(mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    welcomeMessage.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                    passLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                    if (error instanceof AuthFailureError) {
                        user.setEnabled(true);
                        pass.setEnabled(true);
                        user.setFocusableInTouchMode(true);
                        user.setFocusable(true);
                        pass.setFocusableInTouchMode(true);
                        pass.setFocusable(true);
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
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot connect to ITER servers right now.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Constants.Offline_mode = true;
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
                            Constants.Offline_mode = true;
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
                            passLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
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
                                Constants.Offline_mode = true;
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

    public void downloadUpdatedApp(String updatedAppID, String new_message, String appLink) {
        if (hasPermission()) {
            try {
                if (!awolAppUpdateFile.exists())
                    FileDownloader(updatedAppID, new_message, appLink);
                else
                    Utils.updateAvailable(MainActivity.this, new_message);
            } catch (Exception e) {
                Log.e("downloadError", e.toString());
                autoFill();
            }
        } else {
            askPermission();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void askPermission() {
        if (!hasPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("Please allow to access storage")
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                            }
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).create().show();
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                }
            }

        }
    }

    private boolean hasPermission() {
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                downloadUpdatedApp(updatedAppID, this.new_message, appLink);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("Please allow to access storage. Press OK to enable in settings.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, EXTERNAL_STORAGE_PERMISSION_CODE);
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).create().show();


                } else {
                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Permission needed")
                            .setMessage("Please allow to access storage")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                                finish();
                            }).create().show();
                }
            }
        }
    }

    public void FileDownloader(String fileID, String new_message, String appLink) {
        if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
            return;
        }

        View mDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.download_updates_layout, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this)
                .setView(mDialogView);
        AlertDialog mAlertDialog;
        mAlertDialog = mBuilder.show();
        mAlertDialog.setCancelable(false);

        ProgressBar progressBar = mDialogView.findViewById(R.id.progress_bar);
        MaterialTextView update = mDialogView.findViewById(R.id.progress_update);
        MaterialTextView update_out_of_100 = mDialogView.findViewById(R.id.progress_update_100);

        File awolAppUpdateFilePath = new File(Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString());
        PRDownloader.initialize(MainActivity.this);
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(MainActivity.this, config);

        if (Status.PAUSED == PRDownloader.getStatus(downloadId)) {
            PRDownloader.resume(downloadId);
            return;
        }

        // https://drive.google.com/uc?export=download&id=" + fileID
        downloadId = PRDownloader.download(appLink, String.valueOf(awolAppUpdateFilePath), "awol.apk")
                .build()
                .setOnStartOrResumeListener(() -> {
                    isDownloading = true;
                    progressBar.setIndeterminate(false);
                    Toast.makeText(MainActivity.this, "Download Started", Toast.LENGTH_SHORT).show();
                })
                .setOnPauseListener(() -> Toast.makeText(MainActivity.this, "Download Paused", Toast.LENGTH_SHORT).show())
                .setOnCancelListener(() -> Toast.makeText(MainActivity.this, "Download Cancelled", Toast.LENGTH_SHORT).show())
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    update.setText((int) progressPercent + "%");
                    update_out_of_100.setText((int) progressPercent + "/100");
                    progressBar.setProgress((int) progressPercent);
                    progressBar.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        mAlertDialog.dismiss();
                        isDownloading = false;
                        Toast.makeText(MainActivity.this, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                        Utils.updateAvailable(MainActivity.this, new_message);
                    }

                    @Override
                    public void onError(Error error) {
                        Log.d("PRDownload Error", String.valueOf(error.getResponseCode()));
                        if (isDownloading && (Status.QUEUED == PRDownloader.getStatus(downloadId) ||
                                Status.FAILED == PRDownloader.getStatus(downloadId) ||
                                Status.CANCELLED == PRDownloader.getStatus(downloadId)) && !Utils.isNetworkAvailable(MainActivity.this)) {
                            Toast.makeText(MainActivity.this, "Download Paused", Toast.LENGTH_SHORT).show();
                            mAlertDialog.dismiss();
                            PRDownloader.pause(downloadId);
                            return;
                        }
                        mAlertDialog.dismiss();
                        downloadId = 0;
                        progressBar.setProgress(0);
                        Toast.makeText(MainActivity.this, "Downloaded Failed", Toast.LENGTH_SHORT).show();
                        progressBar.setIndeterminate(false);
                        autoFill();
                    }
                });
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            if (isDownloading) {
                FileDownloader(updatedAppID, new_message, appLink);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker
                .removeInternetConnectivityChangeListener(this);
    }
}

