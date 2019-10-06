package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUriExposedException;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import mohit.codex_iter.www.awol.theme.ThemeFragment;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class home extends BaseThemedActivity {
    private String result;
    private ListData[] ld;
    @SuppressWarnings("FieldCanBeLocal")
    private int l, avgab;
    @SuppressWarnings("FieldCanBeLocal")
    private double avgat;
    public ListView rl;
    @SuppressWarnings("FieldCanBeLocal")
    private String[] r;
    public ArrayList<ListData> myList;
    @SuppressWarnings("FieldCanBeLocal")
    private String code;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView name, reg, avat, avab;
    private SharedPreferences sub;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private MyBaseAdapter adapter;
    private DrawerLayout dl;
    private LinearLayout main_layout;
    private DrawerLayout mDrawerLayout;


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




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        final NavigationView navigationView= findViewById(R.id.nav_view);
        main_layout = findViewById(R.id.main_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        Bundle bundle = getIntent().getExtras();

        myList = new ArrayList<>();
        rl = findViewById(R.id.rl);
        if (dark) {
            rl.setBackgroundColor(Color.parseColor("#141414"));

        }
        if (bundle != null)
            result = bundle.getString("result");

        if (result != null) {
            r = result.split("kkk");
        }
        result = r[0];
        avgab = 0;
        avgat = 0;
        sub = getSharedPreferences("sub",
                Context.MODE_PRIVATE);
        try {
            JSONObject jObj1 = new JSONObject(result);
            System.out.println(result);

            if(!jObj1.has("griddata")) {
                Menu menu=navigationView.getMenu();
                MenuItem menuItem=menu.findItem(R.id.pab);
                menuItem.setEnabled(false);
                rl.setVisibility(View.GONE);
                TextView tv=findViewById(R.id.NA);
                tv.setVisibility(View.VISIBLE);
                if (dark){
                    tv.setTextColor(Color.parseColor("FFCCCCCC"));
                    main_layout.setBackgroundColor(Color.parseColor("#141414"));
                } else {
                    tv.setTextColor(Color.parseColor("#141414"));
                }

            }
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
            adapter = new MyBaseAdapter(getApplicationContext(), myList);
            rl.setAdapter(adapter);
            dl = findViewById(R.id.drawer_layout);
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
            reg.setText(r[1]);

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
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            dl.closeDrawers();
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
                                    edit = sub.edit();
                                    edit.putBoolean("logout", true);
                                    edit.apply();
                                    Intent intent3 = new Intent(getApplicationContext(), MainActivity.class);
                                    intent3.putExtra("logout_status", "0");
                                    startActivity(intent3);
                                    break;
                                case R.id.pab:
                                    Intent intent = new Intent(getApplicationContext(), Bunk.class);
                                    startActivity(intent);
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
                                    fragment.show(getSupportFragmentManager(),"theme_fragment");
                                    break;
                            }
                            return true;
                        }
                    });
        }
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

            if (status.equals("0")){
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
            dl.openDrawer(GravityCompat.START);
            return true;
        }
        if (item.getItemId() == R.id.mShare) {
            Bitmap bitmap = ScreenshotUtils.getScreenShot(rl);
            if (bitmap != null){
                File save = ScreenshotUtils.getMainDirectoryName(this);
                File file = ScreenshotUtils.store(bitmap, "screenshot.jpg", save);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    shareScreenshot(file);
                } else {
                    shareScreenshot_low(file);
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

        } catch (FileUriExposedException e){
            Toast.makeText(this, "Something, went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    public void shareScreenshot_low(File file){
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


        if (this.dl.isDrawerOpen(GravityCompat.START)) {
            this.dl.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }

    }


}


