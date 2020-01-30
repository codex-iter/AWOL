package mohit.codex_iter.www.awol;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends BaseThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView = findViewById(R.id.splash);
        ConstraintLayout constraintLayout = findViewById(R.id.splash_l);
        if (dark) {
            constraintLayout.setBackgroundColor(Color.parseColor("#141414"));
            imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.codex));
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
            imageView.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.codex_l));
        }
        int SPLASH_DISPLAY_LENGTH = 2000;
        new Handler().postDelayed(() -> {
            /* Create an Intent that will start the Menu-Activity. */
            Intent mainIntent = new Intent(SplashScreen.this,MainActivity.class);
            SplashScreen.this.startActivity(mainIntent);
            SplashScreen.this.finish();
        }, SPLASH_DISPLAY_LENGTH);

    }
}