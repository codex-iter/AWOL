package mohit.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import mohit.codex_iter.www.awol.model.Lecture;

import static mohit.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;
import static mohit.codex_iter.www.awol.utilities.Constants.VIDEOURL;

public class OnlineLectureVideos extends BaseThemedActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);

        ButterKnife.bind(this);
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
        OnlineLectureSubjectAdapter lecturesAdapter = new OnlineLectureSubjectAdapter(this, lectureArrayList, true, this);
        recyclerView.setAdapter(lecturesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onClicked(String subject_name, String video_link) {
        Intent intent = new Intent(OnlineLectureVideos.this, VideoPlayer.class);
        intent.putExtra(VIDEOURL, video_link);
        startActivity(intent);
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
