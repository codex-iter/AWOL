package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class DetailedResultActivity extends BaseThemedActivity {

    private String mearnedCredits, msgpa, mstatus, msem;
    SharedPreferences userm;
    private RecyclerView recyclerView;
    private String[] r;
    private String result;
    private int l;
    private DetailResultData[] ld;
    private ArrayList<DetailResultData> resultList = new ArrayList<>();
    private DetailedResultAdapter resultAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedresult_activity);

        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerViewDetailedResult);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        if (bundle != null) {
            mearnedCredits = bundle.getString("TotalCredit");
            msgpa = bundle.getString("SGPA");
            mstatus = bundle.getString("Status");
            msem = bundle.getString("Semester");
        }

        if (dark) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        if (bundle != null) {
            result = bundle.getString("result");
            Log.d("result", result);
        }

        if (result != null) {
            r = result.split("kkk");
            result = r[0];
        }

        try {
            JSONObject jObj1 = null;
            if (result != null) {
                jObj1 = new JSONObject(result);
                Log.d("resultdetail", String.valueOf(jObj1));
            }
            JSONArray arr = null;
            if (jObj1 != null) {
                JSONObject jOj2;
                jOj2 = jObj1.getJSONObject(msem);
                arr = jOj2.getJSONArray("Semdata");
                Log.d("resultdetail", String.valueOf(arr));
            }
            if (arr != null) {
                l = arr.length();
            }
            ld = new DetailResultData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = null;
                if (arr != null) {
                    jObj = arr.getJSONObject(i);
                }
                ld[i] = new DetailResultData();

                if (jObj != null) {
                    ld[i].setSubjectdesc(jObj.getString("subjectdesc"));
                    ld[i].setGrade(jObj.getString("grade"));
                    ld[i].setEarnedcredit(jObj.getString("earnedcredit"));
                    ld[i].setSubjectcode(jObj.getString("subjectcode"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error : ", String.valueOf(e));
        } finally {
            DetailResultData.ld = ld;
            for (int i = 0; i < l; i++) {
                resultList.add(ld[i]);
            }
            resultAdapter = new DetailedResultAdapter(this, resultList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
