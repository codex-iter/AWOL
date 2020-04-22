package mohit.codex_iter.www.awol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.model.AttendanceData;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.myViewHolder> {

    private Context ctx;
    private List<AttendanceData> datalist;

    public AttendanceAdapter(Context context, List<AttendanceData> datalist) {
        this.ctx = context;
        this.datalist = datalist;
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

        holder.sub.setText(datalist.get(position).getSub());
        String p = datalist.get(position).getPercent();

        double percent = 0;
        if (p != null) {
            percent = Double.parseDouble(p);
        }

        if (percent > (float) 80) {
            holder.ta.setBackgroundColor(Color.parseColor("#0BBE62"));
        } else if (percent >= (float) 60 && percent <= 80) {
            holder.ta.setBackgroundColor(Color.parseColor("#FFFF66"));
        } else {
            holder.ta.setBackgroundColor(Color.parseColor("#F5FC0101"));
        }
        holder.ta.setText(datalist.get(position).getPercent() + "%");
        String s = datalist.get(position).getOld();
        if (!s.equals("")) {
            double n = Double.parseDouble(datalist.get(position).getPercent());
            double o = Double.parseDouble(s);
            //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        if (!s.equals("")) {
            double n = Double.parseDouble(datalist.get(position).getPercent());
            double o = Double.parseDouble(s);
            //    Toast.makeText(context, "New Attendance : " + n, Toast.LENGTH_SHORT).show();
            if (n >= o) {
                holder.up.setBackgroundResource(R.drawable.up);
            } else {
                holder.up.setBackgroundResource(R.drawable.down);
            }
        }

        holder.lu.setText(datalist.get(position).getUpd());
        holder.th.setText(datalist.get(position).getTheory() + datalist.get(position).getThat());
        holder.prac.setText(datalist.get(position).getLab() + datalist.get(position).getLabt());
        holder.ab.setText(datalist.get(position).getAbsent());
        holder.tc.setText(datalist.get(position).getClasses());
        holder.bunk_text.setText(datalist.get(position).getBunk_text_str());

        if (!dark) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.ta.setTextColor(Color.parseColor("#141831"));
            holder.lu.setTextColor(Color.parseColor("#141831"));
            holder.th.setTextColor(Color.parseColor("#141831"));
            holder.prac.setTextColor(Color.parseColor("#141831"));
            holder.ab.setTextColor(Color.parseColor("#141831"));
            holder.tc.setTextColor(Color.parseColor("#141831"));
            // mViewHolder.total.setTextColor(Color.parseColor("#141831"));
            holder.updated.setTextColor(Color.parseColor("#141831"));
            holder.absents.setTextColor(Color.parseColor("#141831"));
            holder.pract.setTextColor(Color.parseColor("#141831"));
            holder.theory.setTextColor(Color.parseColor("#141831"));
            holder.classes.setTextColor(Color.parseColor("#141831"));
            holder.bunk_text.setTextColor(Color.parseColor("#141831"));
        }

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView sub, lu, th, prac, ab, tc, theory, updated, pract, classes, absents, bunk_text;
        Button ta;
        ImageView up;
        CardView cardView;

        public myViewHolder(@NonNull View view) {
            super(view);

//            total = view.findViewById(R.id.total);
            theory = view.findViewById(R.id.theory_t);
            updated = view.findViewById(R.id.updated);
            pract = view.findViewById(R.id.practicle);
            classes = view.findViewById(R.id.classes);
            absents = view.findViewById(R.id.absents);
            cardView = view.findViewById(R.id.card_view);
            sub = view.findViewById(R.id.sub);
            lu = view.findViewById(R.id.lu);
            th = view.findViewById(R.id.theory);
            prac = view.findViewById(R.id.prac);
            ab = view.findViewById(R.id.ab);
            tc = view.findViewById(R.id.tc);
            ta = view.findViewById(R.id.ta);
            bunk_text = view.findViewById(R.id.bunk_text);

//            tha=view.findViewById(R.id.tha);
//            la=view.findViewById(R.id.la);
            up = view.findViewById(R.id.up);
            //down=view.findViewById(R.id.down);

        }
    }
}
