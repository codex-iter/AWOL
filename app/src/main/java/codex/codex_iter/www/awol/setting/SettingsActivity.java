package codex.codex_iter.www.awol.setting;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import java.util.Objects;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.databinding.ActivitySettingsBinding;

import static codex.codex_iter.www.awol.utilities.ThemeHelper.DARK_THEME;
import static codex.codex_iter.www.awol.utilities.ThemeHelper.FOLLOW_SYSTEM;
import static codex.codex_iter.www.awol.utilities.ThemeHelper.LIGHT_THEME;
import static codex.codex_iter.www.awol.utilities.ThemeHelper.setAppTheme;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @SuppressWarnings("FieldCanBeLocal")
    private ActivitySettingsBinding activitySettingsBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySettingsBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(activitySettingsBinding.getRoot());
        setupToolbar();
        setupPreferences();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String current_versionName = pInfo.versionName;
            activitySettingsBinding.version.setText("v" + current_versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }

    private void setupToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
    }

    private void setupPreferences() {
        getSupportFragmentManager().beginTransaction().replace(R.id.settingsFragment, new SettingsFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (Objects.equals(s, "pref_theme")) {
            switch (Objects.requireNonNull(sharedPreferences.getString(s, "Follow system"))) {
                case LIGHT_THEME:
                    setAppTheme(LIGHT_THEME);
                    break;
                case DARK_THEME:
                    setAppTheme(DARK_THEME);
                    break;
                default:
                    setAppTheme(FOLLOW_SYSTEM);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}