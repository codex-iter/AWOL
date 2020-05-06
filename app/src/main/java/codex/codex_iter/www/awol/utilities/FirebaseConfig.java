package codex.codex_iter.www.awol.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.BuildConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import codex.codex_iter.www.awol.R;

public class FirebaseConfig {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public FirebaseConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings.Builder configBuilder = new FirebaseRemoteConfigSettings.Builder();
        if (BuildConfig.DEBUG) {
            long cacheInterval = 0;
            configBuilder.setMinimumFetchIntervalInSeconds(cacheInterval);
        }
        mFirebaseRemoteConfig.setConfigSettingsAsync(configBuilder.build());
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public String fetch_latest_news(Context context) {
        String new_value = mFirebaseRemoteConfig.getString("news_link");
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener((Activity) context, task -> {
//                    if (task.isSuccessful()) {
////                            Toast.makeText(context, "Fetched successfully", Toast.LENGTH_SHORT).show();
//                    } else {
////                        Toast.makeText(context, "UnFetched successfully", Toast.LENGTH_SHORT).show();
//                    }
                }).addOnFailureListener(e -> {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("error", e.toString());
        });
        return new_value;
    }
//
//    public int read_database(Context context) {
//        String new_value = mFirebaseRemoteConfig.getString("read_database");
//        mFirebaseRemoteConfig.fetchAndActivate()
//                .addOnCompleteListener((Activity) context, task -> {
//                    if (task.isSuccessful()) {
////                            Toast.makeText(context, "Fetched successfully", Toast.LENGTH_SHORT).show();
//                    } else {
////                        Toast.makeText(context, "UnFetched successfully", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(e -> {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
//            Log.d("error", e.toString());
//        });
//        return Integer.parseInt(new_value);
//    }
}