package codex.codex_iter.www.awol.utilities;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String getJsonFromStorage(Context context, String fileName) {
        String jsonString;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((jsonString = bufferedReader.readLine()) != null) {
                sb.append(jsonString);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
