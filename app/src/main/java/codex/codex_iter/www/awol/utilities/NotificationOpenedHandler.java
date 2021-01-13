package codex.codex_iter.www.awol.utilities;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.browser.customtabs.CustomTabsIntent;

import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.application.AppApplication;

public class NotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private Application application;
    public NotificationOpenedHandler(Application application) {
        this.application = application;
    }

    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        Log.i("OSNotificationPayload", "result.notification.payload.toJSONObject().toString(): " + result.notification.payload.toJSONObject().toString());
         String launchUrl = result.notification.payload.launchURL;
        Log.i("OneSignalExample", "launchUrl set with value: " + launchUrl);
        if (launchUrl != null) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            builder.setShowTitle(true);
            builder.setStartAnimations(application, R.anim.slide_in_right, R.anim.slide_out_left);
            builder.setExitAnimations(application, R.anim.slide_in_left, R.anim.slide_out_right);
            builder.setToolbarColor(application.getResources().getColor(R.color.colorAccent));
            customTabsIntent.launchUrl(application, Uri.parse(launchUrl));
        }
    }
}
