package codex.codex_iter.www.awol.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.Html;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadScrapFile {

    private Context context;
    private ProgressDialog progressDialog;

    public DownloadScrapFile(Context context) {
        this.context = context;
    }

    // DownloadTask for downloding video from URL
    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        String fileN = null;
        private String file;
        private boolean fromMainActivity;
        private String message;

        DownloadTask(Context context, String file, boolean fromMainActivity, String message) {
            this.context = context;
            this.file = file;
            this.message = message;
            this.fromMainActivity = fromMainActivity;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();
                input = connection.getInputStream();
                if (!fromMainActivity) {
                    fileN = file + ".txt";
                    File filename = new File(context.getFilesDir() + "/", fileN);
                    output = new FileOutputStream(filename);
                } else {
                    fileN = file + ".apk";
                    File filename = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileN);
                    output = new FileOutputStream(filename);
                }
                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (fromMainActivity) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle(Html.fromHtml("<font color='black'>Downloading new updates</font>"));
                progressDialog.setMessage(Html.fromHtml("<font color='black'>Please wait...</font>"));
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.d("DownloadError", result);
            } else {
                if (fromMainActivity) {
                    progressDialog.dismiss();
                    Utils.updateAvailable(context, message);
                }
            }
            MediaScannerConnection.scanFile(context,
                    new String[]{context.getFilesDir() + "/", fileN}, null,
                    (newpath, newuri) -> {
                        Log.i("ExternalStorage", "Scanned " + newpath + ":");
                        Log.i("ExternalStorage", "-> uri=" + newuri);
                    });
            MediaScannerConnection.scanFile(context,
                    new String[]{String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)), fileN}, null,
                    (newpath, newuri) -> {
                        Log.i("ExternalStorage", "Scanned " + newpath + ":");
                        Log.i("ExternalStorage", "-> uri=" + newuri);
                    });

        }
    }

    //hare you can start downloding video
    public void newDownload(String url, String filename, boolean fromMainActivity, String message) {
        final DownloadTask downloadTask = new DownloadTask(context, filename, fromMainActivity, message);
        downloadTask.execute(url);
    }
}
