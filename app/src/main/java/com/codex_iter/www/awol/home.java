package com.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class home extends AppCompatActivity {
    String result,s;
    ListData ld[];
    int l,avgab;
    double avgat;
    public ListView rl;
    public ArrayList<ListData> myList;
    TextView name,reg,avat,avab;
    SharedPreferences sub;
    SharedPreferences.Editor edit;
    MyBaseAdapter adapter;
    DrawerLayout dl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = getIntent().getExtras();
        myList = new ArrayList<ListData>();
        if (bundle != null)
            result = bundle.getString("result");
         String r[]=result.split("kkk");
         result=r[0];
         avgab=0;
         avgat=0;
        sub = getSharedPreferences("sub",
                Context.MODE_PRIVATE);
        try {
            JSONObject jObj1 = new JSONObject(result);
            JSONArray arr = jObj1.getJSONArray("griddata");
            l = arr.length();
            ld = new ListData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                ld[i]=new ListData();
                String code=jObj.getString("subjectcode");
                String ck=Updated(jObj,sub,code,i);
                ld[i].setCode(code);
                ld[i].setSub(jObj.getString("subject"));
                ld[i].setTheory(jObj.getString("Latt"));
                ld[i].setLab(jObj.getString("Patt"));
                ld[i].setUpd(ck);
                ld[i].setPercent(jObj.getString("TotalAttandence"));
                avgat+=Double.parseDouble(jObj.getString("TotalAttandence").trim());
                avgab+=Integer.parseInt(ld[i].getAbsent());
            }
            avgat/=l;
            avgab/=l;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rl = findViewById(R.id.rl);
            ListData.ld=ld;
            for (int i = 0; i < l; i++) {
                myList.add(ld[i]);
                  }

           adapter = new MyBaseAdapter(getApplicationContext(), myList);
            rl.setAdapter(adapter);
            dl = findViewById(R.id.drawer_layout);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            ActionBar actionbar = getSupportActionBar();
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionbar.setTitle(null);
            NavigationView navigationView = findViewById(R.id.nav_view);
            View headerView = navigationView.getHeaderView(0);
            name = (TextView) headerView.findViewById(R.id.name);
            reg= (TextView) headerView.findViewById(R.id.reg);
            name.setText("");
            reg.setText(r[1]);
            avat= (TextView) headerView.findViewById(R.id.avat);
            avat.setText(String.format("%.2f",avgat));
            avab= (TextView) headerView.findViewById(R.id.avab);
            avab.setText(String.valueOf(avgab));
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            dl.closeDrawers();
                            switch (menuItem.getItemId())
                            {
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
                                    finish();
                                    break;
                                case R.id.pab:
                                            Intent intent = new Intent(getApplicationContext(), Bunk.class);
                                            startActivity(intent);
                                    break;
                            }

                            return true;
                        }
                    });
        }





            }
    private String Updated(JSONObject jObj, SharedPreferences sub, String code,int i) throws JSONException {
      if(sub.contains(code)) {
          JSONObject old = new JSONObject(sub.getString(code, ""));
          if ((!old.getString("Latt").equals(jObj.getString("Latt")))||(!old.getString("Patt").equals(jObj.getString("Patt")))) {
              jObj.put("updated", new Date().getTime());
              ld[i].setOld(old.getString("TotalAttandence"));
              edit = sub.edit();
              Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
              v.vibrate(400);
              edit.putString(code, jObj.toString());
              edit.commit();
              return "just now";
          } else return DateUtils.getRelativeTimeSpanString(old.getLong("updated"), new Date().getTime(), 0).toString();
      }

          else
          {
              jObj.put("updated", new Date().getTime());
              edit = sub.edit();
              Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
              v.vibrate(400);
              edit.putString(code, jObj.toString());
              edit.commit();
              return "just now";
          }
      }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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


