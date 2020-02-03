package mohit.codex_iter.www.awol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static mohit.codex_iter.www.awol.Constants.API;
import static mohit.codex_iter.www.awol.Constants.RESULTS;

public class ResultActivity extends BaseThemedActivity implements ResultAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_layout)
    LinearLayout main_layout;
    @BindView(R.id.recyclerViewDetailedResult)
    RecyclerView recyclerView;
    @BindView(R.id.bottomSheet_view)
    ConstraintLayout bottomSheetView;

    SharedPreferences userm;
    private String result;
    private int l;
    private ResultData[] ld;
    private ArrayList<ResultData> resultList = new ArrayList<>();
    private int sem;
    private String totalCredit, sgpa, status, api;
    private ProgressDialog pd;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailedresult_activity);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
            JSONObject jObj1 = null;
            if (result != null) {
                jObj1 = new JSONObject(result);
                Log.d("resultdetail", String.valueOf(jObj1));
            }
            JSONArray arr = null;
            if (jObj1 != null) {
                arr = jObj1.getJSONArray("data");
            }
            if (arr != null) {
                l = arr.length();
            }
            ld = new ResultData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = null;
                if (arr != null) {
                    jObj = arr.getJSONObject(i);
                }
                ld[i] = new ResultData();

                if (jObj != null) {
                    ld[i].setSemesterdesc(jObj.getString("Semesterdesc"));
                    ld[i].setStynumber(Integer.parseInt(jObj.getString("stynumber")));
                    ld[i].setFail(jObj.getString("fail"));
                    ld[i].setTotalearnedcredit(jObj.getString("totalearnedcredit"));
                }
                ld[i].setSgpaR(jObj != null ? jObj.getString("sgpaR") : null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Error : ", String.valueOf(e));
        } finally {
            ResultData.ld = ld;
            for (int i = 0; i < l; i++) {
                resultList.add(ld[i]);
            }
            ResultAdapter resultAdapter = new ResultAdapter(this, resultList, this);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(resultAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onResultClicked(int sem, String totalCredit, String status, String sgpa) {
        this.totalCredit = totalCredit;
        this.status = status;
        this.sgpa = sgpa;
        this.sem = sem;
        pd = new ProgressDialog(this, R.style.DialogLight);
        pd.setMessage("Fetching Result...");
        pd.setCanceledOnTouchOutside(false);
        bottomSheetBehavior.setPeekHeight(convertDpToPixel(60));
        //pd.show();
        String u = userm.getString("user", "");
        String p = userm.getString("pass", "");
        String s = String.valueOf(sem);
        Log.d("SEM", "onResultClicked: " + s);
        getData(api, u, p, s);
    }
    public static int convertDpToPixel(float dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
    private void getData(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/detailedResult",
                response -> {
                    bottomSheetBehavior.setPeekHeight(convertDpToPixel(0));
                    if (response.equals("169")) {
                        Snackbar snackbar = Snackbar.make(main_layout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra("result", response);
                        intent.putExtra("Semester",String.valueOf(sem));
                        intent.putExtra("SGPA", sgpa);
                        intent.putExtra("TotalCredit", totalCredit);
                        intent.putExtra("Status", status);
                        startActivity(intent);
                    }
                },
                error -> {
                    // error
                    //showData(param[1], param[2]);
                    bottomSheetBehavior.setPeekHeight(convertDpToPixel(0));
                    if (error instanceof AuthFailureError) {
                        Snackbar snackbar = Snackbar.make(main_layout, "Wrong Credentials!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof ServerError) {
                        Snackbar snackbar = Snackbar.make(main_layout, "Cannot connect to ITER servers right now.Try again", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else if (error instanceof NetworkError) {
                        Log.e("Volley_error", String.valueOf(error));
                        Snackbar snackbar = Snackbar.make(main_layout, "Cannot establish connection", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                }
        ) {
            //fix here
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }
}
