package com.codex_iter.www.awol;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        SharedPreferences theme = getSharedPreferences("theme",0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            if (dark)
                setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if (dark){
            coordinatorLayout = findViewById(R.id.coordinator);
            coordinatorLayout.setBackgroundColor(Color.parseColor("#141414"));
        }

        setupToolbar();
        setupPreferences();
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }
    }

    private void setupPreferences() {
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment, new SettingsFragment()).commit();
    }
}