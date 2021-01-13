package codex.codex_iter.www.awol.utilities;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    public static final String FOLLOW_SYSTEM = "Follow system";
    public static final String DARK_THEME = "Dark";
    public static final String LIGHT_THEME = "Light";

    public static void setAppTheme(String theme) {
        switch (theme) {
            case FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case DARK_THEME:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case LIGHT_THEME:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }
}
