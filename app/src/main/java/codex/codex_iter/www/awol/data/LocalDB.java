package codex.codex_iter.www.awol.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import codex.codex_iter.www.awol.model.Student;

public class LocalDB {
    private SharedPreferences sharedPreferences;
    private Gson gson;

    public LocalDB(Context context) {
        this.sharedPreferences = context.getSharedPreferences("students", Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    @Nullable
    public Student getStudent(@Nullable String username) {
        try {
            return gson.fromJson(this.sharedPreferences.getString(username, null), Student.class);
        } catch (Exception e) {
            Log.d("LocalDB", e.toString());
            return null;
        }
    }

    @NonNull
    public ArrayList<Student> getStudents() {
        ArrayList<Student> students = new ArrayList<>();
        for (Map.Entry<String, ?> entry : this.sharedPreferences.getAll().entrySet()) {
            Student student = getStudent(entry.getKey());
            if (student != null) {
                students.add(student);
            }
        }

        return students;
    }

    public void setStudent(@Nullable String username, @Nullable Student student) {
        if (student == null) {
            this.sharedPreferences.edit().remove(username).apply();
        } else {
            Log.d("LocalDB", gson.toJson(student));
            this.sharedPreferences.edit().putString(username, gson.toJson(student)).apply();
        }
    }
}

