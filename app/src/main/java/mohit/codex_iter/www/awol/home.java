package mohit.codex_iter.www.awol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import mohit.codex_iter.www.awol.theme.ThemeFragment;

import static mohit.codex_iter.www.awol.Constants.API;
import static mohit.codex_iter.www.awol.Constants.LOGIN;
import static mohit.codex_iter.www.awol.Constants.NOATTENDANCE;
import static mohit.codex_iter.www.awol.Constants.REGISTRATION_NUMBER;
import static mohit.codex_iter.www.awol.Constants.RESULTS;
import static mohit.codex_iter.www.awol.Constants.RESULTSTATUS;
import static mohit.codex_iter.www.awol.Constants.SHOWRESULT;
import static mohit.codex_iter.www.awol.Constants.STUDENT_NAME;

public class home extends BaseThemedActivity {

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
//    @BindView(R.id.bottomSheet_view)
//    ConstraintLayout bottomSheetView;

    private String result;
    private ListData[] ld;
    @SuppressWarnings("FieldCanBeLocal")
    private int l, avgab;
    @SuppressWarnings("FieldCanBeLocal")
    private double avgat;
    @SuppressWarnings("FieldCanBeLocal")
    private String[] r;
    public ArrayList<ListData> myList = new ArrayList<>();
    @SuppressWarnings("FieldCanBeLocal")
    private String code;
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences sub, userm, studentnamePrefernces;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private MyBaseAdapter adapter;
    private AppUpdateManager appUpdateManager;
    private boolean no_attendance;
    private static final int MY_REQUEST_CODE = 1011;
    private String api;
    private boolean showResult;
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
    private ProgressDialog pd;

    private static final String TAG = "Home";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);

        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
//        appUpdateManager = AppUpdateManagerFactory.create(home.this);

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
//                                home.this,
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
//                                home.this,
//                                MY_REQUEST_CODE);
//                    } catch (IntentSender.SendIntentException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        });
//
//        InstallStateUpdatedListener updatedListener = state -> {
//            if (state.installStatus() == InstallStatus.DOWNLOADED) {
//                popupSnackbarForCompleteUpdate();
//            }
//        };
//
//        appUpdateManager.registerListener(updatedListener);
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        boolean logincheck = false;
        if (bundle != null) {
            logincheck = bundle.getBoolean(LOGIN);
            api = bundle.getString(API);
            no_attendance = bundle.getBoolean(NOATTENDANCE);
        }
        studentnamePrefernces = this.getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        String studentName = studentnamePrefernces.getString(STUDENT_NAME, "");

        if (logincheck) {
            Snackbar snackbar = Snackbar.make(mainLayout, "Success!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        if (dark) {
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        }
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

        if (bundle != null) {
            result = bundle.getString(RESULTS);
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
            ld = new ListData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                ld[i] = new ListData();

                code = jObj.getString("subjectcode");
                String ck = Updated(jObj, sub, code, i);

                ld[i].setCode(code);
                ld[i].setSub(jObj.getString("subject"));
                ld[i].setTheory(jObj.getString("Latt"));
                ld[i].setLab(jObj.getString("Patt"));
                ld[i].setUpd(ck);
                ld[i].setPercent(jObj.getString("TotalAttandence"));
                ld[i].setBunk();
                avgat += Double.parseDouble(jObj.getString("TotalAttandence").trim());
                avgab += Integer.parseInt(ld[i].getAbsent());
            }
            avgat /= l;
            avgab /= l;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ListData.ld = ld;
            for (int i = 0; i < l; i++) {
                myList.add(ld[i]);
            }
            adapter = new MyBaseAdapter(this, myList);
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
            TextView name = headerView.findViewById(R.id.name);
            TextView reg = headerView.findViewById(R.id.reg);
            name.setText(studentName);
            if (bundle != null) {
                reg.setText(bundle.getString(REGISTRATION_NUMBER));
            }

            TextView avat = headerView.findViewById(R.id.avat);
            avat.setText(String.format(Locale.US, "%.2f", avgat));
            TextView avab = headerView.findViewById(R.id.avab);
            avab.setText(String.valueOf(avgab));

            checkResult.setOnClickListener(view -> fetchResult());
            navigationView.setNavigationItemSelectedListener(
                    menuItem -> {
                        drawerLayout.closeDrawers();
                        switch (menuItem.getItemId()) {
                            case R.id.sa:
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check this out: bit.do/iter_awol \n ");
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                break;
                            case R.id.abt:
                                Intent intenta = new Intent(home.this, Abt.class);
                                startActivity(intenta);
                                break;
                            case R.id.cd:
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.github_url))));
                                break;
                            case R.id.lgout:
                                AlertDialog.Builder binder = new AlertDialog.Builder(home.this);
                                binder.setMessage("Do you want to logout ?");
                                binder.setTitle(Html.fromHtml("<font color='#FF7F27'>Message</font>"));
                                binder.setCancelable(false);
                                binder.setPositiveButton(Html.fromHtml("<font color='#FF7F27'>Yes</font>"), (dialog, which) -> {
                                    edit = sub.edit();
                                    edit.putBoolean("logout", true);
                                    edit.apply();
                                    edit = studentnamePrefernces.edit();
                                    edit.putString(STUDENT_NAME, null);
                                    edit.apply();
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
                                Intent intent = new Intent(getApplicationContext(), Bunk.class);
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

    public void fetchResult() {
        pd = new ProgressDialog(this, R.style.DialogLight);
        pd.setMessage("Fetching Result...");
        pd.setCanceledOnTouchOutside(false);
//        pd.show();
//        bottomSheetBehavior.setPeekHeight(convertDpToPixel(60));
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/result",
                response -> {
                    if (response.equals("900")) {
                        hideBottomSheetDialog();
                        Snackbar snackbar = Snackbar.make(mainLayout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        hideBottomSheetDialog();
                        Intent intent = new Intent(home.this, ResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra(RESULTS, response);
                        intent.putExtra(API, api);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof AuthFailureError) {
                            hideBottomSheetDialog();
                            Snackbar snackbar = Snackbar.make(mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else if (error instanceof ServerError) {
                            hideBottomSheetDialog();
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else if (error instanceof NetworkError) {
                            hideBottomSheetDialog();
                            Log.e("Volley_error", String.valueOf(error));
                            Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
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
        getMenuInflater().inflate(R.menu.share, menu);
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
                ld[i].setOld(old.getString("TotalAttandence"));
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
        View view = getLayoutInflater().inflate(R.layout.bottomprogress, null);
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


