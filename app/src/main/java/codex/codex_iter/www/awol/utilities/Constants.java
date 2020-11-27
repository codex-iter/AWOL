package codex.codex_iter.www.awol.utilities;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.theme.ThemeItem;

public class Constants {
    static List<ThemeItem> themeItems;

    public static List<ThemeItem> getThemes() {
        if (themeItems != null)
            return themeItems;

        themeItems = new ArrayList<>();
        themeItems.add(new ThemeItem(R.color.green, R.style.AppTheme_NoActionBar_Green, false));
        themeItems.add(new ThemeItem(R.color.green, R.style.AppTheme_Dark_NoActionBar_Green, true));
        themeItems.add(new ThemeItem(R.color.red, R.style.AppTheme_NoActionBar_Red, false));
        themeItems.add(new ThemeItem(R.color.red, R.style.AppTheme_Dark_NoActionBar_Red, true));
        themeItems.add(new ThemeItem(R.color.orange, R.style.AppTheme_NoActionBar_Orange, false));
        themeItems.add(new ThemeItem(R.color.orange, R.style.AppTheme_Dark_NoActionBar_Orange, true));
        themeItems.add(new ThemeItem(R.color.blue, R.style.AppTheme_NoActionBar_Blue, false));
        themeItems.add(new ThemeItem(R.color.blue, R.style.AppTheme_Dark_NoActionBar_Blue, true));
        return themeItems;
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }

    public static final String API = "apiUrl";
    public static final String DETAILS = "details";
    public static final String RESULT_STATUS = "resultStatus";
    public static final String SHOW_RESULT = "showResult";
    public static final String SHOW_LECTURES = "showLectures";
    public static final String SHOW_CUSTOM_TABS = "showCustomTabs";
    public static final String CUSTOM_TABS_LINK = "custom_tabs_link";
    public static final String CUSTOM_TABS_LINK_2 = "custom_tabs_link_2";
    public static final String AUTH_KEY_ONESIGNAL = "auth_key";
    public static final String APP_ID_ONESIGNAL = "app_id";
    public static final String FETCH_FILE = "fetch_file";
    public static final String REGISTRATION_NUMBER = "registrationNumber";
    public static final String PASSWORD = "password";
    public static final String RESULTS = "result";
    public static final String STUDENT_NAME = "studentName";
    public static final String LOGIN = "loginCheck";
    public static final String NO_ATTENDANCE = "noAttendance";
    public static final String STUDENT_SEMESTER = "stynumber";
    public static final String STUDENT_BRANCH = "branchdesc";
    public static final String STUDENT_YEAR = "academicyear";
    public static final String REMOTE_CONFIG = "remote_config";
    public static final String READ_DATABASE = "read_database";
    public static final String READ_DATABASE2 = "read_database2";
    public static final String READ_DATABASE3 = "read_database3";
    public static final String CHECK_VISIBILITY = "check_visibility";
    public static final String UPDATE_AVAILABLE = "update_available";
    public static final String UNDER_MAINTENANCE = "under_maintenance";
    // optional
    public static final String UPDATE_FILE_SIZE = "update_file_size";
    public static final String APP_LINK = "appLink";
    public static final String UPDATE_MESSAGE = "what's_new";
    // optional
    public static final String DRIVE_APP_ID = "download_id";

    public static final String VIDEO_URL = "url";
    public static Boolean Offline_mode = false;
    public static SharedPreferences offlineDataPreference;
    public static SharedPreferences.Editor offlineDataEditor;
}
