package com.codex_iter.www.awol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Bunk extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunk);
        Bundle bundle = getIntent().getExtras();
        int position;
        if(bundle!=null)
        {
            position=bundle.getInt("pos");

        }
    }
}
