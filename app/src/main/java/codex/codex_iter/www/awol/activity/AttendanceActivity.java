package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.os.Vibrator;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
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
import codex.codex_iter.www.awol.model.AttendanceData;
import codex.codex_iter.www.awol.setting.SettingsActivity;
import codex.codex_iter.www.awol.theme.ThemeFragment;
import codex.codex_iter.www.awol.utilities.Constants;
import codex.codex_iter.www.awol.utilities.DownloadScrapFile;
import codex.codex_iter.www.awol.utilities.FirebaseConfig;
import codex.codex_iter.www.awol.utilities.ScreenshotUtils;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.LOGIN;
import static codex.codex_iter.www.awol.utilities.Constants.NOATTENDANCE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE;
import static codex.codex_iter.www.awol.utilities.Constants.READ_DATABASE2;
import static codex.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTSTATUS;
import static codex.codex_iter.www.awol.utilities.Constants.SHOWLECTUURES;
import static codex.codex_iter.www.awol.utilities.Constants.SHOWRESULT;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENTSEMESTER;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static codex.codex_iter.www.awol.utilities.Constants.offlineDataPreference;

public class AttendanceActivity extends BaseThemedActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_layout)
    LinearLayout mainLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.check_result)
    Button checkResult;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.NA_content)
    TextView tv;
    @BindView(R.id.rl)
    RecyclerView recyclerView;
    @BindView(R.id.NA)
    ConstraintLayout noAttendanceLayout;
    @BindView(R.id.who_layout)
    ConstraintLayout who_layout;
    @BindView(R.id.who_button)
    Button who_button;
    @BindView(R.id.removetile)
    ImageView removetile;
    @BindView(R.id.heading)
    TextView heading;
    @BindView(R.id.heading_desp)
    TextView heading_desp;
    @BindView(R.id.whoCard)
    CardView cardView;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String result;
    private AttendanceData[] attendanceData;
    @SuppressWarnings("FieldCanBeLocal")
    private int l, avgab;
    @SuppressWarnings("FieldCanBeLocal")
    private double avgat;
    @SuppressWarnings("FieldCanBeLocal")
    private String[] r;
    public ArrayList<AttendanceData> attendanceDataArrayList = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private String code, student_semester;
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences sub, userm, studentnamePrefernces, preferences, sharedPreferences, prefs;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private AttendanceAdapter adapter;
    private boolean no_attendance;
    private String api;
    private String showResult, showlectures;
    private BottomSheetDialog dialog;
    private int read_database;

    int[][] state = new int[][]{
            new int[]{android.R.attr.state_checked}, // checked
            new int[]{-android.R.attr.state_checked}
    };

    int[] color = new int[]{
            Color.rgb(255, 46, 84),
            (Color.BLACK)
    };

    ColorStateList csl = new ColorStateList(state, color);

    int[][] state2 = new int[][]{
            new int[]{android.R.attr.state_checked}, // checked
            new int[]{-android.R.attr.state_checked}
    };

    int[] color2 = new int[]{
            Color.rgb(255, 46, 84),
            (Color.GRAY)
    };

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
        View view_cus = getSupportActionBar().getCustomView();
        MaterialTextView title = view_cus.findViewById(R.id.title);
        ImageView icon = view_cus.findViewById(R.id.image);
        ImageView share = view_cus.findViewById(R.id.share);
        preferences = getSharedPreferences("CLOSE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Constants.offlineDataPreference = this.getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE);
        Bundle bundle = getIntent().getExtras();

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
            boolean logincheck = bundle.getBoolean(LOGIN);
            api = bundle.getString(API);
            no_attendance = bundle.getBoolean(NOATTENDANCE);
            result = bundle.getString(RESULTS);
        }

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
                Bitmap bitmap = ScreenshotUtils.getScreenShot(recyclerView);
                if (bitmap != null) {
                    File save = ScreenshotUtils.getMainDirectoryName(this);
                    File file = ScreenshotUtils.store(bitmap, "screenshot.jpg", save);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        shareScreenshot(file);
                    } else {
                        shareScreenshot_low(file);
                    }
                }
            }
        });
        navigationView.getMenu().findItem(R.id.pab).setVisible(false);
        sharedPreferences = getSharedPreferences(API, MODE_PRIVATE);
        CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(RESULTSTATUS);
        apiCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    showResult = documentChange.getDocument().getString(SHOWRESULT);
                    showlectures = documentChange.getDocument().getString(SHOWLECTUURES);
                    read_database = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("fetch_file")));
                    sharedPreferences.edit().putString(SHOWRESULT, showResult).apply();
                    sharedPreferences.edit().putString(SHOWLECTUURES, showlectures).apply();

                    if (showlectures.equals("0"))
                        navigationView.getMenu().findItem(R.id.lecture).setVisible(false);

                    if (sharedPreferences.getInt(READ_DATABASE, 0) < read_database) {
                        sharedPreferences.edit().putInt(READ_DATABASE, read_database).apply();
                        showBottomSheetDialog();
                        downloadfile();
                    }
                }
            }
        });

        studentnamePrefernces = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        String studentName = studentnamePrefernces.getString(STUDENT_NAME, "");

        if (no_attendance) {
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
        processAttendance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View headerView = navigationView.getHeaderView(0);
        if (!Constants.Offlin_mode) {
            editor.putInt("AveragePresent", (int) avgat);
            editor.putString("AverageAbsent", String.valueOf(avgab));
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
        if (!split[0].isEmpty()) {
            title.setText("Hi, " + convertToTitleCaseIteratingChars(split[0]) + "!");
        } else {
            title.setText("Home");
        }
        reg.setText(preferences.getString("RegistrationNumber", null));
        TextView avat = headerView.findViewById(R.id.avat);
        avat.setText(preferences.getInt("AveragePresent", 0) + "%");
        TextView avab = headerView.findViewById(R.id.avab);
        avab.setText(preferences.getString("AverageAbsent", null));

        checkResult.setOnClickListener(view -> fetchResult());
    }

    @Override
    protected void onResume() {
        super.onResume();
        processAttendance();
    }

    void processAttendance() {
        attendanceDataArrayList.clear();
        if (result != null) {
            r = result.split("kkk");
            result = r[0];
        }
        avgab = 0;
        avgat = 0;
        sub = getSharedPreferences("sub",
                Context.MODE_PRIVATE);

        try {
            JSONObject jObj1 = new JSONObject(result);
            JSONArray arr = jObj1.getJSONArray("griddata");
            l = arr.length();
            attendanceData = new AttendanceData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                attendanceData[i] = new AttendanceData();

                code = jObj.getString("subjectcode");
                String ck = Updated(jObj, sub, code, i);

                attendanceData[i].setCode(code);
                attendanceData[i].setSub(jObj.getString("subject"));
                attendanceData[i].setTheory(jObj.getString("Latt"));
                attendanceData[i].setLab(jObj.getString("Patt"));
                attendanceData[i].setUpd(ck);
                attendanceData[i].setPercent(jObj.getString("TotalAttandence"));
                attendanceData[i].setBunk(Integer.parseInt(Objects.requireNonNull(prefs.getString("pref_minimum_attendance", "75"))));
                avgat += Double.parseDouble(jObj.getString("TotalAttandence").trim());
                avgab += Integer.parseInt(attendanceData[i].getAbsent());
                student_semester = jObj.getString(STUDENTSEMESTER);
                sharedPreferences.edit().putString(STUDENTSEMESTER, student_semester).apply();
            }
            avgat /= l;
            avgab /= l;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            AttendanceData.attendanceData = attendanceData;
            if (!Constants.Offlin_mode) {
                attendanceDataArrayList.addAll(Arrays.asList(attendanceData).subList(0, l));
            } else {
                getSavedAttendance();
            }
            saveAttendance(attendanceDataArrayList);
            adapter = new AttendanceAdapter(this, attendanceDataArrayList, Integer.parseInt(Objects.requireNonNull(prefs.getString("pref_minimum_attendance", "75"))));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

        }
    }

    public void downloadfile() {
        StorageReference storageReference_data = FirebaseStorage.getInstance().getReference().child("data.txt");
        StorageReference storageReference_video = FirebaseStorage.getInstance().getReference().child("video.txt");
        DownloadScrapFile downloadScrapFile = new DownloadScrapFile(AttendanceActivity.this);
        storageReference_data.getDownloadUrl().addOnSuccessListener(uri -> {
            downloadScrapFile.newDownload(uri.toString(), "data", false, "");
            storageReference_video.getDownloadUrl().addOnSuccessListener(uri1 -> {
                downloadScrapFile.newDownload(uri1.toString(), "video", false, "");
                hideBottomSheetDialog();
            }).addOnFailureListener(e -> {
                hideBottomSheetDialog();
                Toast.makeText(AttendanceActivity.this, "Something went wrong.Please try again.", Toast.LENGTH_SHORT).show();
                Log.d("errorStorage", e.toString());
                finish();
            });
        }).addOnFailureListener(e -> {
            hideBottomSheetDialog();
            Toast.makeText(AttendanceActivity.this, "Something went wrong.Please try again.", Toast.LENGTH_SHORT).show();
            Log.d("errorStorage", e.toString());
            finish();
        });
    }

    @SuppressLint("CommitPrefEdits")
    public void saveAttendance(ArrayList attendanceDataArrayList) {
        Constants.offlineDataEditor = Constants.offlineDataPreference.edit();
        Gson gson = new Gson();
        String json = gson.toJson(attendanceDataArrayList);
        Constants.offlineDataEditor.putString("StudentAttendance", json);
        Constants.offlineDataEditor.apply();
    }

    public void getSavedAttendance() {
        Snackbar snackbar = Snackbar.make(mainLayout, "Offline mode enabled", Snackbar.LENGTH_SHORT);
        snackbar.show();
        Gson gson = new Gson();
        String json = Constants.offlineDataPreference.getString("StudentAttendance", null);
        Type type = new TypeToken<ArrayList<AttendanceData>>() {
        }.getType();
        attendanceDataArrayList = gson.fromJson(json, type);

        if (attendanceDataArrayList == null) {
            attendanceDataArrayList = new ArrayList<>();
        }
    }

    public void fetchResult() {
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String u = userm.getString("user", "");
        String p = userm.getString("pass", "");
        getData(api, u, p);
        showBottomSheetDialog();
    }

    private void getData(final String... param) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("Result", MODE_PRIVATE);
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
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof ServerError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Log.e("Volley_error", String.valueOf(error));
                            Intent intent = new Intent(AttendanceActivity.this, ResultActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        if (sharedPreferences.getString("StudentResult", null) == null) {
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
            SharedPreferences status_lg = this.getSharedPreferences("status", 0);
            String status = status_lg.getString("status", "");
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
                    v.vibrate(400);
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
                v.vibrate(400);
            }
            edit.putString(code, jObj.toString());
            edit.commit();
            return "Just now";
        }
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
        } catch (FileUriExposedException e) {
            Toast.makeText(this, "Something, went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareScreenshot_low(File file) {
        Uri uri = Uri.fromFile(file);//Convert file path into Uri for sharing
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.sharing_text));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//pass uri here
        startActivity(Intent.createChooser(intent, getString(R.string.share_title)));
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
                if (!Objects.equals(sharedPreferences.getString(SHOWLECTUURES, "0"), "0")) {
                    Intent intent = new Intent(AttendanceActivity.this, OnlineLectureSubjects.class);
                    switch (Objects.requireNonNull(sharedPreferences.getString(STUDENTSEMESTER, "1"))) {
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
                    intent.putExtra(STUDENTSEMESTER, student_semester);
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
                AlertDialog.Builder binder = new AlertDialog.Builder(AttendanceActivity.this);
                binder.setMessage("Do you want to logout ?");
                binder.setTitle(Html.fromHtml("<font color='#FF7F27'>Message</font>"));
                binder.setCancelable(false);
                binder.setPositiveButton(Html.fromHtml("<font color='#FF7F27'>Yes</font>"), (dialog, which) -> {
                    edit = sub.edit();
                    edit.putBoolean("logout", true);
                    edit.apply();
                    studentnamePrefernces.edit().clear().apply();
                    editor.putBoolean("close", false);
                    editor.apply();
                    //Clearing the saved data
                    offlineDataPreference.edit().clear().apply();
                    sharedPreferences.edit().clear().apply();
                    Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                    intent3.putExtra("logout_status", "0");
                    startActivity(intent3);
                });
                binder.setNegativeButton(Html.fromHtml("<font color='#FF7F27'>No</font>"), (dialog, which) -> dialog.cancel());
                AlertDialog alertDialog = binder.create();
                Window window = alertDialog.getWindow();
                WindowManager.LayoutParams wlp = null;
                if (window != null) {
                    wlp = window.getAttributes();
                }
                if (wlp != null) {
                    wlp.gravity = Gravity.BOTTOM;
                }
                if (wlp != null) {
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                }
                if (window != null) {
                    window.setAttributes(wlp);
                }
                alertDialog.show();
                final Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setBackgroundColor(Color.RED);
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setBackgroundColor(Color.GREEN);
                break;
            case R.id.pab: {
                Intent intent = new Intent(AttendanceActivity.this, BunkActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.result:
                if (sharedPreferences.getString(SHOWRESULT, "").equals("0")) {
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
            case R.id.contactus:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "codexiter@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL");
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(emailIntent, null));
        }
        return true;
    }

}


