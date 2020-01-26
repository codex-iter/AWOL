package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ResultActivity extends BaseThemedActivity implements ResultAdapter.OnItemClickListener {

    SharedPreferences userm;
    private RecyclerView recyclerView;
    private String[] r;
    private String result;
    private int l;
    private ResultData[] ld;
    private ArrayList<ResultData> resultList = new ArrayList<>();
    private ResultAdapter resultAdapter;
    private LinearLayout main_layout;
    private int sem;
    private String totalCredit, sgpa, status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setVisibility(View.GONE);
//        navigationView.setEnabled(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Results");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        main_layout = findViewById(R.id.main_layout);

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        Bundle bundle = getIntent().getExtras();
        recyclerView = findViewById(R.id.rl);

        if (dark) {
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        }


        if (bundle != null) {
            result = bundle.getString("result");
        }

        if (result != null) {
            r = result.split("kkk");
            result = r[0];
        }

        try {
            JSONObject jObj1 = null;
            if (result != null) {
                jObj1 = new JSONObject(result);
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
        } finally {
            ResultData.ld = ld;
            for (int i = 0; i < l; i++) {
                resultList.add(ld[i]);
            }
            resultAdapter = new ResultAdapter(this, resultList, this);
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
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        String u = userm.getString("user", "");
        String p = userm.getString("pass", "");
        String web = getString(R.string.link);
        getData(web, u, p);
    }


    private void getData(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/detailedResult",
                response -> {
                    if (response.equals("169")) {
                        Snackbar snackbar = Snackbar.make(main_layout, "Results not found", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        Intent intent = new Intent(ResultActivity.this, DetailedResultActivity.class);
                        response += "kkk" + param[1];
                        intent.putExtra("result", response);
//                        intent.putExtra("Semester",sem );
                        intent.putExtra("SGPA", sgpa);
                        intent.putExtra("TotalCredit", totalCredit);
                        intent.putExtra("Status", status);
                        startActivity(intent);
                    }
                },
                error -> {
                    // error
                    //showData(param[1], param[2]);
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
