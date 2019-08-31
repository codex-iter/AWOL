package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class home extends AppCompatActivity {
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
    private  String code;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView name, reg, avat, avab;
    private SharedPreferences sub;
    private SharedPreferences.Editor edit;
    @SuppressWarnings("FieldCanBeLocal")
    private MyBaseAdapter adapter;
    private JSONObject old;
    private DrawerLayout dl;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
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
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        SharedPreferences theme = getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            if (dark)
                setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = getIntent().getExtras();
        myList = new ArrayList<>();
        rl = findViewById(R.id.rl);
        if (dark) {
            rl.setBackgroundColor(Color.parseColor("#141414"));
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(Color.parseColor("#141414"));
        }
        if (bundle != null)
            result = bundle.getString("result");

        r = result.split("kkk");
        result = r[0];
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
            adapter = new MyBaseAdapter(getApplicationContext(), myList);
            rl.setAdapter(adapter);
            dl = findViewById(R.id.drawer_layout);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            Objects.requireNonNull(actionbar).setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(null);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            name = headerView.findViewById(R.id.name);
            reg = headerView.findViewById(R.id.reg);
            name.setText("");

            /**
             *  @// FIXME: 9/1/2019
             *  Trending Crash Issue
             */
            reg.setText(r[1]);

            avat = headerView.findViewById(R.id.avat);
            avat.setText(String.format(Locale.US, "%.2f", avgat));
            avab = headerView.findViewById(R.id.avab);
            avab.setText(String.valueOf(avgab));
            if (dark) {
                navigationView.setItemTextColor(csl);
                navigationView.setItemIconTintList(csl2);
            }
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                            dl.closeDrawers();
                            switch (menuItem.getItemId()) {
                                case R.id.sa:
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check this out: bit.do/Awol \n ");
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
                                    /**
                                     * @// FIXME: 9/1/2019
                                     * Clear the JSONObj data here.
                                     */
                                    edit = sub.edit();
                                    edit.putBoolean("logout", true);
                                    edit.apply();
                                    finish();
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
                                    Uri uri = Uri.parse("https://awol.flycricket.io/privacy.html");
                                    Intent intent2 = new Intent(Intent.ACTION_VIEW, uri);
                                    startActivity(intent2);
                                    break;
                            }
                            return true;
                        }
                    });
        }
    }

    private String Updated(JSONObject jObj, SharedPreferences sub, String code, int i) throws JSONException {
        if (sub.contains(code)) {
            old = new JSONObject(sub.getString(code, ""));
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
        return super.onOptionsItemSelected(item);
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


