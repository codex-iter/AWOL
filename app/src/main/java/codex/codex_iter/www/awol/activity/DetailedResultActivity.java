package codex.codex_iter.www.awol.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import codex.codex_iter.www.awol.adapter.DetailedResultAdapter;
import codex.codex_iter.www.awol.databinding.ActivityDetailresultsBinding;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.DetailResult;

import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;

public class DetailedResultActivity extends AppCompatActivity {
    private String msem;
    private String result;
    private int l;
    private DetailResult[] detailResultData;
    private final ArrayList<DetailResult> detailResultArrayList = new ArrayList<>();
    private ActivityDetailresultsBinding activityDetailresultsBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDetailresultsBinding = ActivityDetailresultsBinding.inflate(getLayoutInflater());
        setContentView(activityDetailresultsBinding.getRoot());

        Bundle bundle = getIntent().getExtras();
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        if (bundle != null) {
            result = bundle.getString(RESULTS);
            msem = bundle.getString("Semester");
        }

        try {
            JSONObject jObj1;
            if (result != null && !result.isEmpty()) {
                jObj1 = new JSONObject(result.split("kkk")[0]);
            } else {
                throw new InvalidResponseException();
            }

            JSONArray arr;
            JSONObject jOj2;
            jOj2 = jObj1.getJSONObject(msem);
            arr = jOj2.getJSONArray("Semdata");
            l = arr.length();

            detailResultData = new DetailResult[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj;
                jObj = arr.getJSONObject(i);
                detailResultData[i] = new DetailResult();

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
            Snackbar snackbar = Snackbar.make(activityDetailresultsBinding.mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            noResult();
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityDetailresultsBinding.mainLayout, "Something went wrong few things may not work", Snackbar.LENGTH_SHORT);
            snackbar.show();
            noResult();
        } finally {
            try {
                detailResultArrayList.addAll(Arrays.asList(detailResultData).subList(0, l));
            } catch (Exception e) {
                noResult();
            }
            DetailedResultAdapter resultAdapter = new DetailedResultAdapter(this, detailResultArrayList);
            activityDetailresultsBinding.recyclerViewDetailedResult.setHasFixedSize(true);
            activityDetailresultsBinding.recyclerViewDetailedResult.setAdapter(resultAdapter);
            activityDetailresultsBinding.recyclerViewDetailedResult.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void noResult() {
        activityDetailresultsBinding.recyclerViewDetailedResult.setVisibility(View.GONE);
        activityDetailresultsBinding.NA.setVisibility(View.VISIBLE);
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
