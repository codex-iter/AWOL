
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


public class home extends AppCompatActivity {
    String result,s;
    ListData ld[];
    int l;
    public ListView rl;
    public ArrayList<ListData> myList;
    MyBaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Bundle bundle = getIntent().getExtras();
        myList = new ArrayList<ListData>();
        if (bundle != null)
            result = bundle.getString("result");
        try {
            JSONObject jObj1 = new JSONObject(result);
            JSONArray arr = jObj1.getJSONArray("griddata");
            l = arr.length();
            ld = new ListData[l];
            for (int i = 0; i < l; i++) {
                JSONObject jObj = arr.getJSONObject(i);
                ld[i]=new ListData();
                ld[i].setCode(jObj.getString("subjectcode"));
                ld[i].setSub(jObj.getString("subject"));
                ld[i].setTheory(jObj.getString("Latt"));
                ld[i].setLab(jObj.getString("Patt"));
                ld[i].setUpd(jObj.getString("lastupdatedon"));
                ld[i].setPercent(jObj.getString("TotalAttandence"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rl = findViewById(R.id.rl);
            for (int i = 0; i < l; i++) {
                myList.add(ld[i]);
            }

           adapter = new MyBaseAdapter(getApplicationContext(), myList);
            rl.setAdapter(adapter);
        }
 }
}
