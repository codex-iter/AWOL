package mohit.codex_iter.www.awol.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.theme.ThemeItem;

public  class Constants {
    static List<ThemeItem> themeItems;
    public static List<ThemeItem> getThemes()
    {
        if(themeItems !=null)
            return themeItems;

            themeItems = new ArrayList<>();
            themeItems.add(new ThemeItem(R.color.grey50,R.style.AppTheme_NoActionBar,false));
            themeItems.add(new ThemeItem(R.color.colorPrimaryDark,R.style.AppTheme_Dark_NoActionBar,true));
            themeItems.add(new ThemeItem(R.color.red,R.style.AppTheme_NoActionBar_Red,false));
            themeItems.add(new ThemeItem(R.color.red,R.style.AppTheme_Dark_NoActionBar_Red,true));
            themeItems.add(new ThemeItem(R.color.orange,R.style.AppTheme_NoActionBar_Orange,false));
            themeItems.add(new ThemeItem(R.color.orange,R.style.AppTheme_Dark_NoActionBar_Orange,true));
            themeItems.add(new ThemeItem(R.color.blue,R.style.AppTheme_NoActionBar_Blue,false));
            themeItems.add(new ThemeItem(R.color.blue,R.style.AppTheme_Dark_NoActionBar_Blue,true));
            themeItems.add(new ThemeItem(R.color.green,R.style.AppTheme_NoActionBar_Green,false));
            themeItems.add(new ThemeItem(R.color.green,R.style.AppTheme_Dark_NoActionBar_Green,true));
    return themeItems;
    }


    public static void setDarkStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#141414"));
        }
    }

    public static final String API = "apiUrl";
    public static final String DETAILS = "details";
    public static final String RESULTSTATUS = "resultStatus";
    public static final String SHOWRESULT = "showResult";
    public static final String UNDERMAINTAINECE = "underMaintenance";
    public static final String REGISTRATION_NUMBER = "registrationNumber";
    public static final String RESULTS = "result";
    public static final String STUDENT_NAME = "studentName";
    public static final String LOGIN = "loginCheck";
    public static final String NOATTENDANCE = "noAttendance";
    public static Boolean Offlin_mode = false;
    public static SharedPreferences offlineDataPreference;
    public static SharedPreferences.Editor offlineDataEditor;
}
