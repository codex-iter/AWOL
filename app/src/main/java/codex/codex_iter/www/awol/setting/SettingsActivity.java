package codex.codex_iter.www.awol.setting;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.activity.BaseThemedActivity;

public class SettingsActivity extends BaseThemedActivity {
    @SuppressWarnings("FieldCanBeLocal")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
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
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Settings");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        SharedPreferences preferences = getSharedPreferences("Dark", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(!dark){
            editor.putBoolean("dark", true);
            editor.apply();
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        } else {
            editor.putBoolean("dark", false);
            editor.apply();
        }
    }

    private void setupPreferences() {
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment, new SettingsFragment()).commit();
    }
}