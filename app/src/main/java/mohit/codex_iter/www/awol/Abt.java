package mohit.codex_iter.www.awol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Abt extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";
    @SuppressWarnings("FieldCanBeLocal")
    private TextView  dis, para, dev, mo, pa, neh;
    @SuppressWarnings("FieldCanBeLocal")
    private LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        SharedPreferences theme = getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            if (dark)
                setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abt);
        ImageView fb, gi, gmal;
        TextView cdx;
        fb = findViewById(R.id.fb);
        gi = findViewById(R.id.git);
        gmal = findViewById(R.id.gmail);
        dis = findViewById(R.id.dis);
        para = findViewById(R.id.para);
        mo = findViewById(R.id.mohit);
        pa = findViewById(R.id.pawan);
        neh = findViewById(R.id.nehal);
        ImageView logo = findViewById(R.id.logo);
        dev = findViewById(R.id.dev);
        ll = findViewById(R.id.ll);
        if (!dark) {
            dev.setTextColor(Color.parseColor("#141831"));
            mo.setTextColor(Color.parseColor("#141831"));
            neh.setTextColor(Color.parseColor("#141831"));
            pa.setTextColor(Color.parseColor("#141831"));
            dis.setTextColor(Color.parseColor("#141831"));
            para.setTextColor(Color.parseColor("#141831"));
            logo.setBackgroundResource(R.drawable.codex_l);
        }else {
            logo.setBackgroundResource(R.drawable.codex);
            ll.setBackgroundColor(Color.parseColor("#141414"));
        }


        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/codexiter/?ref=br_rs")));
            }
        });
        gi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/codexiter?igshid=w8g2cfygo8sy")));
            }
        });
        gmal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "codexiter@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL");
                getApplicationContext().startActivity(Intent.createChooser(emailIntent, null));
            }
        });
        cdx = findViewById(R.id.cdx);
        cdx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/codex-iter/AWOL")));
            }
        });
    }
}
