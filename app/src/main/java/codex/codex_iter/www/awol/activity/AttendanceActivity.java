package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.AttendanceAdapter;
import codex.codex_iter.www.awol.adapter.MultipleAccountAdapter;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.databinding.ActivityAttendanceBinding;
import codex.codex_iter.www.awol.databinding.ItemSwitchAccountBinding;
import codex.codex_iter.www.awol.exceptions.InvalidFirebaseResponseException;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.Attendance;
import codex.codex_iter.www.awol.model.Student;
import codex.codex_iter.www.awol.setting.SettingsActivity;
import codex.codex_iter.www.awol.utilities.Constants;
import codex.codex_iter.www.awol.utilities.ScreenshotUtils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.APP_ID_ONESIGNAL;
import static codex.codex_iter.www.awol.utilities.Constants.AUTH_KEY_ONESIGNAL;
import static codex.codex_iter.www.awol.utilities.Constants.CUSTOM_TABS_LINK;
import static codex.codex_iter.www.awol.utilities.Constants.CUSTOM_TABS_LINK_2;
import static codex.codex_iter.www.awol.utilities.Constants.FETCH_FILE;
import static codex.codex_iter.www.awol.utilities.Constants.NO_ATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.PASSWORD;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.RESULT_STATUS;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_CUSTOM_TABS;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_LECTURES;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_RESULT;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_BRANCH;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_SEMESTER;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_YEAR;

public class AttendanceActivity extends AppCompatActivity implements InternetConnectivityListener, MultipleAccountAdapter.OnItemClickListener {

    private String result;
    private Attendance[] attendanceData;
    private int l;
    public ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
    private SharedPreferences sharedPreference;
    @SuppressWarnings("FieldCanBeLocal")
    private AttendanceAdapter adapter;
    private boolean no_attendance;
    private String showResult, showlectures, custom_tabs_link, showCustomTabs, custom_tabs_link_2;
    private BottomSheetDialog dialog;
    private static final String[] suffix = new String[]{"k", "m", "b", "t"};
    private String AUTH_KEY;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private LocalDB localDB;
    private Student preferredStudent;
    private ActivityAttendanceBinding activityAttendanceBinding;
    private Boolean isOpen = false;
    private Animation fab_open, fab_close, fab_clock, fab_anticlock;

    @Override
    protected void onResume() {
        super.onResume();
        if (localDB.getStudent(this.sharedPreference.getString("pref_student", null)) != null) {
            preferredStudent = localDB.getStudent(this.sharedPreference.getString("pref_student", null));
        }

        if (no_attendance) {
            noAttendance();
        } else {
            processAttendance();
        }
    }

    void processAttendance() {
        attendanceArrayList.clear();
        int avgab = 0;
        double avgat = 0;

        try {
            JSONObject jObj1;
            JSONArray arr;
            if (result != null && !result.isEmpty()) {
                result = result.split("kkk")[0];
                jObj1 = new JSONObject(result);
            } else {
                throw new InvalidResponseException();
            }

            try {
                arr = jObj1.getJSONArray("griddata");
                l = arr.length();
            } catch (Exception e) {
                throw new InvalidResponseException();
            }

            attendanceData = new Attendance[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                attendanceData[i] = new Attendance();

                if (!jObj.has("subjectcode") || !jObj.has("subject") || !jObj.has("Latt") || !jObj.has("Patt")
                        || !jObj.has("TotalAttandence")) {
                    throw new InvalidResponseException();
                }
                Updated(jObj, localDB.getStudent(this.sharedPreference.getString("pref_student", null)), i);
                attendanceData[i].setCode(jObj.getString("subjectcode"));
                attendanceData[i].setSub(jObj.getString("subject"));
                attendanceData[i].setTheory(jObj.getString("Latt"));
                attendanceData[i].setLab(jObj.getString("Patt"));
                attendanceData[i].setPercent(jObj.getString("TotalAttandence"));
                attendanceData[i].setBunk(Integer.parseInt(Objects.requireNonNull(this.sharedPreference.getString("pref_minimum_attendance", "75"))),
                        this.sharedPreference.getBoolean("pref_extended_stats", false), this.sharedPreference.getBoolean("pref_show_attendance_stats", true));
                avgat += jObj.getDouble("TotalAttandence");
                avgab += attendanceData[i].getAbsent();
                String student_semester = jObj.getString(STUDENT_SEMESTER);
                preferredStudent.setSemester(student_semester);
            }
            preferredStudent.setAttendances(attendanceData);
            avgat /= l;
            avgab /= l;
            preferredStudent.setAveragePresent(Math.round(avgat));
            preferredStudent.setAverageAbsent(avgab);
        } catch (JSONException | InvalidResponseException e) {
            Log.d("message", Objects.requireNonNull(e.getMessage()));
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (preferredStudent != null) attendanceData = preferredStudent.getAttendances();
            else noAttendance();
        } catch (Exception e) {
            Log.d("message", Objects.requireNonNull(e.getMessage()));
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (preferredStudent != null) attendanceData = preferredStudent.getAttendances();
            else noAttendance();
        } finally {
            try {
                localDB.setStudent(this.sharedPreference.getString("pref_student", null), preferredStudent);
                attendanceArrayList.addAll(Arrays.asList(attendanceData).subList(0, l));
            } catch (Exception e) {
                noAttendance();
            }

            adapter = new AttendanceAdapter(this, attendanceArrayList, Integer.parseInt(Objects.requireNonNull(
                    this.sharedPreference.getString("pref_minimum_attendance", "75"))));
            activityAttendanceBinding.rl.setHasFixedSize(true);
            activityAttendanceBinding.rl.setAdapter(adapter);
            activityAttendanceBinding.rl.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void fetchResult() {
        getResultAPI(this.sharedPreference.getString(API, ""), preferredStudent.getRedgNo(), preferredStudent.getPassword());
        showBottomSheetDialog();
    }

    private void getResultAPI(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/result",
                response -> {
                    if (response.equals("900")) {
                        hideBottomSheetDialog();
                        Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        hideBottomSheetDialog();
                        Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);
                        startActivity(intent);
                    }
                },
                error -> {
                    hideBottomSheetDialog();
                    Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                    if (error instanceof AuthFailureError) {
                        Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineResult());
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineResult());
                            startActivity(intent);
                        }
                    } else {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineResult());
                            startActivity(intent);
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

    private void Updated(JSONObject newAttendance, Student oldAttendance, int i) throws JSONException {
        if (oldAttendance != null) {
            for (Attendance student : oldAttendance.getAttendances()) {
                if (student.getCode().equals(newAttendance.getString("subjectcode"))) {
                    if ((!student.getTheory().equals(newAttendance.getString("Latt")))
                            || (!student.getLab().equals(newAttendance.getString("Patt")))) {
                        attendanceData[i].setLastAttendanceUpdateTime(new Date().getTime());
                        attendanceData[i].setOld(student.getPercent());
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v != null) {
                            v.vibrate(200);
                        }
                        attendanceData[i].setUpd("just now");
                        break;
                    } else {
                        attendanceData[i].setLastAttendanceUpdateTime(student.getLastAttendanceUpdateTime());
                        attendanceData[i].setUpd(DateUtils.getRelativeTimeSpanString(student.getLastAttendanceUpdateTime(), new Date().getTime(), 0).toString());
                    }
                }
            }
        } else {
            attendanceData[i].setLastAttendanceUpdateTime(new Date().getTime());
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(200);
            }
            attendanceData[i].setUpd("just now");
        }
    }

    public void noAttendance() {
        localDB.setStudent(sharedPreference.getString("pref_student", null), null);
        activityAttendanceBinding.rl.setVisibility(View.GONE);
        activityAttendanceBinding.NA.setVisibility(View.VISIBLE);
    }

    private String APP_ID;

    private static String numberFormat(double n, int iteration) {
        try {
            double d = ((long) n / 100.0) / 10.0;
            boolean isRound = (d * 10) % 10 == 0;
            return (d < 1000 ?
                    ((d > 99.9 || isRound || (!isRound && d > 9.99) ?
                            (int) d * 10 / 10 : d + ""
                    ) + "" + suffix[iteration])
                    : numberFormat(d, iteration + 1));
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAttendanceBinding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(activityAttendanceBinding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(false);

        localDB = new LocalDB(this);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);

        preferredStudent = localDB.getStudent(this.sharedPreference.getString("pref_student", null));
        if (preferredStudent == null) {
            preferredStudent = new Student();
        }

        try {
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(AttendanceActivity.this);
        } catch (IllegalStateException e) {
            InternetAvailabilityChecker.init(this);
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(AttendanceActivity.this);
        }

        getDataFromFirebase();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            no_attendance = bundle.getBoolean(NO_ATTENDANCE);
            preferredStudent.setName(bundle.getString(STUDENT_NAME));
            preferredStudent.setBranch(bundle.getString(STUDENT_BRANCH));
            preferredStudent.setAcademic_year(bundle.getString(STUDENT_YEAR));
            preferredStudent.setRedgNo(bundle.getString(REGISTRATION_NUMBER));
            preferredStudent.setPassword(bundle.getString(PASSWORD));
            result = bundle.getString(RESULTS);
            preferredStudent.setOfflineAttendance(result);
            getSupportActionBar().setSubtitle(bundle.getString(REGISTRATION_NUMBER));

            try {
                if (!bundle.getString(STUDENT_NAME).isEmpty()) {
                    getSupportActionBar().setTitle(Constants.convertToTitleCaseIteratingChars(bundle.getString(STUDENT_NAME)));
                } else {
                    getSupportActionBar().setTitle("Home");
                }
            } catch (Exception e) {
                getSupportActionBar().setTitle("Home");
            }
        }

        activityAttendanceBinding.fabButton.fab.setOnClickListener(view -> {
            fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
            fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
            fab_clock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
            fab_anticlock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);

            if (isOpen) {
                closeFloatingButtonAction();
            } else {
                openFloatingButtonAction();
            }
        });

        activityAttendanceBinding.fabButton.fab4.setOnClickListener(view -> {
            closeFloatingButtonAction();
            ItemSwitchAccountBinding itemSwitchAccountBinding = ItemSwitchAccountBinding.inflate(LayoutInflater.from(this));
            AlertDialog alertDialog = new MaterialAlertDialogBuilder(this)
                    .setCancelable(true)
                    .setView(itemSwitchAccountBinding.getRoot())
                    .create();

            ArrayList<Student> students = localDB.getStudents();
            Student addAccountCard = new Student();
            addAccountCard.setName("Add Account");
            addAccountCard.setRedgNo("Add a different account");
            students.add(addAccountCard);

            if (!students.isEmpty()) {
                MultipleAccountAdapter multipleAccountAdapter = new MultipleAccountAdapter(this, students, AttendanceActivity.this);
                itemSwitchAccountBinding.accountRecyclerView.setHasFixedSize(true);
                itemSwitchAccountBinding.accountRecyclerView.setAdapter(multipleAccountAdapter);
                itemSwitchAccountBinding.accountRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            }
            alertDialog.show();
        });

        activityAttendanceBinding.fabButton.fab1.setOnClickListener(view -> {
            closeFloatingButtonAction();
            if (Objects.equals(this.sharedPreference.getString(SHOW_RESULT, ""), "0")) {
                Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "We will be back within 3-4 days", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                fetchResult();
            }
        });

        activityAttendanceBinding.fabButton.fab2.setOnClickListener(view -> {
            closeFloatingButtonAction();
            if (!Objects.equals(this.sharedPreference.getString(SHOW_CUSTOM_TABS, "0"), "0")) {
                if (preferredStudent != null) {
                    if (preferredStudent.getAcademic_year().equals("1920")
                            || preferredStudent.getAcademic_year().equals("2021")) {
                        if (custom_tabs_link_2 != null && !custom_tabs_link_2.isEmpty()) {
                            custom_tab(custom_tabs_link_2);
                            Log.d("custom_link", custom_tabs_link_2);
                        } else {
                            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    } else {
                        if (custom_tabs_link != null && !custom_tabs_link.isEmpty()) {
                            custom_tab(custom_tabs_link);
                            Log.d("custom_link", custom_tabs_link);
                        } else {
                            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        activityAttendanceBinding.fabButton.fab3.setOnClickListener(view -> {
            closeFloatingButtonAction();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "All in one app for ITER students - check your attendance, results and a lot more: https://codex-iter.github.io/AWOL/ \n\n_Developed and Maintained by CODEX_ ");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        });
    }

    private void closeFloatingButtonAction() {
        activityAttendanceBinding.fabButton.textViewMoodle.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.textViewResult.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.textViewInvite.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.textViewAccount.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.fab1.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.fab2.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.fab3.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.fab4.setVisibility(View.GONE);
        activityAttendanceBinding.fabButton.fab1.setAnimation(fab_close);
        activityAttendanceBinding.fabButton.fab2.setAnimation(fab_close);
        activityAttendanceBinding.fabButton.fab3.setAnimation(fab_close);
        activityAttendanceBinding.fabButton.fab4.setAnimation(fab_close);
        activityAttendanceBinding.fabButton.fab1.setClickable(false);
        activityAttendanceBinding.fabButton.fab2.setClickable(false);
        activityAttendanceBinding.fabButton.fab3.setClickable(false);
        activityAttendanceBinding.fabButton.fab4.setClickable(false);
        activityAttendanceBinding.fabButton.fab.setAnimation(fab_anticlock);
        isOpen = false;
    }

    private void openFloatingButtonAction() {
        activityAttendanceBinding.fabButton.fab1.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.fab2.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.fab3.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.fab4.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.fab1.setAnimation(fab_open);
        activityAttendanceBinding.fabButton.fab2.setAnimation(fab_open);
        activityAttendanceBinding.fabButton.fab3.setAnimation(fab_open);
        activityAttendanceBinding.fabButton.fab4.setAnimation(fab_open);
        activityAttendanceBinding.fabButton.textViewMoodle.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.textViewResult.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.textViewInvite.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.textViewAccount.setVisibility(View.VISIBLE);
        activityAttendanceBinding.fabButton.fab1.setClickable(true);
        activityAttendanceBinding.fabButton.fab2.setClickable(true);
        activityAttendanceBinding.fabButton.fab3.setClickable(true);
        activityAttendanceBinding.fabButton.fab4.setClickable(true);
        activityAttendanceBinding.fabButton.fab.setAnimation(fab_clock);
        isOpen = true;
    }

    public void getDataFromFirebase() {
        try {
            CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(RESULT_STATUS);
            apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (!documentChange.getDocument().contains(SHOW_RESULT) || !documentChange.getDocument().contains(SHOW_LECTURES) ||
                                !documentChange.getDocument().contains(SHOW_CUSTOM_TABS) || !documentChange.getDocument().contains(FETCH_FILE) ||
                                !documentChange.getDocument().contains(CUSTOM_TABS_LINK) || !documentChange.getDocument().contains(CUSTOM_TABS_LINK_2) ||
                                !documentChange.getDocument().contains(AUTH_KEY_ONESIGNAL) || !documentChange.getDocument().contains(APP_ID_ONESIGNAL)) {
                            throw new InvalidFirebaseResponseException();
                        }
                        showResult = documentChange.getDocument().getString(SHOW_RESULT);
                        showlectures = documentChange.getDocument().getString(SHOW_LECTURES);
                        showCustomTabs = documentChange.getDocument().getString(SHOW_CUSTOM_TABS);
                        this.sharedPreference.edit().putString(SHOW_RESULT, showResult).apply();
                        this.sharedPreference.edit().putString(SHOW_LECTURES, showlectures).apply();
                        this.sharedPreference.edit().putString(SHOW_CUSTOM_TABS, showCustomTabs).apply();
                        custom_tabs_link = String.valueOf(documentChange.getDocument().getString(CUSTOM_TABS_LINK));
                        custom_tabs_link_2 = String.valueOf(documentChange.getDocument().getString(CUSTOM_TABS_LINK_2));
                        AUTH_KEY = documentChange.getDocument().getString(AUTH_KEY_ONESIGNAL);
                        APP_ID = documentChange.getDocument().getString(APP_ID_ONESIGNAL);
                    }
                    downloadStats(AUTH_KEY, APP_ID);
                }
            });
        } catch (InvalidFirebaseResponseException e) {
            Snackbar snackbar = Snackbar.make(
                    activityAttendanceBinding.mainLayout, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);
            snackbar.show();
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    public void downloadStats(String auth_key, String app_id) {
        MaterialTextView download_stats = findViewById(R.id.download_stats);
        LinearLayout download_stats_layout = findViewById(R.id.download_stats_layout);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "https://onesignal.com/api/v1/apps/" + app_id, null,
                response -> {
                    Log.d("response", response.toString());
                    try {
                        download_stats_layout.setVisibility(View.VISIBLE);
                        String s = numberFormat((double) response.getInt("players"), 0);
                        Log.d("size", String.valueOf(s.length()));
                        String word;

                        double d = Double.parseDouble(s.split("(?<=[\\d\\.])(?=[a-z])")[0]);
                        if (!s.isEmpty()) {
                            switch (s.split("(?<=[\\d\\.])(?=[a-z])")[1]) {
                                case "k":
                                    word = "Thousand";
                                    break;
                                case "m":
                                    word = "Million";
                                    break;
                                case "b":
                                    word = "Billion";
                                    break;
                                case "t":
                                    word = "Trillion";
                                    break;
                                default:
                                    word = "Hundred";
                                    break;
                            }
                            download_stats.setText(getResources().getString(R.string.download_stats, d, word));
                        } else {
                            download_stats.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        download_stats_layout.setVisibility(View.GONE);
                    }
                },
                error -> {
                    Log.d("error", Objects.requireNonNull(error.toString()));
                    download_stats_layout.setVisibility(View.GONE);
                }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put("Authorization", "Basic " + auth_key);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            if (no_attendance) {
                Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                try {
                    Bitmap bitmap = ScreenshotUtils.getScreenShot(activityAttendanceBinding.rl);
                    if (bitmap != null) {
                        Toast.makeText(this, "Taking screenshot please wait...", Toast.LENGTH_SHORT).show();
                        File save = ScreenshotUtils.getMainDirectoryName(this);
                        File file = ScreenshotUtils.store(bitmap, "screenshot.jpg", save);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            shareScreenshot(file);
                        } else {
                            shareScreenshot_low(file);
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (Exception e) {
                    Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
            return true;
        }
        if (id == R.id.action_setting) {
            startActivity(new Intent(AttendanceActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_aboutUs) {
            startActivity(new Intent(AttendanceActivity.this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void shareScreenshot(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() +
                    ".my.package.name.provider", file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void shareScreenshot_low(File file) {
        try {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void showBottomSheetDialog() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_bottomprogressbar, null);
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    public void hideBottomSheetDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
    }

    private void custom_tab(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setShowTitle(true);
            builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
            builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);
            builder.setToolbarColor(getResources().getColor(R.color.colorAccent));
            builder.build().launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityAttendanceBinding.mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mInternetAvailabilityChecker
                .removeInternetConnectivityChangeListener(this);
    }

    @Override
    public void switchToClickedAccount(Student student) {
        this.sharedPreference.edit().putString("pref_student", student.getRedgNo()).apply();
        finish();
        startActivity(new Intent(AttendanceActivity.this, MainActivity.class));
    }

    @Override
    public void addAccountClicked() {
        this.sharedPreference.edit().putString("pref_student", null).apply();
        finish();
        startActivity(new Intent(AttendanceActivity.this, MainActivity.class));
    }
}