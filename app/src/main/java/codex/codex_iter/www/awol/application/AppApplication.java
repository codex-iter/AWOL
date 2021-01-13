package codex.codex_iter.www.awol.application;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.onesignal.OneSignal;

import codex.codex_iter.www.awol.utilities.NotificationOpenedHandler;

import static codex.codex_iter.www.awol.utilities.ThemeHelper.FOLLOW_SYSTEM;
import static codex.codex_iter.www.awol.utilities.ThemeHelper.setAppTheme;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPreferences.getString("pref_theme", FOLLOW_SYSTEM);
        setAppTheme(theme != null ? theme : FOLLOW_SYSTEM);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .setNotificationOpenedHandler(new NotificationOpenedHandler(this))
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
