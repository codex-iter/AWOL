package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailedResultActivity extends BaseThemedActivity {

    private String mearnedCredits, msgpa, mstatus;
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

        TextView earnedCredits = findViewById(R.id.earnedCredits);
        TextView sgpa = findViewById(R.id.sgpa);
        TextView status = findViewById(R.id.status);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
           mearnedCredits = bundle.getString("TotalCredit");
           msgpa = bundle.getString("SGPA");
           mstatus = bundle.getString("Status");
        }

        earnedCredits.setText("Total CreaditsEarned : " + mearnedCredits);
        sgpa.setText("SGPA : " + msgpa);
//        if (mstatus.equals("N")) {
//            status.setText("Pass");
//        } else {
//            status.setText("No");
//        }

        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);

        recyclerView = findViewById(R.id.recyclerViewDetailedResult);

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
                arr = jObj1.getJSONArray("Semdata");
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
