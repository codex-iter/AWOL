package codex.codex_iter.www.awol.utilities;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import codex.codex_iter.www.awol.R;

public class FirebaseConfig {
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public FirebaseConfig() {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configBuilder = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(43200)
                //12 hrs
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configBuilder);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
    }

    public String fetch_latest_news(Context context) {
        String new_value = mFirebaseRemoteConfig.getString("news_link");
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener((Activity) context, task -> {
                }).addOnFailureListener(e -> Log.d("error", e.toString()));
        Log.d("json", new_value);
        return new_value;
    }

    public int under_maintenance(Context context) {
        String new_value = mFirebaseRemoteConfig.getString("under_maintenance");
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener((Activity) context, task -> {
                }).addOnFailureListener(e -> Log.d("error", e.toString()));
        return Integer.parseInt(new_value);
    }
}
