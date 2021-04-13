package codex.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

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
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codex.codex_iter.www.awol.activity.AttendanceActivity;
import codex.codex_iter.www.awol.activity.UnderMaintenance;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.databinding.ActivityMainBinding;
import codex.codex_iter.www.awol.exceptions.InvalidFirebaseResponseException;
import codex.codex_iter.www.awol.exceptions.InvalidResponseFetchNameException;
import codex.codex_iter.www.awol.model.Student;
import codex.codex_iter.www.awol.utilities.Utils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.APP_LINK;
import static codex.codex_iter.www.awol.utilities.Constants.DETAILS;
import static codex.codex_iter.www.awol.utilities.Constants.DRIVE_APP_ID;
import static codex.codex_iter.www.awol.utilities.Constants.NO_ATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.PASSWORD;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_BRANCH;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_YEAR;
import static codex.codex_iter.www.awol.utilities.Constants.UNDER_MAINTENANCE;
import static codex.codex_iter.www.awol.utilities.Constants.UPDATE_AVAILABLE;
import static codex.codex_iter.www.awol.utilities.Constants.UPDATE_FILE_SIZE;
import static codex.codex_iter.www.awol.utilities.Constants.UPDATE_MESSAGE;


public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {

    private boolean track;
    private String api = "", new_message, academic_year, appLink;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private int updated_version, current_version, downloadId;
    private FirebaseAuth mAuth;
    private boolean isDownloading;
    private File awolAppUpdateFile;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private LocalDB localDB;
    private SharedPreferences sharedPreferences;
    private Student preferredStudent;
    private ActivityMainBinding activityMainBinding;


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());

        try {
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(MainActivity.this);
        } catch (IllegalStateException e) {
            InternetAvailabilityChecker.init(this);
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(MainActivity.this);
        }

        mAuth = FirebaseAuth.getInstance();
        localDB = new LocalDB(this);
        bottomSheetBehavior = BottomSheetBehavior.from(activityMainBinding.bottomLogin.bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        awolAppUpdateFile = new File(Objects.requireNonNull(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString() + File.separator + "awol.apk");

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            current_version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Handler handler = new Handler();
        handler.postDelayed(() -> bottomSheetBehavior.setPeekHeight(convertDpToPixel()), 200);

        setUserNameAutoFill();

        activityMainBinding.bottomLogin.loginButton.setOnClickListener(view -> {
            String username = Objects.requireNonNull(activityMainBinding.bottomLogin.user.getText()).toString().trim();
            String password = Objects.requireNonNull(activityMainBinding.bottomLogin.pass.getText()).toString().trim();
            if (username.isEmpty() || password.isEmpty()) {
                Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Enter your Details", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (api != null) {
                if (api.isEmpty())
                    Snackbar.make(activityMainBinding.mainLayout, "Invalid Firebase Response", Snackbar.LENGTH_SHORT).show();
                else {
                    activityMainBinding.bottomLogin.progressBar.setVisibility(View.VISIBLE);
                    activityMainBinding.bottomLogin.loginButton.setVisibility(View.GONE);
                    activityMainBinding.bottomLogin.user.setEnabled(false);
                    activityMainBinding.bottomLogin.pass.setEnabled(false);
                    activityMainBinding.bottomLogin.user.setFocusable(false);
                    activityMainBinding.bottomLogin.pass.setFocusable(false);
                    activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                    getName(api, username, password);
                }
            } else {
                Snackbar.make(activityMainBinding.mainLayout, "Invalid Firebase Response", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserNameAutoFill() {
        ArrayList<Student> students = localDB.getStudents();
        ArrayList<String> regdnos = new ArrayList<>();
        for (Student student : students) {
            regdnos.add(student.getRedgNo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, regdnos);
        activityMainBinding.bottomLogin.user.setAdapter(adapter);
        activityMainBinding.bottomLogin.user.setThreshold(1);

        activityMainBinding.bottomLogin.user.setOnItemClickListener((adapterView, view, i, l) -> {
            int pos = regdnos.indexOf(activityMainBinding.bottomLogin.user.getText().toString());
            activityMainBinding.bottomLogin.pass.setText(students.get(pos).getPassword());
            activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        });

        activityMainBinding.bottomLogin.pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0)
                    activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void fetchDetails() {
        try {
            CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(DETAILS);
            apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        try {
                            if (!documentChange.getDocument().contains(API) || !documentChange.getDocument().contains(UPDATE_AVAILABLE) ||
                                    !documentChange.getDocument().contains(UNDER_MAINTENANCE) || !documentChange.getDocument().contains(UPDATE_FILE_SIZE) ||
                                    !documentChange.getDocument().contains(APP_LINK) || !documentChange.getDocument().contains(UPDATE_MESSAGE) ||
                                    !documentChange.getDocument().contains(DRIVE_APP_ID)) {
                                throw new InvalidFirebaseResponseException();
                            }
                        } catch (InvalidFirebaseResponseException e1) {
                            Toast.makeText(this, "Please try after sometime", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        api = documentChange.getDocument().getString(API);
                        updated_version = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString(UPDATE_AVAILABLE)));
                        int check = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString(UNDER_MAINTENANCE)));
                        appLink = documentChange.getDocument().getString(APP_LINK);
                        new_message = documentChange.getDocument().getString(UPDATE_MESSAGE);

                        this.sharedPreferences.edit().putString(API, api).apply();
                        this.sharedPreferences.edit().putInt("CHECK", check).apply();

                        if (check == 1) {
                            Intent intent = new Intent(MainActivity.this, UnderMaintenance.class);
                            startActivity(intent);
                            finish();
                            return;
                        }

                        if (updated_version > current_version && current_version > 0 && Utils.isNetworkAvailable(MainActivity.this)) {
                            downloadUpdatedApp(this.new_message, appLink);
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
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
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
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
        }
    }

    private static int convertDpToPixel() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = (float) 600 * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    private void autoFill() {
        preferredStudent = localDB.getStudent(sharedPreferences.getString("pref_student", null));
        if (preferredStudent != null) {
            activityMainBinding.bottomLogin.user.setText(preferredStudent.getRedgNo());
            activityMainBinding.bottomLogin.pass.setText(preferredStudent.getPassword());
            activityMainBinding.bottomLogin.loginButton.performClick();
        }
    }

    private void getAttendanceAPI(final String... param) {
        if (param[0] == null) {
            param[0] = this.sharedPreferences.getString(API, api);
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/attendance",
                response -> {
                    Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                    intent.putExtra(REGISTRATION_NUMBER, param[1]);
                    intent.putExtra(PASSWORD, param[2]);
                    intent.putExtra(STUDENT_YEAR, param[3]);
                    intent.putExtra(STUDENT_NAME, param[4]);
                    intent.putExtra(STUDENT_BRANCH, param[5]);

                    if (response.equals("404")) {
                        processLoginViewsStatus();
                        resetUserPassViews();
                        Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Wrong Credentials", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (response.equals("390")) {
                        intent.putExtra(NO_ATTENDANCE, true);
                        intent.putExtra(RESULTS, "");
                        if (mAuth.getCurrentUser() != null) {
                            this.sharedPreferences.edit().putString("pref_student", param[1]).apply();
                            startActivity(intent);
                            finish();
                        } else {
                            mAuth.signInAnonymously()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            sharedPreferences.edit().putString("pref_student", param[1]).apply();
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    });
                        }
                    } else {
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);

                        if (mAuth.getCurrentUser() != null) {
                            this.sharedPreferences.edit().putString("pref_student", param[1]).apply();
                            startActivity(intent);
                            finish();
                        } else {
                            mAuth.signInAnonymously()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            this.sharedPreferences.edit().putString("pref_student", param[1]).apply();
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Oops, something went wrong! Please try after sometime", Snackbar.LENGTH_SHORT);
                                            snackbar.show();
                                        }
                                    });
                        }
                    }
                },
                error -> {
                    processLoginViewsStatus();
                    Intent intent = new Intent(MainActivity.this, AttendanceActivity.class);
                    intent.putExtra(REGISTRATION_NUMBER, param[1]);
                    intent.putExtra(PASSWORD, param[2]);
                    intent.putExtra(STUDENT_YEAR, param[3]);
                    intent.putExtra(STUDENT_NAME, param[4]);
                    intent.putExtra(STUDENT_BRANCH, param[5]);

                    if (error instanceof AuthFailureError) {
                        resetUserPassViews();
                        Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (preferredStudent == null) {
                            resetUserPassViews();
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineAttendance());
                            startActivity(intent);
                            finish();
                        }
                    } else if (error instanceof NetworkError) {
                        if (preferredStudent == null) {
                            resetUserPassViews();
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineAttendance());
                            startActivity(intent);
                            finish();
                        }
                    } else if (error instanceof TimeoutError) {
                        if (!track) {
                            activityMainBinding.bottomLogin.progressBar.setVisibility(View.VISIBLE);
                            activityMainBinding.bottomLogin.loginButton.setVisibility(View.GONE);
                            activityMainBinding.bottomLogin.user.setEnabled(false);
                            activityMainBinding.bottomLogin.pass.setEnabled(false);
                            activityMainBinding.bottomLogin.user.setFocusable(true);
                            activityMainBinding.bottomLogin.pass.setFocusable(true);
                            activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            track = true;
                            activityMainBinding.bottomLogin.loginButton.performClick();
                        } else {
                            if (preferredStudent == null) {
                                resetUserPassViews();
                                Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                track = false;
                            } else {
                                intent.putExtra(RESULTS, preferredStudent.getOfflineAttendance());
                                startActivity(intent);
                                finish();
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
                params.put(STUDENT_YEAR, param[3]);
                params.put(STUDENT_NAME, param[4]);
                params.put(STUDENT_BRANCH, param[5]);
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
                            processLoginViewsStatus();
                            resetUserPassViews();
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Wrong Credentials", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            JSONObject jobj = new JSONObject(response);
                            JSONArray jarr = jobj.getJSONArray("detail");
                            JSONObject jobj1 = jarr.getJSONObject(0);
                            if (!jobj1.has("name") || !jobj1.has(STUDENT_BRANCH)) {
                                throw new InvalidResponseFetchNameException();
                            }
                            if (jobj1.has("academicyear")) {
                                if (!jobj1.getString("academicyear").isEmpty()) {
                                    academic_year = jobj1.getString("academicyear");
                                }
                            } else {
                                academic_year = "";
                            }

                            String studentName = jobj1.getString("name");
                            String student_branch = jobj1.getString(STUDENT_BRANCH);
                            MainActivity.this.getAttendanceAPI(this.sharedPreferences.getString(API, api), param[1], param[2], academic_year, studentName, student_branch);
                        }
                    } catch (JSONException | InvalidResponseFetchNameException e) {
                        processLoginViewsStatus();
                        resetUserPassViews();
                        Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } catch (Exception e) {
                        processLoginViewsStatus();
                        resetUserPassViews();
                        Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                },
                error -> {
                    processLoginViewsStatus();
                    if (error instanceof AuthFailureError) {
                        resetUserPassViews();
                        Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (preferredStudent == null) {
                            resetUserPassViews();
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot connect to ITER servers right now.", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            MainActivity.this.getAttendanceAPI(this.sharedPreferences.getString(API, api), param[1], param[2],
                                    preferredStudent.getAcademic_year(), preferredStudent.getName(), preferredStudent.getBranch());

                        }
                    } else if (error instanceof NetworkError) {
                        if (preferredStudent == null) {
                            resetUserPassViews();
                            Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            MainActivity.this.getAttendanceAPI(this.sharedPreferences.getString(API, api), param[1], param[2],
                                    preferredStudent.getAcademic_year(), preferredStudent.getName(), preferredStudent.getBranch());
                        }
                    } else if (error instanceof TimeoutError) {
                        if (!track) {
                            activityMainBinding.bottomLogin.progressBar.setVisibility(View.VISIBLE);
                            activityMainBinding.bottomLogin.loginButton.setVisibility(View.GONE);
                            activityMainBinding.bottomLogin.user.setEnabled(false);
                            activityMainBinding.bottomLogin.pass.setEnabled(false);
                            activityMainBinding.bottomLogin.user.setFocusable(true);
                            activityMainBinding.bottomLogin.pass.setFocusable(true);
                            activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            track = true;
                            activityMainBinding.bottomLogin.loginButton.performClick();
                        } else {
                            if (preferredStudent == null) {
                                resetUserPassViews();
                                Snackbar snackbar = Snackbar.make(activityMainBinding.mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                                track = false;
                            } else {
                                MainActivity.this.getAttendanceAPI(this.sharedPreferences.getString(API, api), param[1], param[2],
                                        preferredStudent.getAcademic_year(), preferredStudent.getName(), preferredStudent.getBranch());
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

    private void resetUserPassViews() {
        activityMainBinding.bottomLogin.user.setEnabled(true);
        activityMainBinding.bottomLogin.pass.setEnabled(true);
        activityMainBinding.bottomLogin.user.setFocusableInTouchMode(true);
        activityMainBinding.bottomLogin.user.setFocusable(true);
        activityMainBinding.bottomLogin.pass.setFocusableInTouchMode(true);
        activityMainBinding.bottomLogin.pass.setFocusable(true);
    }

    private void processLoginViewsStatus() {
        activityMainBinding.bottomLogin.progressBar.setVisibility(View.INVISIBLE);
        activityMainBinding.bottomLogin.loginButton.setVisibility(View.VISIBLE);
        activityMainBinding.bottomLogin.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
    }

    private void downloadUpdatedApp(String new_message, String appLink) {
        try {
            if (!awolAppUpdateFile.exists())
                FileDownloader(new_message, appLink);
            else
                Utils.updateAvailable(MainActivity.this, new_message);
        } catch (Exception e) {
            Log.e("downloadError", e.toString());
            autoFill();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void FileDownloader(String new_message, String appLink) {
        if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
            return;
        }

        View mDialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_download_updates, null);
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
                    update.setText(getResources().getString(R.string.update_percentage, progressPercent));
                    update_out_of_100.setText(getResources().getString(R.string.update_percentage_100, progressPercent));
                    progressBar.setProgress((int) progressPercent);
                    progressBar.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        if (!isFinishing()) {
                            mAlertDialog.dismiss();
                            isDownloading = false;
                            Toast.makeText(MainActivity.this, "Downloaded Successfully", Toast.LENGTH_SHORT).show();
                            Utils.updateAvailable(MainActivity.this, new_message);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        if (!isFinishing()) {
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
                    }
                });
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        if (isConnected) {
            if (isDownloading) {
                FileDownloader(new_message, appLink);
            }
        } else {
            Toast.makeText(this, "No connection found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker
                .removeInternetConnectivityChangeListener(this);
    }
}

