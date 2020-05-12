package codex.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import codex.codex_iter.www.awol.MainActivity;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.application.CleanCacheApplication;

import static codex.codex_iter.www.awol.utilities.Constants.DETAILS;

public class SplashScreenActivity extends AppCompatActivity {

    public SharedPreferences pref;
    private int check, clear_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.SplashTheme);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection(DETAILS);
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots != null) {
                for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                    check = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("under_maintenance")));
                    clear_data = Integer.parseInt(Objects.requireNonNull(documentChange.getDocument().getString("clear_data")));
                    pref.edit().putInt("CHECK", check).apply();

                    if (clear_data > pref.getInt("clear_data", 0)) {
                        Toast.makeText(this, "Data Successfully cleared.", Toast.LENGTH_SHORT).show();
                        pref.edit().putInt("clear_data", clear_data).apply();
                        CleanCacheApplication.getInstance().clearApplicationData();
                    }
                }
            }
        });
        if (pref.getInt("CHECK", 0) == 1) {
            Intent intent = new Intent(SplashScreenActivity.this, UnderMaintenance.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}