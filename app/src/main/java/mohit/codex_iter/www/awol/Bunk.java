package mohit.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;



public class Bunk extends BaseThemedActivity {

    EditText atndedt, bnkedt, taredt;
    TextView result, left;
    TextView sub;
    Button target, bunk, attend;
    double absent, total, percent, present;
    ListData[] ld;

    private View view2, view1;


    public class DogsDropdownOnItemClickListener implements AdapterView.OnItemClickListener {

        String TAG = "DogsDropdownOnItemClickListener.java";

        @Override
        public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {

            Animation fadeInAnimation = AnimationUtils.loadAnimation(v.getContext(), android.R.anim.fade_in);
            fadeInAnimation.setDuration(10);
            v.startAnimation(fadeInAnimation);

            popupWindowDogs.dismiss();
            String selectedItemText = ((TextView) v).getText().toString();
            sub.setText(selectedItemText);
            total = Double.parseDouble(ld[arg2].getClasses());
                absent = Double.parseDouble(ld[arg2].getAbsent());
                percent = Double.parseDouble(ld[arg2].getPercent());
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



    }

    public PopupWindow popupWindowDogs(String[] subn) {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewDogs = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewDogs.setAdapter(dogsAdapter(subn));

        // set the item click listener
        listViewDogs.setOnItemClickListener(new DogsDropdownOnItemClickListener());

        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewDogs);

        return popupWindow;
    }


    private ArrayAdapter<String> dogsAdapter(String dogsArray[]) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dogsArray) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                String item = getItem(position);

                TextView listItem = new TextView(Bunk.this);

                listItem.setText(item);
                listItem.setTag(position);
                listItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                listItem.setBackgroundColor(Color.parseColor("#141414"));
                int padding = Math.round(getResources().getDisplayMetrics().density*16);
                listItem.setPadding(padding, padding, padding, padding);
                listItem.setTextColor(Color.WHITE);

                return listItem;
            }
        };

        return adapter;
    }

    PopupWindow popupWindowDogs;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bunk);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Plan a bunk");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        }
        this.ld = ListData.ld;
        String[] subn = new String[ld.length];
        for (int i = 0; i < ld.length; i++)
            subn[i] = ld[i].getSub();


        popupWindowDogs = popupWindowDogs(subn);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);


        if (dark){
            view1.setBackgroundColor(Color.parseColor("#A9A9A9"));
            view2.setBackgroundColor(Color.parseColor("#A9A9A9"));

        }


        sub = findViewById(R.id.sub);
        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindowDogs.showAsDropDown(view, 0, Math.round(getResources().getDisplayMetrics().density*16));
            }
        });
//        if (dark) {
//            ArrayAdapter a = new ArrayAdapter<>(this, R.layout.drop_down_dark, subn);
//            sub.setAdapter(a);
//        } else {
//            ArrayAdapter a = new ArrayAdapter<>(this, R.layout.drop_down, subn);
//            sub.setAdapter(a);
//        }

//        sub.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                total = Double.parseDouble(ld[position].getClasses());
//                absent = Double.parseDouble(ld[position].getAbsent());
//                percent = Double.parseDouble(ld[position].getPercent());
//                present = total - absent;
//                if (75 < percent) {
//                    int i;
//                    for (i = 0; i != -99; i++) {
//                        double p = (present / (total + i)) * 100;
//                        if (p < 75) break;
//                    }
//                    if (i > 1) {
//                        result.setText("Bunk " + (i - 1) + " classes for 75% ");
//                    } else {
//                        result.setText(" ");
//                    }
//                } else if (75 > percent) {
//                    int i;
//                    for (i = 0; i != -99; i++) {
//                        double p = ((present + i) / (total + i) * 100);
//                        if (p > 75) break;
//                    }
//                    if (i > 1) {
//                        result.setText("Attend " + (i - 1) + " classes for 75%");
//                    } else {
//                        result.setText(" ");
//                    }
//                }
//                left.setText("");
//            }
//
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                total = Double.parseDouble(ld[0].getClasses());
//                absent = Double.parseDouble(ld[0].getAbsent());
//                percent = Double.parseDouble(ld[0].getPercent());
//                present = total - absent;
//                if (75 < percent) {
//                    int i;
//                    for (i = 0; i != -99; i++) {
//                        double p = (present / (total + i)) * 100;
//                        if (p < 75) break;
//                    }
//                    if (i > 1) {
//                        result.setText("Bunk " + (i - 1) + " classes for 75% ");
//                    } else {
//                        result.setText("No bunk");
//                    }
//                } else if (75 > percent) {
//                    int i;
//                    for (i = 0; i != -99; i++) {
//                        double p = ((present + i) / (total + i) * 100);
//                        if (p > 75) break;
//                    }
//                    if (i > 1) {
//                        result.setText("Attend " + (i - 1) + " classes for 75%");
//                    } else {
//                        result.setText("");
//                    }
//                }
//                left.setText("");
//            }
//        });
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
                                          int s_t = 0;
                                          if (!s.equals("")) {
                                              s_t = Integer.parseInt(s);
                                          }
                                          InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                          if (imm != null) {
                                              imm.hideSoftInputFromWindow(taredt.getWindowToken(), 0);
                                          }
                                          if (s.equals("0") || s_t > 100 || s.equals("00") || s.equals("000")) {
                                              Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Valid Value",Snackbar.LENGTH_SHORT).show();
                                          } else if (s.equals(""))
                                              Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Some Value",Snackbar.LENGTH_SHORT).show();
                                          else if (s.equals("100") && absent > 0)
                                              Snackbar.make((LinearLayout) findViewById(R.id.ll),"Not Possible!!",Snackbar.LENGTH_SHORT).show();
                                          else{
                                              double tp = new Scanner(s).nextDouble();
                                              if (tp < percent) {
                                                  int i;
                                                  double p;
                                                  for (i = 0; i != -99; i++) {
                                                      p = (present / (total + i)) * 100;
                                                      if (p < tp) break;
                                                  }
                                                  if (i > 1000){
                                                      result.setText("Don't need to attend the classes.");
                                                  } else {
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
                                                  }
                                              } else if (tp > percent) {
                                                  int i;
                                                  for (i = 0; i != -99; i++) {
                                                      double p = ((present + i) / (total + i) * 100);
                                                      if (p > tp) break;
                                                  }
                                                  if (i > 1000) {
                                                      result.setText("Don't need to attend the classes.");
                                                  }else {
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
                                  }
        );

        attend.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String s = atndedt.getText().toString().trim();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(atndedt.getWindowToken(), 0);
                }
                if (s.equals(""))
                    Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Some Value",Snackbar.LENGTH_SHORT).show();
                else {
                    int c = new Scanner(s).nextInt();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    double p = ((present + c) / (total + c)) * 100;
                    if (p > 0) {
                        result.setText("Your attendance will be " + String.format(Locale.US, "%.2f", p) + "%");
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
                    } else {
                        Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Valid value",Snackbar.LENGTH_SHORT).show();
                    }

                }

            }
        });

        bunk.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                String s = bnkedt.getText().toString().trim();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(bnkedt.getWindowToken(), 0);
                }
                if (s.equals(""))
                    Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Some value",Snackbar.LENGTH_SHORT).show();

                else {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null)
                        if (inputManager != null) {
                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    int c = new Scanner(bnkedt.getText().toString().trim()).nextInt();
                    double p = ((present) / (total + c)) * 100;
                    if (p > 0) {
                        result.setText("Your attendance will be " + String.format(Locale.US, "%.2f", p) + "%");
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


                    } else {
                        Snackbar.make((LinearLayout) findViewById(R.id.ll),"Enter Valid Value",Snackbar.LENGTH_SHORT).show();

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }

        return true;
    }
}
