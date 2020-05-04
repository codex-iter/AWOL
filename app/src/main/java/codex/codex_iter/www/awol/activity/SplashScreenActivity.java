package codex.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.application.CleanCacheApplication;

public class SplashScreenActivity extends AppCompatActivity {

    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();

//        editor.putBoolean("is_First_Run", true);
//        editor.commit();

        if (pref.getBoolean("is_First_Run", true)) {
            CleanCacheApplication.getInstance().clearApplicationData();
            SharedPreferences pref_new = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor_new = pref_new.edit();
            editor_new.putBoolean("is_First_Run", false);
            editor_new.apply();
        }

        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}