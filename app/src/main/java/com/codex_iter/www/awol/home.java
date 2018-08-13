package com.codex_iter.www.awol;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class home extends AppCompatActivity {
    String result,s;
    ListData ld[];
    int l,avgab;
    double avgat;
    public ListView rl;
    public ArrayList<ListData> myList;
    TextView name,reg,avat,avab;
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
        try {
            JSONObject jObj1 = new JSONObject(result);
            JSONArray arr = jObj1.getJSONArray("griddata");
            l = arr.length();
            ld = new ListData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                ld[i]=new ListData();
                ld[i].setCode(jObj.getString("subjectcode"));
                ld[i].setSub(jObj.getString("subject"));
                ld[i].setTheory(jObj.getString("Latt"));
                ld[i].setLab(jObj.getString("Patt"));
                ld[i].setUpd(jObj.getString("lastupdatedon"));
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
                            }

                            return true;
                        }
                    });
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
        }

