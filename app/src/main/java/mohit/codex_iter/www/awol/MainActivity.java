package mohit.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    EditText user, pass;
    Button btn;
    ProgressDialog pd;
    SharedPreferences userm, logout;
    SharedPreferences.Editor edit;
    LinearLayout ll;
    private static final String PREFS_NAME = "prefs";
    private static final String PREF_DARK_THEME = "dark_theme";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        final boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        SharedPreferences theme = getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);
        if (useDarkTheme) {
            if (dark)
                setTheme(R.style.AppTheme_Dark_NoActionBar);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = findViewById(R.id.ll);
        if (dark) {
            ll.setBackgroundColor(Color.parseColor("#141414"));
        }
        ll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputManager != null) {
                    inputManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        btn = findViewById(R.id.btn);
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        logout = getSharedPreferences("sub",
                Context.MODE_PRIVATE);

        if (!dark) {
            user.setTextColor(Color.parseColor("#141831"));
            pass.setTextColor(Color.parseColor("#141831"));
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = user.getText().toString().trim();
                String p = pass.getText().toString().trim();

                if (u.equals("") & p.equals(""))
                    Toast.makeText(MainActivity.this, "Enter your Details", Toast.LENGTH_SHORT).show();

                else {
                    if (haveNetworkConnection()) {
                        String web = getString(R.string.link);
                        getData(web, u, p);
                        edit = userm.edit();
                        edit.putString("user", u);
                        edit.putString(u + "pass", p);
                        edit.putString("pass", p);
                        edit.apply();
                        edit = logout.edit();
                        edit.putBoolean("logout", false);
                        edit.apply();

                    } else {
                        Toast.makeText(getApplicationContext(), "Something, went wrong.Try Again", Toast.LENGTH_SHORT).show();
                    }
//                    else {
//                       // showData(u, p);
//                    }
                }
            }
        });

        if (userm.contains("user") && userm.contains("pass") && logout.contains("logout") && !logout.getBoolean("logout", false)) {
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            btn.performClick();
        }


    }


//    private void showData(String u, String p) {
//        Toast.makeText(getApplicationContext(), "Something, went wrong.Try Again", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(MainActivity.this, home.class);
//        String test = userm.getString("user", "");
//        if(userm.contains(u)) {
//            if(p.equals(userm.getString(u+"pass", ""))){
//                edit=logout.edit();
//                edit.putBoolean("logout",false);
//                edit.apply();
//                String s = userm.getString(u, "");
//                intent.putExtra("result", s);
//                Toast.makeText(getApplicationContext(), "showing offline value for this user", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }else {
//                Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else
//        Toast.makeText(getApplicationContext(), "No offline data for the user", Toast.LENGTH_SHORT).show();

    //}


    private void getData(final String... param) {

        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/attendance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (pd.isShowing())
                            pd.dismiss();
                        user.setText("");
                        pass.setText("");
                        if (response.equals("404"))
                            Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                        else {

                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, home.class);
                            //getname(param);
                            response += "kkk" + param[1];
                            intent.putExtra("result", response);
                            edit.putString(param[1], response);
                            edit.commit();
                            startActivity(intent);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (pd.isShowing())
                            pd.dismiss();
                        user.setText("");
                        pass.setText("");
                        //showData(param[1], param[2]);
                        if (error instanceof AuthFailureError)
                            Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                        else if (error instanceof ServerError)
                            Toast.makeText(getApplicationContext(), "Cannot connect to servers right now.Try again", Toast.LENGTH_SHORT).show();
                        else if (error instanceof NetworkError) {
                            Log.e("Volley_error", String.valueOf(error));
                            Toast.makeText(getApplicationContext(), "cannot establish connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError)
                            Toast.makeText(getApplicationContext(), "Cannot connect to servers right now.Try again", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);

                return params;
            }
        };
        queue.add(postRequest);


    }
//    private void getname(final String... param){
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0]+"/studentinfo",
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response)  {
//
//                     try {
//                           JSONObject jobj  = new JSONObject(response);
//                           JSONArray jarr   = jobj.getJSONArray("detail");
//                           JSONObject jobj1 = jarr.getJSONObject(0);
//                           name = jobj1.getString("name");
//                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(), "cannot fetch name!!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {}}
//
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("user", param[1]);
//                params.put("pass", param[2]);
//
//                return params;
//            }
//        };
//        queue.add(postRequest);
//
//    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // connected to the internet


        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                // connected to wifi
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                haveConnectedMobile = true;
            }
        }

//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
