package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.ResultAdapter;
import codex.codex_iter.www.awol.exceptions.InvalidResponseException;
import codex.codex_iter.www.awol.model.ResultData;
import codex.codex_iter.www.awol.utilities.Constants;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.RESULTS;

public class ResultActivity extends BaseThemedActivity implements ResultAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.recyclerViewDetailedResult)
    RecyclerView recyclerView;
    @BindView(R.id.NA)
    ConstraintLayout noAttendanceLayout;
    @BindView(R.id.NA_content)
    TextView tv;
    SharedPreferences userm;
    private String result;
    private int l;
    private ResultData[] ld;
    private ArrayList<ResultData> resultDataArrayList = new ArrayList<>();
    private int sem;
    private String totalCredit, sgpa, status, api;
    private BottomSheetDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailresults);

        Constants.offlineDataPreference = this.getSharedPreferences("OFFLINEDATA", Context.MODE_PRIVATE);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        if (dark) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }
        if (bundle != null) {
            result = bundle.getString(RESULTS);
            api = bundle.getString(API);
            Log.d(RESULTS, result);
        }
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
            arr = jObj1.getJSONArray("data");
            l = arr.length();
            ld = new ResultData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj;
                jObj = arr.getJSONObject(i);
                ld[i] = new ResultData();

                if (jObj != null) {
                    if (!jObj.has("Semesterdesc") || !jObj.has("stynumber") || !jObj.has("fail") ||
                            !jObj.has("totalearnedcredit") || !jObj.has("sgpaR")) {
                        throw new InvalidResponseException();
                    }

                    ld[i].setSemesterdesc(jObj.getString("Semesterdesc"));
                    ld[i].setStynumber(Integer.parseInt(jObj.getString("stynumber")));
                    ld[i].setFail(jObj.getString("fail"));
                    ld[i].setTotalearnedcredit(jObj.getString("totalearnedcredit"));
                    ld[i].setSgpaR(jObj.getString("sgpaR"));
                    if (jObj.has("cgpaR")) {
                        ld[i].setCgpaR(jObj.getString("cgpaR"));
                    } else {
                        ld[i].setCgpaR("Not Available");
                    }
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
            ResultData.ld = ld;
            if (!Constants.Offline_mode) {
                try {
                    resultDataArrayList.addAll(Arrays.asList(ld).subList(0, l));
                    saveResult(resultDataArrayList);
                } catch (Exception e) {
                    Log.d("error", "Result array might be null");
                    noResult();
                }
            } else {
                getSavedResult();
            }
            ResultAdapter resultAdapter = new ResultAdapter(this, resultDataArrayList, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @SuppressLint("CommitPrefEdits")
    public void saveResult(ArrayList<ResultData> resultDataArrayList) {
        try {
            Constants.offlineDataEditor = Constants.offlineDataPreference.edit();
            Gson gson = new Gson();
            String json = gson.toJson(resultDataArrayList);
            Constants.offlineDataEditor.putString("StudentResult", json);
            Constants.offlineDataEditor.apply();
        } catch (Exception e) {
            Log.d("error", "Something went wrong");
        }
    }

    public void getSavedResult() {
        try {
            Gson gson = new Gson();
            String json = Constants.offlineDataPreference.getString("StudentResult", null);
            Type type = new TypeToken<ArrayList<ResultData>>() {
            }.getType();
            resultDataArrayList = gson.fromJson(json, type);

            if (resultDataArrayList == null) {
                resultDataArrayList = new ArrayList<>();
            }
            if (resultDataArrayList.isEmpty()) {
                noResult();
            }
        } catch (Exception e) {
            noResult();
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
    public void onResultClicked(int sem, String totalCredit, String status, String sgpa) {
        this.totalCredit = totalCredit;
        this.status = status;
        this.sgpa = sgpa;
        this.sem = sem;
        String u = userm.getString("user", "");
        String p = userm.getString("pass", "");
        String s = String.valueOf(sem);
        Log.d("SEM", "onResultClicked: " + s);
        getData(api, u, p, s);
        showBottomSheetDialog();
    }

//    public static int convertDpToPixel(float dp) {
//        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
//        float px = dp * (metrics.densityDpi / 160f);
//        return Math.round(px);
//    }

    private void getData(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/detailedResult",
                response -> {
                    if (response.equals("169")) {
                        hideBottomSheetDialog();
                        Snackbar snackbar = Snackbar.make(main_layout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        hideBottomSheetDialog();
                        Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra("result", response);
                        intent.putExtra("Semester", String.valueOf(sem));
                        intent.putExtra("SGPA", sgpa);
                        intent.putExtra("TotalCredit", totalCredit);
                        intent.putExtra("Status", status);
                        startActivity(intent);
                    }
                },
                error -> {
                    hideBottomSheetDialog();
                    if (error instanceof AuthFailureError) {
                        if (Constants.offlineDataPreference.getString("StudentDetailResult" + sem, null) == null) {
                            Snackbar snackbar = Snackbar.make(main_layout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                            intent.putExtra("Semester", String.valueOf(sem));
                            startActivity(intent);
                        }
                    } else if (error instanceof ServerError) {
                        if (Constants.offlineDataPreference.getString("StudentDetailResult" + sem, null) == null) {
                            Snackbar snackbar = Snackbar.make(main_layout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                            intent.putExtra("Semester", String.valueOf(sem));
                            startActivity(intent);
                        }
                    } else if (error instanceof NetworkError) {
                        Log.e("Volley_error", String.valueOf(error));
                        if (Constants.offlineDataPreference.getString("StudentDetailResult" + sem, null) == null) {
                            Snackbar snackbar = Snackbar.make(main_layout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                            intent.putExtra("Semester", String.valueOf(sem));
                            startActivity(intent);
                        }
                    } else {
                        if (Constants.offlineDataPreference.getString("StudentDetailResult" + sem, null) == null) {
                            Snackbar snackbar = Snackbar.make(main_layout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                            intent.putExtra("Semester", String.valueOf(sem));
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
        //    private BottomSheetBehavior bottomSheetBehavior;
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.bottomprogressbar, null);
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
