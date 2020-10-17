package codex.codex_iter.www.awol.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;

import static codex.codex_iter.www.awol.utilities.Constants.DETAILS;

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
