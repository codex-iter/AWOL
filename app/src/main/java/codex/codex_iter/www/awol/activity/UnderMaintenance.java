package codex.codex_iter.www.awol.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import codex.codex_iter.www.awol.R;

public class UnderMaintenance extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_undermaintenance);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
