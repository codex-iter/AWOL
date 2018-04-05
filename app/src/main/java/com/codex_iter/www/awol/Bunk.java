package com.codex_iter.www.awol;
import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Scanner;

public class Bunk extends AppCompatActivity {

    EditText atndedt,bnkedt;
    Spinner taredt;
    TextView result,sub;
    Button target,bunk,attend;
    double absent,total,percent,present;
    String subject;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunk);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            subject=bundle.getString("sub");
            percent=new Scanner(bundle.getString("percent")).nextDouble();
            absent=new Scanner(bundle.getString("absent")).nextDouble();
            total=new Scanner(bundle.getString("total")).nextDouble();
        }
        present = total-absent;
        atndedt=findViewById(R.id.atndedt);
        bnkedt=findViewById(R.id.bnkedt);
        taredt=findViewById(R.id.taredt);
        attend=findViewById(R.id.attend);
        bunk=findViewById(R.id.bunk);
        target=findViewById(R.id.target);
        result=findViewById(R.id.result);
        sub=findViewById(R.id.sub);
        sub.setText(subject);
        String s[]={""," 60 "," 65 "," 70 "," 75 "," 80 "," 85 "," 90 "};
        ArrayAdapter a= new ArrayAdapter(this,R.layout.view, s);
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taredt.setAdapter(a);

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s= taredt.getSelectedItem().toString();
                    double tp = new Scanner(s).nextDouble();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (tp < percent) {
                        int i;
                        for (i = 0; i != -99; i++) {
                            double p = (present / (total + i)) * 100;
                            if (p <= tp) break;
                        }
                        result.setText("Bunk " + i + " classes for req attendance");
                    } else if (tp > percent) {
                        int i;
                        for (i = 0; i != -99; i++) {
                            double p = ((present + i) / (total + i) * 100);
                            if (p >= tp) break;
                        }
                        result.setText("Attend " + i + " classes for req attendance");
                    }
                }
            }
        );

        attend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s= atndedt.getText().toString().trim();

                if(s.equals(""))
                    Toast.makeText(getApplicationContext(),"enter some value",Toast.LENGTH_SHORT).show();
                else {
                    int c = new Scanner(s).nextInt();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    double p = ((present + c) / (total + c)) * 100;
                    result.setText("You attendance will be " + String.format("%.2f", p));
                }
            }
        });

        bunk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s= bnkedt.getText().toString().trim();

                if(s.equals(""))
                    Toast.makeText(getApplicationContext(),"enter some value",Toast.LENGTH_SHORT).show();
                else {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    int c = new Scanner(bnkedt.getText().toString().trim()).nextInt();
                    double p = ((present) / (total + c)) * 100;
                    result.setText("You attendance will be " + String.format("%.2f", p));
                }    }
        });

       bnkedt.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               taredt.setSelection(0);
               atndedt.setText("");
               return false;
           }
       });

        atndedt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bnkedt.setText("");
                taredt.setSelection(0);
                return false;
            }
        });

        taredt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bnkedt.setText("");
                atndedt.setText("");
                return false;
            }
        });





    }
}
