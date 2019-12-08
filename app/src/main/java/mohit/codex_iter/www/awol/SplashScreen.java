package mohit.codex_iter.www.awol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    /** Duration of wait **/
    private static final String THEME="theme_pref";
    SharedPreferences theme ;

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    public void setTheme(int resId) {
        theme=getApplicationContext().getSharedPreferences("theme", 0);
        if(theme.contains(THEME)) {
            super.setTheme(theme.getInt(THEME, R.style.AppTheme));
        }
        else
          super.setTheme(R.style.splash_screen);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}