package mohit.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Bunk extends AppCompatActivity {

    EditText atndedt, bnkedt, taredt;
    TextView result, left;
    Spinner sub;
    Button target, bunk, attend;
    double absent, total, percent, present;
    ListData[] ld;
    @SuppressWarnings("FieldCanBeLocal")
    private TextView target_at, bunk_at, attend_at;
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
        setContentView(R.layout.activity_bunk);
        LinearLayout ll = findViewById(R.id.ll);
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
        target_at = findViewById(R.id.target_at);
        bunk_at = findViewById(R.id.classes_bunk);
        attend_at = findViewById(R.id.going_attend);

        if (!dark) {
            target_at.setTextColor(Color.parseColor("#141831"));
            bunk_at.setTextColor(Color.parseColor("#141831"));
            attend_at.setTextColor(Color.parseColor("#141831"));
        } else {
            ll.setBackgroundColor(Color.parseColor("#141414"));
        }

        this.ld = ListData.ld;
        String[] subn = new String[ld.length];
        for (int i = 0; i < ld.length; i++)
            subn[i] = ld[i].getSub();
        sub = findViewById(R.id.sub);
        if (dark) {
            ArrayAdapter a = new ArrayAdapter<>(this, R.layout.drop_down_dark, subn);
            sub.setAdapter(a);
        } else {
            ArrayAdapter a = new ArrayAdapter<>(this, R.layout.drop_down, subn);
            sub.setAdapter(a);
        }

        sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                total = Double.parseDouble(ld[position].getClasses());
                absent = Double.parseDouble(ld[position].getAbsent());
                percent = Double.parseDouble(ld[position].getPercent());
                present = total - absent;
                if (75 < percent) {
                    int i;
                    for (i = 0; i != -99; i++) {
                        double p = (present / (total + i)) * 100;
                        if (p < 75) break;
                    }
                    if (i > 1) {
                        result.setText("Bunk " + (i - 1) + " classes for 75% ");
                    } else {
                        result.setText(" ");
                    }
                } else if (75 > percent) {
                    int i;
                    for (i = 0; i != -99; i++) {
                        double p = ((present + i) / (total + i) * 100);
                        if (p > 75) break;
                    }
                    if (i > 1) {
                        result.setText("Attend " + (i - 1) + " classes for 75%");
                    } else {
                        result.setText(" ");
                    }
                }
                left.setText("");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                total = Double.parseDouble(ld[0].getClasses());
                absent = Double.parseDouble(ld[0].getAbsent());
                percent = Double.parseDouble(ld[0].getPercent());
                present = total - absent;
                if (75 < percent) {
                    int i;
                    for (i = 0; i != -99; i++) {
                        double p = (present / (total + i)) * 100;
                        if (p < 75) break;
                    }
                    if (i > 1) {
                        result.setText("Bunk " + (i - 1) + " classes for 75% ");
                    }  else {
                        result.setText("No bunk");
                    }
                } else if (75 > percent) {
                    int i;
                    for (i = 0; i != -99; i++) {
                        double p = ((present + i) / (total + i) * 100);
                        if (p > 75) break;
                    }
                    if (i > 1) {
                        result.setText("Attend " + (i - 1) + " classes for 75%");
                    } else {
                        result.setText("");
                    }
                }
                left.setText("");
            }
        });
        present = total - absent;
        atndedt = findViewById(R.id.atndedt);
        bnkedt = findViewById(R.id.bnkedt);
        taredt = findViewById(R.id.taredt);
        attend = findViewById(R.id.attend);
        bunk = findViewById(R.id.bunk);
        target = findViewById(R.id.target);
        result = findViewById(R.id.result);
        left = findViewById(R.id.left);

        target.setOnClickListener(new View.OnClickListener() {
                                      @SuppressLint("SetTextI18n")
                                      @Override
                                      public void onClick(View v) {
                                          String s = taredt.getText().toString().trim();
                                          if (s.equals("0")) {
                                              Toast.makeText(Bunk.this, "Enter Valid Value", Toast.LENGTH_SHORT).show();
                                          } else if (s.equals(""))
                                              Toast.makeText(getApplicationContext(), "Enter Some Value", Toast.LENGTH_SHORT).show();
                                          else if (s.equals("100") && absent > 0)
                                              Toast.makeText(getApplicationContext(), "Not Possible!!", Toast.LENGTH_SHORT).show();
                                          else {
                                              double tp = new Scanner(s).nextDouble();
                                              InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                              if (getCurrentFocus() != null)
                                                  if (inputManager != null) {
                                                      inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                                  }
                                              if (tp < percent) {
                                                  int i;
                                                  double p;
                                                  for (i = 0; i != -99; i++) {
                                                      p = (present / (total + i)) * 100;
                                                      if (p < tp) break;
                                                  }
                                                  result.setText("Bunk " + (i - 1) + " classes for req attendance");
                                                  if ((int) tp != 75) {
                                                      int bunk = i - 1;
                                                      if (75 < tp) {

                                                          for (i = 0; i != 99; i++) {
                                                              p = (present / (total + bunk + i)) * 100;
                                                              if (p < 75) break;
                                                          }
                                                          left.setText("Bunk " + (i - 1) + " more classes for 75% ");
                                                      } else if (75 > tp) {
                                                          for (i = 0; i != -99; i++) {
                                                              p = ((present + i) / (total + bunk + i)) * 100;
                                                              if (p > 75) break;
                                                          }
                                                          left.setText("Attend " + (i - 1) + " classes after bunk for 75%");
                                                      }

                                                  } else
                                                      left.setText("");
                                              } else if (tp > percent) {
                                                  int i;
                                                  for (i = 0; i != -99; i++) {
                                                      double p = ((present + i) / (total + i) * 100);
                                                      if (p > tp) break;
                                                  }
                                                  result.setText("Attend " + i + " classes for req attendance");
                                                  if ((int) tp != 75) {
                                                      double attend = i;
                                                      double p;
                                                      if (75 < tp) {
                                                          for (i = 0; i != -99; i++) {
                                                              p = ((present + attend) / (total + attend + i)) * 100;
                                                              if (p < 75) break;
                                                          }
                                                          left.setText("Bunk " + (i - 1) + " classes afterwards for 75% ");
                                                      } else if (75 > tp) {
                                                          for (i = 0; i != -99; i++) {
                                                              p = ((present + attend + i) / (total + attend + i) * 100);
                                                              if (p > 75) break;
                                                          }
                                                          left.setText("Attend " + (i - 1) + " more classes for 75%");
                                                      }

                                                  }
                                              }
                                          }

                                      }
                                  }
        );

        attend.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String s = atndedt.getText().toString().trim();

                if (s.equals(""))
                    Toast.makeText(getApplicationContext(), "enter some value", Toast.LENGTH_SHORT).show();
                else {
                    int c = new Scanner(s).nextInt();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    double p = ((present + c) / (total + c)) * 100;
                    result.setText("You attendance will be " + String.format(Locale.US, "%.2f", p));
                    int i;
                    double pr;
                    if (75 < p) {

                        for (i = 0; i != -99; i++) {
                            pr = ((present + c) / (total + c + i)) * 100;
                            if (pr < 75) break;
                        }
                        left.setText("Bunk " + (i - 1) + " classes afterwards for 75% ");
                    } else if (75 > p) {
                        for (i = 0; i != -99; i++) {
                            pr = ((present + c + i) / (total + c + i) * 100);
                            if (pr > 75) break;
                        }
                        left.setText("Attend " + (i - 1) + " more classes for 75%");
                    }

                }

            }
        });

        bunk.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String s = bnkedt.getText().toString().trim();

                if (s.equals(""))
                    Toast.makeText(getApplicationContext(), "enter some value", Toast.LENGTH_SHORT).show();
                else {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    int c = new Scanner(bnkedt.getText().toString().trim()).nextInt();
                    double p = ((present) / (total + c)) * 100;
                    result.setText("You attendance will be " + String.format(Locale.US, "%.2f", p));
                    int i;
                    double pr;

                    if (75 < p) {

                        for (i = 0; i != -99; i++) {
                            pr = (present / (total + c + i)) * 100;
                            if (pr < 75) break;
                        }
                        left.setText("Bunk " + (i - 1) + " more classes for 75% ");
                    } else if (75 > p) {
                        for (i = 0; i != -99; i++) {
                            pr = ((present + i) / (total + c + i) * 100);
                            if (pr > 75) break;
                        }
                        left.setText("Attend " + (i - 1) + " classes after bunk for 75%");
                    }


                }
            }
        });

        bnkedt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                taredt.setText("");
                atndedt.setText("");
                result.setText("");
                left.setText("");
                return false;
            }
        });

        atndedt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bnkedt.setText("");
                taredt.setText("");
                result.setText("");
                left.setText("");
                return false;
            }
        });

        taredt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                bnkedt.setText("");
                atndedt.setText("");
                result.setText("");
                left.setText("");
                return false;
            }
        });


    }

}
