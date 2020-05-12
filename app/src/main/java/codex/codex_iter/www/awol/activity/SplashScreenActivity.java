package codex.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.application.CleanCacheApplication;
import codex.codex_iter.www.awol.utilities.FirebaseConfig;

public class SplashScreenActivity extends AppCompatActivity {

    public SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        if (pref.getBoolean("is_First_Run", true)) {
            CleanCacheApplication.getInstance().clearApplicationData();
            SharedPreferences pref_new = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor_new = pref_new.edit();
            editor_new.putBoolean("is_First_Run", false);
            editor_new.putBoolean("is_First_Run2", true);
            editor_new.apply();
        }

        FirebaseConfig firebaseConfig = new FirebaseConfig();
        int check = firebaseConfig.under_maintenance(this);
//        Toast.makeText(this, String.valueOf(check), Toast.LENGTH_SHORT).show();
        if (check > pref.getInt("CHECK", 0)) {
            pref.edit().putInt("CHECK", check).apply();
            Intent intent = new Intent(SplashScreenActivity.this, UnderMaintenance.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}