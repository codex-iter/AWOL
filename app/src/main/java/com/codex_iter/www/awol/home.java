package com.codex_iter.www.awol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;

import static org.jsoup.Jsoup.connect;

public class home extends AppCompatActivity {
    String s, web;
    ListData ld[];
    public ListView rl;
    public ArrayList<ListData> myList = new ArrayList<ListData>();
    MyBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = getIntent().getExtras();
        rl = (ListView) findViewById(R.id.rl);
        adapter = new MyBaseAdapter(getApplicationContext(), myList);
        rl.setAdapter(adapter);
        if (bundle != null)
            web = bundle.getString("web");
        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                 
                    Document doc = connect(web).ignoreContentType(true).get();
                    s = doc.body().text().trim();
                    JSONObject jObj1 = new JSONObject(s);
                    JSONArray arr = jObj1.getJSONArray("griddata");
                    int l = arr.length();
                    ld = new ListData[l];
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jObj = arr.getJSONObject(i);
                        ld[i].setCode(jObj.getString("subjectcode"));
                        ld[i].setSub(jObj.getString("subject"));
                        ld[i].setTheory(jObj.getString("Latt"));
                        ld[i].setLab(jObj.getString("Patt"));
                        ld[i].setUpd(jObj.getString("lastupdatedon"));
                        ld[i].setPercent(jObj.getString("TotalAttandenc"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < ld.length; i++)
                            myList.add(ld[i]);

                        ((BaseAdapter) rl.getAdapter()).notifyDataSetChanged();
                    }
                });
            }
        };
    }
}