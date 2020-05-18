package codex.codex_iter.www.awol.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
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

    static void updateAvailable(Context context, String message) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MESSAGE", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("MESSAGE", message).apply();
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "awol.apk");
        Log.d("path", file.getPath());
        if (file.exists()) {
            new AlertDialog.Builder(context)
                    .setTitle(Html.fromHtml("<font color='black'>Update Available</font>"))
                    .setMessage("What's new : \n" + sharedPreferences.getString("MESSAGE", "Bug fix"))
                    .setCancelable(false)
                    .setPositiveButton("UPDATE NOW", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", file),
                                "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    }).setNegativeButton("LATER", (dialog, which) -> dialog.dismiss()).show();
        } else {
            Toast.makeText(context, "Something went wrong. Please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
