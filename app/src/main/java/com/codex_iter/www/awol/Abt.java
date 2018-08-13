package com.codex_iter.www.awol;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Abt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abt);
        ImageView fb,gi,gmal;
        TextView cdx;
        fb=findViewById(R.id.fb);
        gi=findViewById(R.id.git);
        gmal=findViewById(R.id.gmail);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/mohitlalitaagarwalnovember")));
            }
        });
        gi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mojito9542")));
            }
        });
        gmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "mohitagarwal9542@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL");
                getApplicationContext().startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        cdx=findViewById(R.id.cdx);
         cdx.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/codex-iter")));
             }
         });
    }
}
