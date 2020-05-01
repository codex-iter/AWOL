package mohit.codex_iter.www.awol.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.adapter.OnlineLectureSubjectAdapter;
import mohit.codex_iter.www.awol.model.Lecture;
import mohit.codex_iter.www.awol.utilities.Utils;

import static mohit.codex_iter.www.awol.utilities.Constants.STUDENTSEMESTER;
import static mohit.codex_iter.www.awol.utilities.Constants.STUDENT_NAME;

public class OnlineLectureSubjects extends AppCompatActivity implements OnlineLectureSubjectAdapter.OnItemClickListener {

    @BindView(R.id.recyclerViewDetailedResult)
    RecyclerView recyclerView;

    private String sem;
    private ArrayList<Lecture> subjectName = new ArrayList<>();
    private ArrayList<Lecture> subjectLinks = new ArrayList<>();
    private SharedPreferences.Editor editor;
    private String jsonVideosLinks, jsonSubjectNames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailresults);

        ButterKnife.bind(this);
        jsonVideosLinks = Utils.getJsonFromAssets(getApplicationContext(), "data.json");
        jsonSubjectNames = Utils.getJsonFromAssets(getApplicationContext(), "video.json");

        SharedPreferences sharedPreferences = getSharedPreferences(STUDENT_NAME, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            sem = bundle.getString(STUDENTSEMESTER);
            editor.putString(STUDENTSEMESTER, sem);
            editor.apply();
        }
        getJSONdata("");
        OnlineLectureSubjectAdapter lecturesAdapter = new OnlineLectureSubjectAdapter(this, subjectName, false, this);
        recyclerView.setAdapter(lecturesAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    public void getJSONdata(String subname) {
        try {
            if (jsonVideosLinks != null && jsonSubjectNames != null) {
                JSONObject lectures = new JSONObject(jsonVideosLinks);
                JSONObject subject = new JSONObject(jsonSubjectNames);
                String[] semester = {"2nd", "4th"};
                for (String s : semester) {
                    if (sem.equals(s)) {
                        JSONObject subjects = lectures.getJSONObject(s);
                        JSONObject su = subject.getJSONObject(s);
                        Iterator<String> key_subject = su.keys();
                        while (key_subject.hasNext()) {
                            String keybranch = key_subject.next();
                            if (keybranch.equals("Computer Science & Information Technology")) {
                                Iterator<String> sem_no = subjects.keys();
                                JSONArray subjectsname = su.getJSONArray(keybranch);
                                while (sem_no.hasNext()) {
                                    String keysubject = sem_no.next();
                                    for (int i = 0; i < subjectsname.length(); i++) {
                                        if (keysubject.equals(subjectsname.get(i))) {
                                            JSONArray links = subjects.getJSONArray(keysubject);
                                            subjectName.add(new Lecture(keysubject));
                                            if (subname.equals(keysubject) && !subname.isEmpty()) {
                                                subjectLinks.clear();
                                                for (int j = 0; j < links.length(); j++) {
                                                    JSONObject json = links.getJSONObject(j);
                                                    subjectLinks.add(new Lecture(json.getString("name"), json.getString("link")));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            Log.d("error", e.getMessage());
        }
    }

    @Override
    public void onClicked(String subject_name, String video_link) {
        getJSONdata(subject_name);
        Intent intent = new Intent(this, OnlineLectureVideos.class);
        Gson gson = new Gson();
        String json = gson.toJson(subjectLinks);
        editor.putString("SubjectLinks", json);
        editor.apply();
        startActivity(intent);
    }
}
