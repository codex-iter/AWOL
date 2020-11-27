package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.AttendanceAdapter;
import codex.codex_iter.www.awol.adapter.MultipleAccountAdapter;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.exceptions.InvalidFirebaseResponseException;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.Attendance;
import codex.codex_iter.www.awol.model.Student;
import codex.codex_iter.www.awol.setting.SettingsActivity;
import codex.codex_iter.www.awol.theme.ThemeFragment;
import codex.codex_iter.www.awol.utilities.Constants;
import codex.codex_iter.www.awol.utilities.FirebaseConfig;
import codex.codex_iter.www.awol.utilities.ScreenshotUtils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.APP_ID_ONESIGNAL;
import static codex.codex_iter.www.awol.utilities.Constants.AUTH_KEY_ONESIGNAL;
import static codex.codex_iter.www.awol.utilities.Constants.CUSTOM_TABS_LINK;
import static codex.codex_iter.www.awol.utilities.Constants.CUSTOM_TABS_LINK_2;
import static codex.codex_iter.www.awol.utilities.Constants.FETCH_FILE;
import static codex.codex_iter.www.awol.utilities.Constants.NO_ATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.PASSWORD;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE2;
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

public class AttendanceActivity extends BaseThemedActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener, MultipleAccountAdapter.OnItemClickListener {

    @BindView(R.id.main_layout)
    ConstraintLayout mainLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.check_result)
    MaterialButton checkResult;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.NA_content)
    MaterialTextView tv;
    @BindView(R.id.rl)
    RecyclerView recyclerView;
    @BindView(R.id.NA)
    ConstraintLayout noAttendanceLayout;
    @BindView(R.id.who_layout)
    ConstraintLayout who_layout;
    @BindView(R.id.who_button)
    MaterialButton who_button;
    @BindView(R.id.removetile)
    ImageView removetile;
    @BindView(R.id.heading)
    MaterialTextView heading;
    @BindView(R.id.heading_desp)
    MaterialTextView heading_desp;
    @BindView(R.id.whoCard)
    MaterialCardView cardView;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
//    @BindView(R.id.adView)
//    AdView adView;

    private String result;
    private Attendance[] attendanceData;
    private int l;
    public ArrayList<Attendance> attendanceArrayList = new ArrayList<>();
    private String student_semester;
    private SharedPreferences sharedPreference;
    @SuppressWarnings("FieldCanBeLocal")
    private AttendanceAdapter adapter;
    private boolean no_attendance;
    private String showResult, showlectures, custom_tabs_link, showCustomTabs, custom_tabs_link_2;
    private BottomSheetDialog dialog;
    private int read_database;
    private static final String[] suffix = new String[]{"k", "m", "b", "t"};
    private String AUTH_KEY;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private View headerView;
    private static final String TOOLBAR_COLOR = "toolbar_color";
    private LocalDB localDB;
    private Student preferredStudent;
    private boolean dropArrowDown;

    @Override
    protected void onResume() {
        super.onResume();
        //TODO Check if attendance available or not from MainActivity
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
                        || !jObj.has("TotalAttandence") || attendanceData[i].getAbsent().isEmpty() || attendanceData[i].getAbsent() == null) {
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
                avgab += Integer.parseInt(attendanceData[i].getAbsent());
                student_semester = jObj.getString(STUDENT_SEMESTER);
                preferredStudent.setSemester(student_semester);
            }
            preferredStudent.setAttendances(attendanceData);
            avgat /= l;
            avgab /= l;
            preferredStudent.setAveragePresent(Math.round(avgat));
            preferredStudent.setAverageAbsent(avgab);
        } catch (JSONException | InvalidResponseException e) {
            Log.d("message", Objects.requireNonNull(e.getMessage()));
            Snackbar snackbar = Snackbar.make(mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (preferredStudent != null) attendanceData = preferredStudent.getAttendances();
            else noAttendance();
        } catch (Exception e) {
            Log.d("message", Objects.requireNonNull(e.getMessage()));
            Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
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

            if (preferredStudent != null) {
                TextView avat = headerView.findViewById(R.id.avat);
                avat.setText(getResources().getString(R.string.average_present, preferredStudent.getAveragePresent()));
                TextView avab = headerView.findViewById(R.id.avab);
                avab.setText(String.valueOf(preferredStudent.getAverageAbsent()));
            }

            adapter = new AttendanceAdapter(this, attendanceArrayList, Integer.parseInt(Objects.requireNonNull(
                    this.sharedPreference.getString("pref_minimum_attendance", "75"))));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                        Snackbar snackbar = Snackbar.make(mainLayout, "Results not found", Snackbar.LENGTH_SHORT);
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
                        Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineResult());
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            intent.putExtra(RESULTS, preferredStudent.getOfflineResult());
                            startActivity(intent);
                        }
                    } else {
                        if (preferredStudent == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
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

        recyclerView.setVisibility(View.GONE);
        noAttendanceLayout.setVisibility(View.VISIBLE);

        if (dark) {
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            mainLayout.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            checkResult.setTextColor(Color.parseColor("#141414"));
            tv.setTextColor(Color.parseColor("#141414"));
        }
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
        setContentView(R.layout.activity_attendance);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setCustomView(R.layout.activity_action_bar);

        localDB = new LocalDB(this);

        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);

        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        // hide plan bunk item
        navigationView.getMenu().findItem(R.id.pab).setVisible(false);

        TextView student_name = headerView.findViewById(R.id.name);
        TextView student_regdno = headerView.findViewById(R.id.reg);
        View view_cus = getSupportActionBar().getCustomView();
        MaterialTextView title = view_cus.findViewById(R.id.title);
        ImageView icon = view_cus.findViewById(R.id.image);
        ImageView share = view_cus.findViewById(R.id.share);
        ImageView arrowUpDown = headerView.findViewById(R.id.arrowDownUp);

        // make multiple account arrow by default true
        dropArrowDown = true;

        preferredStudent = localDB.getStudent(this.sharedPreference.getString("pref_student", null));
        if (preferredStudent == null) {
            preferredStudent = new Student();
        }

        // change the color accordingly
        if (dark) {
            cardView.setBackgroundColor(Color.parseColor("#141414"));
            heading.setTextColor(Color.parseColor("#FFFFFFFF"));
            heading_desp.setTextColor(Color.parseColor("#FFCCCCCC"));
            heading_desp.setTextColor(Color.parseColor("#FFCCCCCC"));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
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
        FirebaseConfig();

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
            student_name.setText(bundle.getString(STUDENT_NAME));
            student_regdno.setText(bundle.getString(REGISTRATION_NUMBER));
            String[] split = Objects.requireNonNull(bundle.getString(STUDENT_NAME)).split("\\s+");
            try {
                if (!split[0].isEmpty()) {
                    title.setText("Hi, " + Constants.convertToTitleCaseIteratingChars(split[0]) + "!");
                } else {
                    title.setText("Home");
                }
            } catch (Exception e) {
                title.setText("Home");
            }
        }

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String current_versionName = pInfo.versionName;
            MaterialTextView version = findViewById(R.id.version);
            version.setText("v" + current_versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        if (pref.getBoolean("is_First_Run2", true)) {
            pref.edit().putBoolean("is_First_Run2", false).apply();
            who_layout.setVisibility(View.GONE);
        }

        icon.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        headerView.findViewById(R.id.changeTheme).setOnClickListener(view -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            ThemeFragment fragment = ThemeFragment.newInstance();
            fragment.show(getSupportFragmentManager(), "theme_fragment");
        });

        arrowUpDown.setOnClickListener(view -> {
            ConstraintLayout accountsLayout = headerView.findViewById(R.id.layoutAccounts);
            RecyclerView listAccounts = headerView.findViewById(R.id.listAccount);
            MultipleAccountAdapter multipleAccountAdapter;

            if (dropArrowDown) {
                accountsLayout.setVisibility(View.VISIBLE);
                arrowUpDown.setImageResource(R.drawable.arrow_up);
                dropArrowDown = false;
            } else {
                accountsLayout.setVisibility(View.GONE);
                arrowUpDown.setImageResource(R.drawable.arrow_down);
                dropArrowDown = true;
            }

            ArrayList<Student> students = localDB.getStudents();
            Student addAccountCard = new Student();
            addAccountCard.setName("Add Account");
            students.add(addAccountCard);

            if (preferredStudent != null) {
                for (Student student : students) {
                    if (student.getRedgNo().equals(preferredStudent.getRedgNo())) {
                        students.remove(student);
                        break;
                    }
                }
            }

            if (!students.isEmpty()) {
                multipleAccountAdapter = new MultipleAccountAdapter(this, students, AttendanceActivity.this);
                listAccounts.setHasFixedSize(true);
                listAccounts.setAdapter(multipleAccountAdapter);
                listAccounts.setLayoutManager(new LinearLayoutManager(this));
            }
        });

        share.setOnClickListener(view -> {
            if (no_attendance) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                try {
                    Bitmap bitmap = ScreenshotUtils.getScreenShot(recyclerView);
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
                        Snackbar snackbar = Snackbar.make(mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                } catch (Exception e) {
                    Snackbar snackbar = Snackbar.make(mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        checkResult.setOnClickListener(view -> fetchResult());
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
                        read_database = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString(FETCH_FILE)));
                        this.sharedPreference.edit().putString(SHOW_RESULT, showResult).apply();
                        this.sharedPreference.edit().putString(SHOW_LECTURES, showlectures).apply();
                        this.sharedPreference.edit().putString(SHOW_CUSTOM_TABS, showCustomTabs).apply();
                        custom_tabs_link = String.valueOf(documentChange.getDocument().getString(CUSTOM_TABS_LINK));
                        custom_tabs_link_2 = String.valueOf(documentChange.getDocument().getString(CUSTOM_TABS_LINK_2));
                        AUTH_KEY = documentChange.getDocument().getString(AUTH_KEY_ONESIGNAL);
                        APP_ID = documentChange.getDocument().getString(APP_ID_ONESIGNAL);

                        if (showlectures.equals("0"))
                            navigationView.getMenu().findItem(R.id.lecture).setVisible(false);
                        if (showCustomTabs.equals("0")) {
                            navigationView.getMenu().findItem(R.id.customTabs).setVisible(false);
                        } else {
                            navigationView.getMenu().findItem(R.id.customTabs).setVisible(true);
                        }

                        if (this.sharedPreference.getInt(READ_DATABASE, 0) < read_database) {
                            this.sharedPreference.edit().putInt(READ_DATABASE, read_database).apply();
                            showBottomSheetDialog();
//                        downloadFile();
                        }
                        downloadStats(AUTH_KEY, APP_ID);
                    }
                }
            });
        } catch (InvalidFirebaseResponseException e) {
            Snackbar snackbar = Snackbar.make(mainLayout, Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT);
            snackbar.show();
            navigationView.getMenu().findItem(R.id.lecture).setVisible(false);
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
            navigationView.getMenu().findItem(R.id.lecture).setVisible(false);
        }
    }

    public void FirebaseConfig() {
        FirebaseConfig firebaseConfig = new FirebaseConfig();
        String json = firebaseConfig.fetch_latest_news(this);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getInt("version") >= 1) {
                if (who_layout.getVisibility() == View.GONE && this.sharedPreference.getInt("version", 0) < jsonObject.getInt("version")) {
                    who_layout.setVisibility(View.VISIBLE);
                }
                this.sharedPreference.edit().putInt("version", jsonObject.getInt("version")).apply();
                who_button.setOnClickListener(view -> {
                    Uri uri;
                    try {
                        uri = Uri.parse(jsonObject.getString("link"));
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
                heading.setText(jsonObject.getString("news_title"));
                heading_desp.setText(jsonObject.getString("news_text"));
                Picasso.get()
                        .load(jsonObject.getString("image_url"))
                        .placeholder(R.drawable.ic_image)
                        .into(logo);
                if (this.sharedPreference.getBoolean("close", false)) {
                    who_layout.setVisibility(View.GONE);
                }
                removetile.setOnClickListener(view -> {
                    this.sharedPreference.edit().putBoolean("close", true).apply();
                    who_layout.setVisibility(View.GONE);
                });
            } else {
                who_layout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            Log.d("error_cardtile", e.toString());
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
                        if (dark) {
                            download_stats.setTextColor(getResources().getColor(R.color.white));
                            ImageView imageView = findViewById(R.id.download_icon);
                            imageView.setImageResource(R.drawable.eye_light);
                        }

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
            intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public void shareScreenshot_low(File file) {
        try {
            Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
            intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
            startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot take screenshot. Please try again", Snackbar.LENGTH_LONG);
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
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sa:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "All in one app for ITER students - check your attendance, results and a lot more: https://codex-iter.github.io/AWOL/ \n\n_Developed and Maintained by CODEX_ ");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.lecture: {
                if (!Objects.equals(this.sharedPreference.getString(SHOW_LECTURES, "0"), "0")) {
                    Intent intent = new Intent(AttendanceActivity.this, OnlineLectureSubjects.class);
                    switch (Objects.requireNonNull(this.sharedPreference.getString(STUDENT_SEMESTER, "1"))) {
                        case "1":
                            student_semester = "1st";
                            break;
                        case "2":
                            student_semester = "2nd";
                            break;
                        case "3":
                            student_semester = "3rd";
                            break;
                        case "4":
                            student_semester = "4th";
                            break;
                        case "5":
                            student_semester = "5th";
                            break;
                        case "6":
                            student_semester = "6th";
                            break;
                        case "7":
                            student_semester = "7th";
                            break;
                        case "8":
                            student_semester = "8th";
                            break;
                    }
                    intent.putExtra(STUDENT_SEMESTER, student_semester);
                    intent.putExtra(READ_DATABASE2, read_database);
                    startActivity(intent);
                }
                break;
            }
            case R.id.abt: {
                Intent intent = new Intent(AttendanceActivity.this, AboutActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.lgout:
                if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    this.drawerLayout.closeDrawer(GravityCompat.START);
                }
                MaterialAlertDialogBuilder binder = new MaterialAlertDialogBuilder(AttendanceActivity.this);
                binder.setMessage("Do you want to logout?");
                binder.setTitle(Html.fromHtml("<font color='#ba000d'>Are you sure?</font>"));
                binder.setCancelable(true);
                binder.setPositiveButton("YES", (dialog, which) -> {
                    localDB.setStudent(sharedPreference.getString("pref_student", null), null);

                    FirebaseAuth.getInstance().signOut();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent3);
                });
                binder.setNegativeButton("NO", (dialog, which) -> dialog.cancel());
                binder.show();
                break;
            case R.id.result:
                if (Objects.equals(this.sharedPreference.getString(SHOW_RESULT, ""), "0")) {
                    Snackbar snackbar = Snackbar.make(mainLayout, "We will be back within 3-4 days", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    fetchResult();
                }
                break;
            case R.id.setting:
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.customTabs:
                if (!Objects.equals(this.sharedPreference.getString(SHOW_CUSTOM_TABS, "0"), "0")) {
                    if (preferredStudent != null) {
                        if (preferredStudent.getAcademic_year().equals("1920")
                                || preferredStudent.getAcademic_year().equals("2021")) {
                            if (custom_tabs_link_2 != null && !custom_tabs_link_2.isEmpty()) {
                                custom_tab(custom_tabs_link_2);
                                Log.d("custom_link", custom_tabs_link_2);
                            } else {
                                Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        } else {
                            if (custom_tabs_link != null && !custom_tabs_link.isEmpty()) {
                                custom_tab(custom_tabs_link);
                                Log.d("custom_link", custom_tabs_link);
                            } else {
                                Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        }
                    } else {
                        Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
                break;
        }
        return true;
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
            if (this.getSharedPreferences("theme", 0).contains(TOOLBAR_COLOR)) {
                builder.setToolbarColor(getResources().getColor(this.getSharedPreferences("theme", 0).getInt(TOOLBAR_COLOR, 0)));
            } else {
                builder.setToolbarColor(getResources().getColor(R.color.green));
            }
            builder.build().launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, please try again", Snackbar.LENGTH_SHORT);
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