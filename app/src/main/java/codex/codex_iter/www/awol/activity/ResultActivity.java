package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.ResultAdapter;
import codex.codex_iter.www.awol.data.LocalDB;
import codex.codex_iter.www.awol.databinding.ActivityResultsBinding;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.Result;
import codex.codex_iter.www.awol.model.Student;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;

public class ResultActivity extends AppCompatActivity implements ResultAdapter.OnItemClickListener {
    private String result;
    private int l;
    private Result[] resultData;
    private final ArrayList<Result> resultArrayList = new ArrayList<>();
    private int sem;
    private String totalCredit;
    private String sgpa;
    private String status;
    private BottomSheetDialog dialog;
    private SharedPreferences sharedPreferences;
    private Student preferred_student;
    private ActivityResultsBinding activityResultsBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultsBinding = ActivityResultsBinding.inflate(getLayoutInflater());
        setContentView(activityResultsBinding.getRoot());
        setSupportActionBar(activityResultsBinding.toolbar);

        LocalDB localDB = new LocalDB(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferred_student = localDB.getStudent(sharedPreferences.getString("pref_student", null));
        if (preferred_student == null)
            throw new InvalidResponseException();

        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        activityResultsBinding.toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            result = bundle.getString(RESULTS);
            preferred_student.setOfflineResult(result);
        }

        try {
            JSONObject jObj1;
            if (result != null && !result.isEmpty()) {
                jObj1 = new JSONObject(result.split("kkk")[0]);
            } else {
                throw new InvalidResponseException();
            }
            JSONArray arr;
            arr = jObj1.getJSONArray("data");
            l = arr.length();
            resultData = new Result[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj;
                jObj = arr.getJSONObject(i);
                resultData[i] = new Result();

                if (jObj != null) {
                    if (!jObj.has("Semesterdesc") || !jObj.has("stynumber") || !jObj.has("fail") ||
                            !jObj.has("totalearnedcredit") || !jObj.has("sgpaR")) {
                        throw new InvalidResponseException();
                    }

                    resultData[i].setSemesterdesc(jObj.getString("Semesterdesc"));
                    resultData[i].setStynumber(Integer.parseInt(jObj.getString("stynumber")));
                    resultData[i].setFail(jObj.getString("fail"));
                    resultData[i].setTotalearnedcredit(jObj.getString("totalearnedcredit"));
                    resultData[i].setSgpaR(jObj.getString("sgpaR"));
                    if (jObj.has("cgpaR")) {
                        resultData[i].setCgpaR(jObj.getString("cgpaR"));
                    } else {
                        resultData[i].setCgpaR("Not Available");
                    }
                } else {
                    throw new InvalidResponseException();
                }
            }
            preferred_student.setResult(resultData);
        } catch (JSONException | InvalidResponseException e) {
            Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Invalid API Response", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (preferred_student != null) resultData = preferred_student.getResult();
            else noResult();
        } catch (Exception e) {
            Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Something went wrong few things may not work", Snackbar.LENGTH_SHORT);
            snackbar.show();
            if (preferred_student != null) resultData = preferred_student.getResult();
            else noResult();
        } finally {
            try {
                localDB.setStudent(this.sharedPreferences.getString("pref_student", null), preferred_student);
                resultArrayList.addAll(Arrays.asList(resultData).subList(0, l));
            } catch (Exception e) {
                noResult();
            }
            ResultAdapter resultAdapter = new ResultAdapter(this, resultArrayList, this);
            activityResultsBinding.recyclerViewDetailedResult.setHasFixedSize(true);
            activityResultsBinding.recyclerViewDetailedResult.setAdapter(resultAdapter);
            activityResultsBinding.recyclerViewDetailedResult.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    public void noResult() {
        activityResultsBinding.recyclerViewDetailedResult.setVisibility(View.GONE);
        activityResultsBinding.NA.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResultClicked(int sem, String totalCredit, String status, String sgpa) {
        this.totalCredit = totalCredit;
        this.status = status;
        this.sgpa = sgpa;
        this.sem = sem;
        getDetailedResultAPI(this.sharedPreferences.getString(API, ""), preferred_student.getRedgNo(), preferred_student.getPassword(), String.valueOf(sem));
        showBottomSheetDialog();
    }

    private void getDetailedResultAPI(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/detailedResult",
                response -> {
                    hideBottomSheetDialog();
                    if (response.equals("169")) {
                        Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                        response += "kkk" + param[1];

                        intent.putExtra(RESULTS, response);
                        intent.putExtra("Semester", String.valueOf(sem));
                        intent.putExtra("SGPA", sgpa);
                        intent.putExtra("TotalCredit", totalCredit);
                        intent.putExtra("Status", status);
                        startActivity(intent);
                    }
                },
                error -> {
                    hideBottomSheetDialog();
                    Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                    intent.putExtra("Semester", String.valueOf(sem));
                    if (error instanceof AuthFailureError) {
                        Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        if (preferred_student == null) {
                            Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        if (preferred_student == null) {
                            Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            startActivity(intent);
                        }
                    } else {
                        if (preferred_student == null) {
                            Snackbar snackbar = Snackbar.make(activityResultsBinding.mainLayout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            startActivity(intent);
                        }
                    }

                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);
                params.put("sem", param[3]);

                return params;
            }
        };
        queue.add(postRequest);
    }

    public void showBottomSheetDialog() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_bottomprogressbar, null);
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
        }
        dialog.show();

    }

    public void hideBottomSheetDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
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
