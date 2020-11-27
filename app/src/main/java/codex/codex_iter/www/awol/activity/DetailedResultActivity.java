package codex.codex_iter.www.awol.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.DetailedResultAdapter;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.DetailResult;

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
    private String result;
    private int l;
    private DetailResult[] detailResultData;
    private ArrayList<DetailResult> detailResultArrayList = new ArrayList<>();

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
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        if (bundle != null) {
            result = bundle.getString(RESULTS);
            msem = bundle.getString("Semester");
        }

        if (dark) {
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
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
            Snackbar snackbar = Snackbar.make(main_layout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            noResult();
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(main_layout, "Something went wrong few things may not work", Snackbar.LENGTH_SHORT);
            snackbar.show();
            noResult();
        } finally {
            try {
                detailResultArrayList.addAll(Arrays.asList(detailResultData).subList(0, l));
            } catch (Exception e) {
                noResult();
            }
            DetailedResultAdapter resultAdapter = new DetailedResultAdapter(this, detailResultArrayList);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
