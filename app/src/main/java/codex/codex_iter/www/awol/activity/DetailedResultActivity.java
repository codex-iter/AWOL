package codex.codex_iter.www.awol.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.DetailedResultAdapter;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.DetailResultData;
import codex.codex_iter.www.awol.utilities.Constants;

import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;

public class DetailedResultActivity extends BaseThemedActivity {

    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.recyclerViewDetailedResult)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.NA)
    ConstraintLayout noAttendanceLayout;
    @BindView(R.id.NA_content)
    MaterialTextView tv;
    private String msem;
    SharedPreferences userm;
    private String result;
    private int l;
    private DetailResultData[] detailResultData;
    private ArrayList<DetailResultData> detailResultDataArrayList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailresults);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        if (bundle != null) {
//            String mearnedCredits = bundle.getString("TotalCredit");
//            String msgpa = bundle.getString("SGPA");
//            String mstatus = bundle.getString("Status");
            result = bundle.getString(RESULTS);
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
            JSONObject jObj1;
            if (result != null) {
                jObj1 = new JSONObject(result);
                Log.d("resultdetail", String.valueOf(jObj1));
            } else {
                throw new InvalidResponseException();
            }
            JSONArray arr;
            JSONObject jOj2;
            jOj2 = jObj1.getJSONObject(msem);
            arr = jOj2.getJSONArray("Semdata");
            Log.d("resultdetail", String.valueOf(arr));
            l = arr.length();
            detailResultData = new DetailResultData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj;
                jObj = arr.getJSONObject(i);
                detailResultData[i] = new DetailResultData();

                if (jObj != null) {
                    if (!jObj.has("subjectdesc") || !jObj.has("grade") || !jObj.has("earnedcredit")
                            || !jObj.has("subjectcode")) {
                        throw new InvalidResponseException();
                    }
                    detailResultData[i].setSubjectdesc(jObj.getString("subjectdesc"));
                    detailResultData[i].setGrade(jObj.getString("grade"));
                    detailResultData[i].setEarnedcredit(jObj.getString("earnedcredit"));
                    detailResultData[i].setSubjectcode(jObj.getString("subjectcode"));
                } else {
                    throw new InvalidResponseException();
                }
            }
        } catch (JSONException | InvalidResponseException e) {
            Snackbar snackbar = Snackbar.make(main_layout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (!Constants.Offline_mode) {
                noResult();
            }
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(main_layout, "Something went wrong few things may not work", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (!Constants.Offline_mode) {
                noResult();
            }
        } finally {
            DetailResultData.detailResultData = detailResultData;
            if (!Constants.Offline_mode) {
                try {
                    detailResultDataArrayList.addAll(Arrays.asList(detailResultData).subList(0, l));
                    saveDetailedResult(detailResultDataArrayList, msem);
                } catch (Exception e) {
                    Log.d("error", "null");
                    noResult();
                }
            } else {
                getSavedDetailedResult(msem);
            }
            DetailedResultAdapter resultAdapter = new DetailedResultAdapter(this, detailResultDataArrayList);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void noResult() {
        recyclerView.setVisibility(View.GONE);
        noAttendanceLayout.setVisibility(View.VISIBLE);
        if (dark) {
            tv.setTextColor(Color.parseColor("#FFFFFF"));
            main_layout.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            tv.setTextColor(Color.parseColor("#141414"));
        }
    }

    public void saveDetailedResult(ArrayList<DetailResultData> attendanceDataArrayList, String sem) {
        try {
            Constants.offlineDataEditor = Constants.offlineDataPreference.edit();
            Gson gson = new Gson();
            String json = gson.toJson(attendanceDataArrayList);
            Constants.offlineDataEditor.putString("StudentDetailResult" + sem, json);
            Constants.offlineDataEditor.apply();
        } catch (Exception e) {
            Log.d("error", "Something went wrong");
        }
    }

    public void getSavedDetailedResult(String sem) {
//        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("DetailResult", MODE_PRIVATE);
        try {
            Gson gson = new Gson();
            String json = Constants.offlineDataPreference.getString("StudentDetailResult" + sem, null);
            Type type = new TypeToken<ArrayList<DetailResultData>>() {
            }.getType();
            detailResultDataArrayList = gson.fromJson(json, type);

            if (detailResultDataArrayList == null) {
                detailResultDataArrayList = new ArrayList<>();
            }

            if (detailResultDataArrayList.isEmpty()) {
                noResult();
            }
        } catch (Exception e) {
            noResult();
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
