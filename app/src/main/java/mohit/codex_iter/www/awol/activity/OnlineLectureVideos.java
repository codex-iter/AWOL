package mohit.codex_iter.www.awol.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import mohit.codex_iter.www.awol.model.Lecture;

import static mohit.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;

public class OnlineLectureVideos extends AppCompatActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recyclerViewDetailedResult)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailresults);

        ButterKnife.bind(this);
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
        Toast.makeText(this, video_link, Toast.LENGTH_SHORT).show();
    }
}
