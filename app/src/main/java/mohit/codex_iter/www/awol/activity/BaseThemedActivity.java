package mohit.codex_iter.www.awol.activity;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import mohit.codex_iter.www.awol.R;

public abstract class BaseThemedActivity extends AppCompatActivity {

    protected SharedPreferences preferences;
    protected boolean dark;
    private static final String PREF_DARK_THEME = "dark_theme";
    private static final String PREFS_NAME = "prefs";
    private static final String THEME = "theme_pref";


    @Override
    public void setTheme(int resId) {

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        SharedPreferences theme = getSharedPreferences("theme", 0);
        dark = theme.getBoolean(PREF_DARK_THEME, false);
//        if (useDarkTheme && dark) {
//                super.setTheme(getDarkTheme());
//        }
//        else super.setTheme(getLightTheme());
        super.setTheme(theme.getInt(THEME, R.style.AppTheme_NoActionBar));


    }


}
