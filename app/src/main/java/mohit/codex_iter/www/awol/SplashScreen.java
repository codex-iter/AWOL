package mohit.codex_iter.www.awol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

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
            MyApplication.getInstance().clearApplicationData();
            SharedPreferences pref_new = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
            SharedPreferences.Editor editor_new = pref_new.edit();
            editor_new.putBoolean("is_First_Run", false);
            editor_new.commit();
        }

        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}