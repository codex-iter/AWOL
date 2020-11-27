package codex.codex_iter.www.awol.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import codex.codex_iter.www.awol.model.Lecture;

import static codex.codex_iter.www.awol.utilities.Constants.API;
import static codex.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static codex.codex_iter.www.awol.utilities.Constants.VIDEO_URL;

public class OnlineLectureVideos extends BaseThemedActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    MaterialToolbar toolbar;
    @BindView(R.id.main_Layout)
    ConstraintLayout mainLayout;

    private String direct_link;
    private BottomSheetDialog dialog;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(API, MODE_PRIVATE);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Video Lectures");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        if (dark) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            recyclerView.setBackgroundColor(Color.parseColor("#141414"));
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.black));
            Objects.requireNonNull(toolbar.getNavigationIcon()).setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        }

        SharedPreferences sharedPreferences = getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SubjectLinks", null);
        Type type = new TypeToken<ArrayList<Lecture>>() {
        }.getType();

        ArrayList<Lecture> lectureArrayList = gson.fromJson(json, type);
        if (lectureArrayList == null) {
            lectureArrayList = new ArrayList<>();
        }
        Collections.sort(lectureArrayList, (lecture, t1) -> lecture.getName().compareTo(t1.getName()));
        OnlineLectureSubjectAdapter lecturesAdapter = new OnlineLectureSubjectAdapter(this, lectureArrayList, true, this);
        recyclerView.setAdapter(lecturesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClicked(String subject_name, String video_link) {
        //Fetching Direct link from url of box
        showBottomSheetDialog();
        getDirectLink(sharedPreferences.getString(API, null), video_link);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void getDirectLink(final String... param) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonObjec = new JSONObject();
        try {
            jsonObjec.put("link", param[1]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, param[0] + "/fetch", jsonObjec,
                response -> {
                    try {
                        hideBottomSheetDialog();
                        direct_link = response.getString("direct_url");
                        Log.d("link", direct_link);
                        if (!direct_link.isEmpty()) {
                            Intent intent = new Intent(OnlineLectureVideos.this, VideoPlayer.class);
                            intent.putExtra(VIDEO_URL, direct_link);
                            startActivity(intent);
                        } else {
                            Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, Please try again!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    } catch (JSONException e) {
                        Log.d("error", Objects.requireNonNull(e.toString()));
                    }
                }, error -> {
            hideBottomSheetDialog();
            Log.d("volleyerror", error.toString());
            if (error instanceof ServerError) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Something went wrong, Please try again!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (error instanceof NetworkError) {
                Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar.make(mainLayout, "Cannot establish connection!!", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void showBottomSheetDialog() {
        //    private BottomSheetBehavior bottomSheetBehavior;
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_bottomprogressbar, null);
        if (dialog == null) {
            dialog = new BottomSheetDialog(this);
            dialog.setContentView(view);
            dialog.setCancelable(false);
        }
        dialog.show();

    }

    public void hideBottomSheetDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
