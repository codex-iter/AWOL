package codex.codex_iter.www.awol.activity;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.DetailedResultAdapter;
import codex.codex_iter.www.awol.model.DetailResultData;
import codex.codex_iter.www.awol.utilities.Constants;

import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;

public class DetailedResultActivity extends BaseThemedActivity {

    private String mearnedCredits, msgpa, mstatus, msem;
    SharedPreferences userm;
    private String result;
    private int l;
    private DetailResultData[] detailResultData;
    private ArrayList<DetailResultData> detailResultDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailresults);
        Bundle bundle = getIntent().getExtras();
        Toolbar toolbar = findViewById(R.id.toolbar);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewDetailedResult);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        if (bundle != null) {
            mearnedCredits = bundle.getString("TotalCredit");
            msgpa = bundle.getString("SGPA");
            mstatus = bundle.getString("Status");
            result = bundle.getString(RESULTS);
        }
        if (bundle != null) {
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

        if (result != null) {
            String[] r = result.split("kkk");
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
            detailResultData = new DetailResultData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = null;
                if (arr != null) {
                    jObj = arr.getJSONObject(i);
                }
                detailResultData[i] = new DetailResultData();

                if (jObj != null) {
                    detailResultData[i].setSubjectdesc(jObj.getString("subjectdesc"));
                    detailResultData[i].setGrade(jObj.getString("grade"));
                    detailResultData[i].setEarnedcredit(jObj.getString("earnedcredit"));
                    detailResultData[i].setSubjectcode(jObj.getString("subjectcode"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error : ", String.valueOf(e));
        } finally {
            DetailResultData.detailResultData = detailResultData;
            if (!Constants.Offlin_mode) {
                detailResultDataArrayList.addAll(Arrays.asList(detailResultData).subList(0, l));
                saveAttendance(detailResultDataArrayList, msem);
            } else {
                getSavedAttendance(msem);
            }
            DetailedResultAdapter resultAdapter = new DetailedResultAdapter(this, detailResultDataArrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void saveAttendance(ArrayList attendanceDataArrayList, String sem) {
//        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DetailResult", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
        Constants.offlineDataEditor = Constants.offlineDataPreference.edit();
        Gson gson = new Gson();
        String json = gson.toJson(attendanceDataArrayList);
        Constants.offlineDataEditor.putString("StudentDetailResult" + msem, json);
        Constants.offlineDataEditor.apply();
    }

    public void getSavedAttendance(String sem) {
//        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DetailResult", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = Constants.offlineDataPreference.getString("StudentDetailResult" + msem, null);
        Type type = new TypeToken<ArrayList<DetailResultData>>() {
        }.getType();
        detailResultDataArrayList = gson.fromJson(json, type);

        if (detailResultDataArrayList == null) {
            detailResultDataArrayList = new ArrayList<>();
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
