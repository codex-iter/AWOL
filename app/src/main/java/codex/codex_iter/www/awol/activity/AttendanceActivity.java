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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.treebo.internetavailabilitychecker.InternetAvailabilityChecker;
import com.treebo.internetavailabilitychecker.InternetConnectivityListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
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
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.AttendanceData;
import codex.codex_iter.www.awol.setting.SettingsActivity;
import codex.codex_iter.www.awol.theme.ThemeFragment;
import codex.codex_iter.www.awol.utilities.Constants;
import codex.codex_iter.www.awol.utilities.FirebaseConfig;
import codex.codex_iter.www.awol.utilities.ScreenshotUtils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.NO_ATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE2;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.RESULT_STATUS;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_CUSTOM_TABS;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_LECTURES;
import static codex.codex_iter.www.awol.utilities.Constants.SHOW_RESULT;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_SEMESTER;

public class AttendanceActivity extends BaseThemedActivity implements NavigationView.OnNavigationItemSelectedListener, InternetConnectivityListener {

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
    private AttendanceData[] attendanceData;
    private int l;
    @SuppressWarnings("FieldCanBeLocal")
    private String[] r;
    public ArrayList<AttendanceData> attendanceDataArrayList = new ArrayList<>();
    private String student_semester;
    private SharedPreferences sub;
    private SharedPreferences studentnamePrefernces;
    private SharedPreferences preferences;
    private SharedPreferences sharedPreferences;
    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private AttendanceAdapter adapter;
    private boolean no_attendance;
    private String api;
    private String showResult, showlectures, custom_tabs_link, showCustomTabs;
    private BottomSheetDialog dialog;
    private int read_database;
    private static final String[] suffix = new String[]{"", "k", "m", "b", "t"};
    private String AUTH_KEY;
    //    private AdRequest adRequest;
//    private boolean isLoaded;
    private InternetAvailabilityChecker mInternetAvailabilityChecker;
    private View headerView;
    private static final String TOOLBAR_COLOR = "toolbar_color";

    public AttendanceActivity() {
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
    protected void onResume() {
        super.onResume();
        //TODO Check if attendance available or not from MainActivity
        if (no_attendance) {
            noAttendance();
        } else {
            processAttendance();
            // if user is not logout, show updated time of attendance
            this.getSharedPreferences("status", 0).edit().putString("status", "").apply();
        }
    }

    void processAttendance() {
        attendanceDataArrayList.clear();
        if (result != null) {
            r = result.split("kkk");
            result = r[0];
        }
        int avgab = 0;
        double avgat = 0;
        sub = getSharedPreferences("sub",
                Context.MODE_PRIVATE);

        try {
            JSONObject jObj1;
            if (result != null) {
                jObj1 = new JSONObject(result);
            } else {
                throw new InvalidResponseException();
            }
            JSONArray arr = jObj1.getJSONArray("griddata");
            l = arr.length();

            attendanceData = new AttendanceData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                attendanceData[i] = new AttendanceData();

                if (!jObj.has("subjectcode") || !jObj.has("subject") || !jObj.has("Latt") || !jObj.has("Patt")
                        || !jObj.has("TotalAttandence") || attendanceData[i].getAbsent().isEmpty() || attendanceData[i].getAbsent() == null) {
                    throw new InvalidResponseException();
                }

                String code = jObj.getString("subjectcode");
                String ck = Updated(jObj, sub, code, i);

                attendanceData[i].setCode(code);
                attendanceData[i].setSub(jObj.getString("subject"));
                attendanceData[i].setTheory(jObj.getString("Latt"));
                attendanceData[i].setLab(jObj.getString("Patt"));
                attendanceData[i].setUpd(ck);
                attendanceData[i].setPercent(jObj.getString("TotalAttandence"));
                attendanceData[i].setBunk(Integer.parseInt(Objects.requireNonNull(prefs.getString("pref_minimum_attendance", "75"))),
                        prefs.getBoolean("pref_extended_stats", false), prefs.getBoolean("pref_show_attendance_stats", true));
                avgat += jObj.getDouble("TotalAttandence");
                avgab += Integer.parseInt(attendanceData[i].getAbsent());
                student_semester = jObj.getString(STUDENT_SEMESTER);
                sharedPreferences.edit().putString(STUDENT_SEMESTER, student_semester).apply();
            }
            avgat /= l;
            avgab /= l;
            preferences.edit().putInt("AveragePresent", (int) avgat).apply();
            preferences.edit().putInt("AverageAbsent", (avgab)).apply();
            TextView avat = headerView.findViewById(R.id.avat);
            avat.setText(getResources().getString(R.string.average_present, preferences.getInt("AveragePresent", 0)));
            TextView avab = headerView.findViewById(R.id.avab);
            avab.setText(String.valueOf(preferences.getInt("AverageAbsent", 0)));

        } catch (JSONException | InvalidResponseException e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (!Constants.Offline_mode) {
                noAttendance();
            }
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong few things may not work properly", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (!Constants.Offline_mode) {
                noAttendance();
            }
        } finally {
            AttendanceData.attendanceData = attendanceData;
            if (!Constants.Offline_mode) {
                try {
                    attendanceDataArrayList.addAll(Arrays.asList(attendanceData).subList(0, l));
                    saveAttendance(attendanceDataArrayList);
                } catch (Exception e) {
                    Log.d("error", "Array might be null");
                    noAttendance();
                }
            } else {
                getSavedAttendance();
                TextView avat = headerView.findViewById(R.id.avat);
                avat.setText(getResources().getString(R.string.average_present, preferences.getInt("AveragePresent", 0)));
                TextView avab = headerView.findViewById(R.id.avab);
                avab.setText(String.valueOf(preferences.getInt("AverageAbsent", 0)));

            }
            adapter = new AttendanceAdapter(this, attendanceDataArrayList, Integer.parseInt(Objects.requireNonNull(prefs.getString("pref_minimum_attendance", "75"))));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void saveAttendance(ArrayList<AttendanceData> attendanceDataArrayList) {
        try {
            Constants.offlineDataEditor = Constants.offlineDataPreference.edit();
            Gson gson = new Gson();
            String json = gson.toJson(attendanceDataArrayList);
            Constants.offlineDataEditor.putString("StudentAttendance", json);
            Constants.offlineDataEditor.apply();
        } catch (Exception e) {
            Log.d("error", "Might be arrayList null");
        }
    }

    public void getSavedAttendance() {
        Snackbar snackbar = Snackbar.make(mainLayout, "Offline mode enabled", Snackbar.LENGTH_SHORT);
        snackbar.show();
        try {
            Gson gson = new Gson();
            String json = Constants.offlineDataPreference.getString("StudentAttendance", null);
            Type type = new TypeToken<ArrayList<AttendanceData>>() {
            }.getType();
            attendanceDataArrayList = gson.fromJson(json, type);

            if (attendanceDataArrayList == null) {
                attendanceDataArrayList = new ArrayList<>();
            }

            if (attendanceDataArrayList.isEmpty()) {
                noAttendance();
            }
        } catch (Exception e) {
            Log.d("error", "Something might be wrong");
            noAttendance();
        }
    }

    public void fetchResult() {
        SharedPreferences userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String u = userm.getString("user", "");
        String p = userm.getString("pass", "");
        getData(api, u, p);
        showBottomSheetDialog();
    }

    private void getData(final String... param) {
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
                        intent.putExtra(API, api);
                        startActivity(intent);
                    }
                },
                error -> {
                    hideBottomSheetDialog();
                    if (error instanceof AuthFailureError) {
                        if (Constants.offlineDataPreference.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof ServerError) {
                        if (Constants.offlineDataPreference.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (Constants.offlineDataPreference.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Log.e("Volley_error", String.valueOf(error));
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        if (Constants.offlineDataPreference.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
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

    private String Updated(JSONObject jObj, SharedPreferences sub, String code, int i) throws JSONException {
        if (sub.contains(code)) {
            JSONObject old = new JSONObject(Objects.requireNonNull(sub.getString(code, "")));
            SharedPreferences is_logout = this.getSharedPreferences("status", 0);
            String status = is_logout.getString("status", "");
            if (Objects.requireNonNull(status).equals("0")) {
                old.put("Latt", "");
                old.put("Patt", "");
                old.put("TotalAttandence", "");
            }
            if ((!old.getString("Latt").equals(jObj.getString("Latt"))) || (!old.getString("Patt").equals(jObj.getString("Patt")))) {
                jObj.put("updated", new Date().getTime());
                attendanceData[i].setOld(old.getString("TotalAttandence"));
                edit = sub.edit();
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (v != null) {
                    v.vibrate(200);
                }
                edit.putString(code, jObj.toString());
                edit.apply();
                return "Just now";
            } else
                return DateUtils.getRelativeTimeSpanString(old.getLong("updated"), new Date().getTime(), 0).toString();
        } else {
            jObj.put("updated", new Date().getTime());
            edit = sub.edit();
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null) {
                v.vibrate(200);
            }
            edit.putString(code, jObj.toString());
            edit.commit();
            return "Just now";
        }
    }

    public void noAttendance() {
        navigationView.getMenu().findItem(R.id.pab).setVisible(false);
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

    private static String numberFormat(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        int MAX_LENGTH = 4;
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        try {
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(AttendanceActivity.this);
        } catch (IllegalStateException e) {
            InternetAvailabilityChecker.init(this);
            mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
            mInternetAvailabilityChecker.addInternetConnectivityListener(AttendanceActivity.this);
        }

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        Objects.requireNonNull(this.getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setCustomView(R.layout.activity_action_bar);
        View view_cus = getSupportActionBar().getCustomView();
        MaterialTextView title = view_cus.findViewById(R.id.title);
        ImageView icon = view_cus.findViewById(R.id.image);
        ImageView share = view_cus.findViewById(R.id.share);
        preferences = getSharedPreferences("CLOSE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Constants.offlineDataPreference = this.getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();

        // Mobile Ads
//        MobileAds.initialize(this);
//        MobileAds.setRequestConfiguration(
//                new RequestConfiguration.Builder().setTestDeviceIds(Collections.singletonList("623B1B7759D51209294A77125459D9B7"))
//                        .build());
//
//        adRequest = new AdRequest.Builder().build();
//
//        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                Log.d("Banner", "Loaded");
//                isLoaded = true;
//            }
//
//            @Override
//            public void onAdFailedToLoad(LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//                Log.d("adsError", loadAdError.toString());
//                adView.setVisibility(View.GONE);
//                isLoaded = false;
//            }
//
//            @Override
//            public void onAdClicked() {
//                super.onAdClicked();
//                Log.d("Banner", "Clicked");
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//                Log.d("Banner", "Opened");
//            }
//        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String current_versionName = pInfo.versionName;
            MaterialTextView version = findViewById(R.id.version);
            version.setText("v" + current_versionName);
            if (dark) {
                version.setTextColor(getResources().getColor(R.color.white));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (pref.getBoolean("is_First_Run2", true)) {
            pref.edit().putBoolean("is_First_Run2", false).apply();
            who_layout.setVisibility(View.GONE);
        }
        if (bundle != null) {
            api = bundle.getString(API);
            no_attendance = bundle.getBoolean(NO_ATTENDANCE);
            result = bundle.getString(RESULTS);
        }

        sharedPreferences = getSharedPreferences(API, MODE_PRIVATE);
        CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(RESULT_STATUS);
        apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    showResult = documentChange.getDocument().getString(SHOW_RESULT);
                    showlectures = documentChange.getDocument().getString(SHOW_LECTURES);
                    showCustomTabs = documentChange.getDocument().getString(SHOW_CUSTOM_TABS);
                    read_database = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("fetch_file")));
                    sharedPreferences.edit().putString(SHOW_RESULT, showResult).apply();
                    sharedPreferences.edit().putString(SHOW_LECTURES, showlectures).apply();
                    sharedPreferences.edit().putString(SHOW_CUSTOM_TABS, showCustomTabs).apply();
                    custom_tabs_link = String.valueOf(documentChange.getDocument().getString("custom_tabs_link"));
                    AUTH_KEY = documentChange.getDocument().getString("auth_key");
                    APP_ID = documentChange.getDocument().getString("app_id");

                    if (showlectures.equals("0"))
                        navigationView.getMenu().findItem(R.id.lecture).setVisible(false);
                    if (showCustomTabs.equals("0")) {
                        navigationView.getMenu().findItem(R.id.customTabs).setVisible(false);
                    } else {
                        navigationView.getMenu().findItem(R.id.customTabs).setVisible(true);
                    }

                    if (sharedPreferences.getInt(READ_DATABASE, 0) < read_database) {
                        sharedPreferences.edit().putInt(READ_DATABASE, read_database).apply();
                        showBottomSheetDialog();
//                        downloadFile();
                    }
                    downloadStats(AUTH_KEY, APP_ID);
                }
            }
        });

        FirebaseConfig firebaseConfig = new FirebaseConfig();
        String json = firebaseConfig.fetch_latest_news(this);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.getInt("version") >= 1) {
                if (who_layout.getVisibility() == View.GONE && preferences.getInt("version", 0) < jsonObject.getInt("version")) {
                    who_layout.setVisibility(View.VISIBLE);
                }
                preferences.edit().putInt("version", jsonObject.getInt("version")).apply();
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
                if (preferences.getBoolean("close", false)) {
                    who_layout.setVisibility(View.GONE);
                }
                removetile.setOnClickListener(view -> {
                    editor.putBoolean("close", true);
                    who_layout.setVisibility(View.GONE);
                    editor.apply();
                });
            } else {
                who_layout.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            Log.d("error_cardtile", e.toString());
        }

        icon.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(this);


        if (dark) {
            cardView.setBackgroundColor(Color.parseColor("#141414"));
            heading.setTextColor(Color.parseColor("#FFFFFFFF"));
            heading_desp.setTextColor(Color.parseColor("#FFCCCCCC"));
            heading_desp.setTextColor(Color.parseColor("#FFCCCCCC"));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
            title.setTextColor(Color.parseColor("#ffffff"));
        }

        share.setOnClickListener(view -> {
            if (no_attendance) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                try {
                    Bitmap bitmap = ScreenshotUtils.getScreenShot(recyclerView);
                    if (bitmap != null) {
                        Toast.makeText(this, "Taking screenshot...", Toast.LENGTH_SHORT).show();
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
        navigationView.getMenu().findItem(R.id.pab).setVisible(false);

        studentnamePrefernces = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        String studentName = studentnamePrefernces.getString(STUDENT_NAME, "");
//
//        if (no_attendance) {
//            noAttendance();
//        } else {
//            processAttendance();
//        }
        setSupportActionBar(toolbar);

        headerView = navigationView.getHeaderView(0);
        if (!Constants.Offline_mode) {
            if (bundle != null) {
                String regis = bundle.getString(REGISTRATION_NUMBER);
                editor.putString("RegistrationNumber", regis);
            }
            editor.apply();
        }
        headerView.findViewById(R.id.changeTheme).setOnClickListener(view -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            ThemeFragment fragment = ThemeFragment.newInstance();
            fragment.show(getSupportFragmentManager(), "theme_fragment");
        });
        TextView name = headerView.findViewById(R.id.name);
        TextView reg = headerView.findViewById(R.id.reg);
        name.setText(studentName);
        String[] split = Objects.requireNonNull(studentName).split("\\s+");
        try {
            if (!split[0].isEmpty()) {
                title.setText("Hi, " + convertToTitleCaseIteratingChars(split[0]) + "!");
            } else {
                title.setText("Home");
            }
        } catch (Exception e) {
            title.setText("Home");
        }
        reg.setText(preferences.getString("RegistrationNumber", null));
        checkResult.setOnClickListener(view -> fetchResult());
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
                        String s = numberFormat((double) response.getInt("players"));
                        Log.d("size", String.valueOf(s.length()));
                        String word;

                        double d = Double.parseDouble(s.split("(?<=[\\d\\.])(?=[a-z])")[0]);
                        if (s.split("(?<=[\\d\\.])(?=[a-z])")[1].equals("k")) {
                            word = "Thousand";
                        } else if (s.split("")[1].equals("m")) {
                            word = "Million";
                        } else {
                            word = "Hundred";
                        }
                        download_stats.setText(getResources().getString(R.string.download_stats, d, word));
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
        //    private BottomSheetBehavior bottomSheetBehavior;
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottomprogressbar, null);
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
        SharedPreferences.Editor editor = preferences.edit();
        switch (item.getItemId()) {
            case R.id.sa:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check this out: tiny.cc/iter_awol \n ");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.lecture: {
                if (!Objects.equals(sharedPreferences.getString(SHOW_LECTURES, "0"), "0")) {
                    Intent intent = new Intent(AttendanceActivity.this, OnlineLectureSubjects.class);
                    switch (Objects.requireNonNull(sharedPreferences.getString(STUDENT_SEMESTER, "1"))) {
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
            case R.id.cd:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.github_url))));
                break;
            case R.id.lgout:
                if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    this.drawerLayout.closeDrawer(GravityCompat.START);
                }
                MaterialAlertDialogBuilder binder = new MaterialAlertDialogBuilder(AttendanceActivity.this);
                binder.setMessage("Do you want to logout?");
                binder.setTitle(Html.fromHtml("<font color='#ba000d'>Are you sure?</font>"));
                binder.setCancelable(false);
                binder.setPositiveButton("YES", (dialog, which) -> {
                    if (sub == null) {
                        sub = getSharedPreferences("sub",
                                Context.MODE_PRIVATE);
                    }
                    edit = sub.edit();
                    edit.putBoolean("logout", true);
                    edit.apply();
                    if (studentnamePrefernces != null) {
                        studentnamePrefernces.edit().clear().apply();
                    }
                    editor.putBoolean("close", false);
                    editor.apply();
                    //Clearing the saved data
                    if (Constants.offlineDataPreference != null) {
                        Constants.offlineDataPreference.edit().clear().apply();
                    }
                    if (sharedPreferences != null) {
                        sharedPreferences.edit().clear().apply();
                    }
                    if (preferences != null) {
                        preferences.edit().clear().apply();
                    }
                    FirebaseAuth.getInstance().signOut();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                    intent3.putExtra("logout_status", "0");
                    startActivity(intent3);
                });
                binder.setNegativeButton("NO", (dialog, which) -> dialog.cancel());
                binder.show();
                break;
            case R.id.result:
                if (Objects.equals(sharedPreferences.getString(SHOW_RESULT, ""), "0")) {
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
            case R.id.policy:
                Uri uri = Uri.parse("https://awol-iter.flycricket.io/privacy.html");
                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent2);
                break;
            case R.id.customTabs:
                if (!Objects.equals(sharedPreferences.getString(SHOW_CUSTOM_TABS, "0"), "0")) {
                    if (custom_tabs_link != null && !custom_tabs_link.isEmpty()) {
                        custom_tab(custom_tabs_link);
                        Log.d("custom_link", custom_tabs_link);
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
//        if (isConnected) {
//            if (!isLoaded) {
//                try {
//                    if (adRequest == null) {
//                        adRequest = new AdRequest.Builder().build();
//                    }
//                    adView.loadAd(adRequest);
//                } catch (Exception e) {
//                    //Exception
//                    adView.setVisibility(View.GONE);
//                }
//            }
//            if (Constants.Offline_mode) {
//                Constants.Offline_mode = false;
//                Snackbar.make(mainLayout, "Your connection is back. Please refresh the app", Snackbar.LENGTH_INDEFINITE)
//                        .setActionTextColor(Color.GREEN).setAction("REFRESH", v -> {
//                    startActivity(new Intent(this, MainActivity.class));
//                    finish();
//                }).show();
//            }
//        }
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
                builder.setToolbarColor(getResources().getColor(R.color.white));
            }
            builder.build().launchUrl(this, Uri.parse(url));
        } catch (Exception e) {
            Log.d("custom_tabs_link", Objects.requireNonNull(e.getMessage()));
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
}