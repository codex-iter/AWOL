package com.codex_iter.www.awol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText user, pass;
    Button btn;
    ProgressDialog pd;
    SharedPreferences userm;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        btn = findViewById(R.id.btn);
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = user.getText().toString().trim();
                String p = pass.getText().toString().trim();
                edit= userm.edit();
                edit.putString("user",u);
                edit.putString("pass",p);
                edit.commit();
                if(u.equals("")||p.equals(""))
                    Toast.makeText(getApplicationContext(),"empty credentials",Toast.LENGTH_SHORT).show();
                else {


                    if (haveNetworkConnection()) {
                        String web = "https://codex-bunk.herokuapp.com/?username=" + u + "&password=" + p;
                        new JsonTask().execute(web,u);
                    } else{
                        Toast.makeText(getApplicationContext(), "no network connection", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, home.class);
                        if(userm.contains(u)) {
                            String s = userm.getString(u, "");
                            intent.putExtra("result", s);
                            Toast.makeText(getApplicationContext(), "showing offline value for this user", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "no offline info for this user", Toast.LENGTH_SHORT).show();



                }  }  }
        });
        if (userm.contains("user")&&userm.contains("pass")) {
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            btn.performClick();
        }



    }


    private class JsonTask extends AsyncTask<String, String, String> {
         String u;
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                u=params[1];
                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                pd.setCanceledOnTouchOutside(true);
                pd.dismiss();

            } catch (FileNotFoundException e) {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "cannot establish connection!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
            } finally {
                if (connection != null) {
                    connection.disconnect();
                } else {
                    Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing())
                pd.dismiss();
            if (result.trim().equals("{}")) {
                user.setText("");
                pass.setText("");
                Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();

            } else {
                user.setText("");
                pass.setText("");

                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, home.class);
                intent.putExtra("result", result);
                edit.putString(u,result);
                edit.commit();
                startActivity(intent);

            }


        }
    }


    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}