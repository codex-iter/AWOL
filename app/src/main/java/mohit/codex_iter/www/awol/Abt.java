package mohit.codex_iter.www.awol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Abt extends BaseThemedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abt);
       Constants.setDarkStatusBar(this);
    }


    public void openFacebook(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/codexiter/?ref=br_rs")));
    }

    public void openInstagram(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/codexiter?igshid=w8g2cfygo8sy")));
    }

    public void openGmail(View view) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "codexiter@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback for AWOL");
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(emailIntent, null));
    }

    public void openGithub(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/codex-iter/AWOL")));
    }
}
