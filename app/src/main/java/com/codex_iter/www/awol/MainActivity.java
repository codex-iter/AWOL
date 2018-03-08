package com.codex_iter.www.awol;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
EditText user, pass;
Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user= findViewById(R.id.user);
        pass= findViewById(R.id.pass);
        btn=  findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = user.getText().toString().trim();
                String p = pass.getText().toString().trim();
                String web="https://codex-bunk.herokuapp.com/?username="+u+"&password="+p;
                Intent intent= new Intent(MainActivity.this,home.class);
                intent.putExtra("web",web);
                startActivity(intent);
            }
        });
    }
}
