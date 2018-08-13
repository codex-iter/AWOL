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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
    LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
ll=findViewById(R.id.ll);
ll.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    return false;
    }
});
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
                if(u.equals("") & p.equals(""))
                    Toast.makeText(MainActivity.this, "Enter your Details", Toast.LENGTH_SHORT).show();

                else {


                    if (haveNetworkConnection()) {
                        String web = "https://codex-bunk.herokuapp.com/attendance/?username=" + u + "&password=" + p;
                        new JsonTask().execute(web,u);
                    } else{
                        Toast.makeText(getApplicationContext(), "no network connection", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, home.class);
                        if(userm.contains(u)) {
                            if(p.equals(userm.getString("pass", ""))){
                            String s = userm.getString(u, "");
                            intent.putExtra("result", s);
                            Toast.makeText(getApplicationContext(), "showing offline value for this user", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                        else{
                                Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT).show();
                            }}
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
                u = params[1];
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);
                connection.connect();
                if(connection.getResponseCode()==400)
                    throw new MalformedURLException();
                    InputStream stream = connection.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));


                    StringBuffer buffer = new StringBuffer();
                    String line = "";

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");

                    }

                    return buffer.toString();


            } catch (MalformedURLException e) {
                return "invalid credentials";

            } catch (IOException e) {
                return "Cannot connect to servers right now";
            } catch (Exception e) {
                return "Cannot connect to servers right now";
            } finally {
                if (connection != null) {
                    connection.disconnect();
                } else {
                    return "cannot establish connection";
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    return "cannot establish connection";


                }
        }}

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing())
                pd.dismiss();
            if (result.trim().equals("{}")) {
                user.setText("");
                pass.setText("");
                Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();

            } else if (result.equals("invalid credentials")) {
                Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT).show();
            } else if (result.equals("cannot establish connection")) {
                Toast.makeText(getApplicationContext(), "cannot establish connection", Toast.LENGTH_SHORT).show();
            } else if (result.equals("Cannot connect to servers right now")) {
                Toast.makeText(getApplicationContext(), "Cannot connect to servers right now", Toast.LENGTH_SHORT).show();
            } else {
                user.setText("");
                pass.setText("");

                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, home.class);
                result += "kkk" + u;
                intent.putExtra("result", result);
                edit.putString(u, result);
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
