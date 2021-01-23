package codex.codex_iter.www.awol.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utils {
    public static boolean isDialogShowing;
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

    public static void updateAvailable(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MESSAGE", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("MESSAGE", message).apply();
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + "awol.apk");
        Log.d("path", file.getPath());
        alertDialog
                .setTitle("Update Available")
                .setMessage("What's new : \n" + sharedPreferences.getString("MESSAGE", "Bug fix"))
                .setCancelable(false)
                .setPositiveButton("UPDATE NOW", (d, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", file),
                            "application/vnd.android.package-archive");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);
                }).setNegativeButton("LATER", (d, which) -> d.dismiss());
        AlertDialog dialog = alertDialog.create();
        if (!isDialogShowing && file.exists()) {
            isDialogShowing = true;
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
