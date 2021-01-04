package codex.codex_iter.www.awol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.Attendance;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.myViewHolder> {

    private Context ctx;
    private List<Attendance> dataList;
    private int pre_minimum_attendance;

    public AttendanceAdapter(Context context, List<Attendance> dataList, int pref_minimum_attendance) {
        this.ctx = context;
        this.dataList = dataList;
        this.pre_minimum_attendance = pref_minimum_attendance;
    }

    @NonNull
    @Override
    public AttendanceAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.item_attendance, parent, false);
        return new myViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.myViewHolder holder, int position) {
        SharedPreferences theme = ctx.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        holder.sub.setText(dataList.get(position).getSub());
        String p = dataList.get(position).getPercent();

        double percent = 0;
        if (p != null) {
            percent = Double.parseDouble(p);
        }

        if (percent >= pre_minimum_attendance + 10) {
            holder.ta.setBackgroundColor(Color.parseColor("#0BBE62"));
        } else if (percent >= pre_minimum_attendance) {
            holder.ta.setBackgroundColor(Color.parseColor("#FFD600"));
        } else {
            holder.ta.setBackgroundColor(Color.parseColor("#FF5252"));
        }
        holder.ta.setText(dataList.get(position).getPercent() + "%");
        String s = dataList.get(position).getOld();
        if (!s.equals("")) {
            double n = Double.parseDouble(dataList.get(position).getPercent());
            double o = Double.parseDouble(s);
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        if (!s.equals("")) {
            double n = Double.parseDouble(dataList.get(position).getPercent());
            double o = Double.parseDouble(s);
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        if (dataList.get(position).getUpd().toLowerCase().contains("yesterday")) {
            holder.lastUpdated.setText("Updated " + dataList.get(position).getUpd().toLowerCase());
        } else {
            if (dataList.get(position).getUpd().contains("ago") || dataList.get(position).getUpd().contains("just now")) {
                holder.lastUpdated.setText("Updated " + dataList.get(position).getUpd());
            } else {
                holder.lastUpdated.setText("Updated on " + dataList.get(position).getUpd());
            }
        }

        holder.th.setText(dataList.get(position).getTheory() + dataList.get(position).getThat());
        holder.prac.setText(dataList.get(position).getLab() + dataList.get(position).getLabt());
        holder.ab.setText(dataList.get(position).getAbsent());
        holder.tc.setText(dataList.get(position).getClasses());
        if (dataList.get(position).getBunk_text_str() != null && !dataList.get(position).getBunk_text_str().isEmpty()) {
            holder.bunk_text.setText(dataList.get(position).getBunk_text_str());
        } else {
            holder.bunk_text.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView sub;
        MaterialTextView th;
        MaterialTextView prac;
        MaterialTextView ab;
        MaterialTextView tc;
        MaterialTextView theory;
        MaterialTextView pract;
        MaterialTextView classes;
        MaterialTextView absents;
        MaterialTextView bunk_text;
        MaterialTextView lastUpdated;
        MaterialButton ta;
        ImageView up;
        MaterialCardView cardView;

        public myViewHolder(@NonNull View view) {
            super(view);
            theory = view.findViewById(R.id.theory_t);
            pract = view.findViewById(R.id.practicle);
            classes = view.findViewById(R.id.classes);
            absents = view.findViewById(R.id.absents);
            cardView = view.findViewById(R.id.card_view);
            sub = view.findViewById(R.id.sub);
            th = view.findViewById(R.id.theory);
            prac = view.findViewById(R.id.prac);
            ab = view.findViewById(R.id.ab);
            tc = view.findViewById(R.id.tc);
            ta = view.findViewById(R.id.ta);
            bunk_text = view.findViewById(R.id.bunk_text);
            up = view.findViewById(R.id.up);
            lastUpdated = view.findViewById(R.id.lastUpdated);
        }
    }
}
