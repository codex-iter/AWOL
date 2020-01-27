package mohit.codex_iter.www.awol;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

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

import mohit.codex_iter.www.awol.theme.ThemeFragment;

import static com.crashlytics.android.Crashlytics.log;
import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


public class home extends BaseThemedActivity {
    private String result;
    private ListData[] ld;
    @SuppressWarnings("FieldCanBeLocal")
    private int l, avgab;
    @SuppressWarnings("FieldCanBeLocal")
    private double avgat;
    public RecyclerView rl;
    @SuppressWarnings("FieldCanBeLocal")
    private String[] r;
    public ArrayList<ListData> myList = new ArrayList<>();
    ;
    @SuppressWarnings("FieldCanBeLocal")
    private String code;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView name, reg, avat, avab;
    private SharedPreferences sub, userm;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private MyBaseAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private AppUpdateManager appUpdateManager;
    private boolean no_attendance;
    private LinearLayout main_layout;
    private static final int MY_REQUEST_CODE = 1011;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recycle);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        main_layout = findViewById(R.id.main_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Bundle bundle = getIntent().getExtras();


        appUpdateManager = AppUpdateManagerFactory.create(home.this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                IMMEDIATE,
                                home.this,
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
                                home.this,
                                MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        InstallStateUpdatedListener updatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };

        appUpdateManager.registerListener(updatedListener);

        boolean logincheck = false;
        if (bundle != null) {
            logincheck = bundle.getBoolean("Login_Check");
        }
        if (logincheck) {
            Snackbar snackbar = Snackbar.make(main_layout, "Success!", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        rl = findViewById(R.id.rl);
        if (dark) {
            rl.setBackgroundColor(Color.parseColor("#141414"));

        }
        if (bundle != null) {
            no_attendance = bundle.getBoolean("NO_ATTENDANCE");
        }
        if (no_attendance) {
            Menu menu = navigationView.getMenu();
            MenuItem menuItem = menu.findItem(R.id.pab);
            menuItem.setEnabled(false);

            rl.setVisibility(View.GONE);
            TextView tv = findViewById(R.id.NA);
            tv.setVisibility(View.VISIBLE);
            if (dark) {
                tv.setTextColor(Color.parseColor("#FFFFFF"));
                main_layout.setBackgroundColor(Color.parseColor("#141414"));
            } else {
                tv.setTextColor(Color.parseColor("#141414"));
            }
        }

        if (bundle != null) {
            result = bundle.getString("result");
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
            rl.setHasFixedSize(true);
            rl.setAdapter(adapter);
            rl.setLayoutManager(new LinearLayoutManager(this));

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(null);
            View headerView = navigationView.getHeaderView(0);
            name = headerView.findViewById(R.id.name);
            reg = headerView.findViewById(R.id.reg);
            name.setText("");
            if (bundle != null) {
                reg.setText(bundle.getString("REGISTRATION_NO"));
            }

            avat = headerView.findViewById(R.id.avat);
            avat.setText(String.format(Locale.US, "%.2f", avgat));
            avab = headerView.findViewById(R.id.avab);
            avab.setText(String.valueOf(avgab));
//            if (dark) {
//
//                navigationView.setItemTextColor(csl);
//                navigationView.setItemIconTintList(csl2);
//            }

            navigationView.setNavigationItemSelectedListener(
                    menuItem -> {
                        mDrawerLayout.closeDrawers();
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
                                pd = new ProgressDialog(this);
                                pd.setMessage("Fetching Result...");
                                pd.setCanceledOnTouchOutside(false);
                                pd.show();
                                userm = getSharedPreferences("user",
                                        Context.MODE_PRIVATE);
                                String u = userm.getString("user", "");
                                String p = userm.getString("pass", "");
                                String web = getString(R.string.link);
                                getData(web, u, p);
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
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                ThemeFragment fragment = ThemeFragment.newInstance();
                                fragment.show(getSupportFragmentManager(), "theme_fragment");
                                break;
                        }
                        return true;
                    });
        }
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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/result",
                response -> {
                    pd.dismiss();
                    if (response.equals("900")) {
                        Snackbar snackbar = Snackbar.make(main_layout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Intent intent = new Intent(home.this, ResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra("result", response);
                      //  edit.putString(param[1], response);
//                        edit.apply();
                        startActivity(intent);
                    }
                },
                error -> {
                    // error
                    //showData(param[1], param[2]);
                    pd.dismiss();
                    if (error instanceof AuthFailureError) {
                        Snackbar snackbar=Snackbar.make(main_layout,"Wrong Credentials!",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        Snackbar snackbar=Snackbar.make(main_layout,"Cannot connect to ITER servers right now.Try again",Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof NetworkError) {
                        Log.e("Volley_error", String.valueOf(error));
                        Snackbar snackbar=Snackbar.make(main_layout,"Cannot establish connection",Snackbar.LENGTH_SHORT);
                        snackbar.show();
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
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.mShare) {
            if (no_attendance) {
                Snackbar snackbar = Snackbar.make(main_layout, "Attendance is currently unavailable", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                Bitmap bitmap = ScreenshotUtils.getScreenShot(rl);
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


    @Override
    public void onBackPressed() {


        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }

    }


}


