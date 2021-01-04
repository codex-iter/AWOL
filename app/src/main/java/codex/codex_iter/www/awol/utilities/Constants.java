package codex.codex_iter.www.awol.utilities;

public class Constants {
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
    public static final String NO_ATTENDANCE = "noAttendance";
    public static final String STUDENT_SEMESTER = "stynumber";
    public static final String STUDENT_BRANCH = "branchdesc";
    public static final String STUDENT_YEAR = "academicyear";
    public static final String UPDATE_AVAILABLE = "update_available";
    public static final String UNDER_MAINTENANCE = "under_maintenance";
    // optional
    public static final String UPDATE_FILE_SIZE = "update_file_size";
    public static final String APP_LINK = "appLink";
    public static final String UPDATE_MESSAGE = "what's_new";
    // optional
    public static final String DRIVE_APP_ID = "download_id";
}
