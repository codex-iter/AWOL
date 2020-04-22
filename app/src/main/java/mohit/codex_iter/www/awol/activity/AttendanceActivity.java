package mohit.codex_iter.www.awol.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.os.Vibrator;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
import mohit.codex_iter.www.awol.MainActivity;
import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.adapter.AttendanceAdapter;
import mohit.codex_iter.www.awol.bottomsheet.BottomSheetFragment;
import mohit.codex_iter.www.awol.model.AttendanceData;
import mohit.codex_iter.www.awol.setting.SettingsActivity;
import mohit.codex_iter.www.awol.theme.ThemeFragment;
import mohit.codex_iter.www.awol.utilities.Constants;
import mohit.codex_iter.www.awol.utilities.ScreenshotUtils;

import static mohit.codex_iter.www.awol.utilities.Constants.API;
import static mohit.codex_iter.www.awol.utilities.Constants.LOGIN;
import static mohit.codex_iter.www.awol.utilities.Constants.NOATTENDANCE;
import static mohit.codex_iter.www.awol.utilities.Constants.REGISTRATION_NUMBER;
import static mohit.codex_iter.www.awol.utilities.Constants.RESULTS;
import static mohit.codex_iter.www.awol.utilities.Constants.RESULTSTATUS;
import static mohit.codex_iter.www.awol.utilities.Constants.SHOWRESULT;
import static mohit.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;

public class AttendanceActivity extends BaseThemedActivity {

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
    @BindView(R.id.covid19)
    TextView covid19;
    @BindView(R.id.covid_desp)
    TextView covid_desp;
    @BindView(R.id.whoCard)
    CardView cardView;
    @BindView(R.id.whologo)
    ImageView whologo;

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
    private String code;
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences sub, userm, studentnamePrefernces;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private AttendanceAdapter adapter;
    private AppUpdateManager appUpdateManager;
    private boolean no_attendance;
    private static final int MY_REQUEST_CODE = 1011;
    private String api;
    private boolean showResult, logincheck;
    private BottomSheetDialog dialog;

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

    ColorStateList csl2 = new ColorStateList(state2, color2);

    private static final String TAG = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        SharedPreferences preferences = getSharedPreferences("CLOSE", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            logincheck = bundle.getBoolean(LOGIN);
            api = bundle.getString(API);
            no_attendance = bundle.getBoolean(NOATTENDANCE);
            result = bundle.getString(RESULTS);
        }

        who_button.setOnClickListener(view -> {
            Uri uri = Uri.parse("https://www.who.int/emergencies/diseases/novel-coronavirus-2019");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        if (preferences.getBoolean("close", false)) {
            who_layout.setVisibility(View.GONE);
        }
        removetile.setOnClickListener(view -> {
            editor.putBoolean("close", true);
            who_layout.setVisibility(View.GONE);
            editor.apply();
        });

        if (dark) {
            whologo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            cardView.setBackgroundColor(Color.parseColor("#141414"));
            covid19.setTextColor(Color.parseColor("#FFFFFFFF"));
            covid_desp.setTextColor(Color.parseColor("#FFCCCCCC"));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        }
        CollectionReference apiCollection = FirebaseFirestore.getInstance().collection(RESULTSTATUS);
        apiCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null) {
                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        showResult = documentChange.getDocument().getBoolean(SHOWRESULT);
                    }
                }
            }
        });

        studentnamePrefernces = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        String studentName = studentnamePrefernces.getString(STUDENT_NAME, "");

        Menu menu = navigationView.getMenu();
        if (no_attendance) {
            MenuItem menuItem = menu.findItem(R.id.pab);
            menuItem.setEnabled(false);

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
                attendanceData[i].setBunk();
                avgat += Double.parseDouble(jObj.getString("TotalAttandence").trim());
                avgab += Integer.parseInt(attendanceData[i].getAbsent());
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
            adapter = new AttendanceAdapter(this, attendanceDataArrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(null);
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
            TextView name = headerView.findViewById(R.id.name);
            TextView reg = headerView.findViewById(R.id.reg);
            name.setText(studentName);
            reg.setText(preferences.getString("RegistrationNumber", null));
            TextView avat = headerView.findViewById(R.id.avat);
            avat.setText(preferences.getInt("AveragePresent", 0) + "%");
            TextView avab = headerView.findViewById(R.id.avab);
            avab.setText(preferences.getString("AverageAbsent", null));

            checkResult.setOnClickListener(view -> fetchResult());
            navigationView.setNavigationItemSelectedListener(
                    menuItem -> {
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.sa:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check this out: tiny.cc/iter_awol \n ");
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            case R.id.abt:
                                Intent intenta = new Intent(AttendanceActivity.this, AboutActivity.class);
                                startActivity(intenta);
                                break;
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
                                    edit = studentnamePrefernces.edit();
                                    edit.putBoolean("User_exists", false);
                                    edit.putString(STUDENT_NAME, null);
                                    edit.apply();
                                    editor.putBoolean("close", false);
                                    editor.apply();
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
                            case R.id.pab:
                                Intent intent = new Intent(AttendanceActivity.this, BunkActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.result:
                                if (!showResult) {
                                    Snackbar snackbar = Snackbar.make(mainLayout, "We will be back within 3-4 days", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                } else {
                                    fetchResult();
                                }
                                break;
                            case R.id.setting:
                                Intent intent1 = new Intent(getApplicationContext(), SettingsActivity.class);
                                startActivity(intent1);
                                break;
                            case R.id.policy:
                                Uri uri = Uri.parse("https://awol-iter.flycricket.io/privacy.html");
                                Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent2);
                                break;
                            case R.id.change_theme:
                                drawerLayout.closeDrawer(GravityCompat.START);
                                ThemeFragment fragment = ThemeFragment.newInstance();
                                fragment.show(getSupportFragmentManager(), "theme_fragment");
                                break;
                        }
                        return true;
                    });
        }
    }

    public void saveAttendance(ArrayList attendanceDataArrayList) {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("Attendance", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(attendanceDataArrayList);
        editor.putString("StudentAttendance", json);
        editor.apply();
    }

    public void getSavedAttendance() {
        Snackbar snackbar = Snackbar.make(mainLayout, "Offline mode enabled", Snackbar.LENGTH_SHORT);
        snackbar.show();
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("Attendance", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("StudentAttendance", null);
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

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

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
//
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
//
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
//                        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String Updated(JSONObject jObj, SharedPreferences sub, String code, int i) throws JSONException {
        if (sub.contains(code)) {
            JSONObject old = new JSONObject(sub.getString(code, ""));
            SharedPreferences status_lg = this.getSharedPreferences("status", 0);
            String status = status_lg.getString("status", "");
            if (status.equals("0")) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.mShare) {
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

    public void showBottomSheetDialogFragment() {
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void showBottomSheetDialog() {
        //    private BottomSheetBehavior bottomSheetBehavior;
        View view = getLayoutInflater().inflate(R.layout.bottomprogressbar, null);
        dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void hideBottomSheetDialog() {
        dialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }

    }
}


