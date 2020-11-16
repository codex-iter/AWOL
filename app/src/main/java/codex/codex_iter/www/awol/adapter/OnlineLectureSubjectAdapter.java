package codex.codex_iter.www.awol.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.Lecture;

public class OnlineLectureSubjectAdapter extends RecyclerView.Adapter<OnlineLectureSubjectAdapter.ViewHolder> {

    private ArrayList<Lecture> subjectArrayList;
    private Context mcontext;
    private OnItemClickListener onItemClickListener;
    private boolean fromSubject;

    public OnlineLectureSubjectAdapter(Context mcontext, ArrayList<Lecture> subjectArrayList, boolean fromSubject, OnItemClickListener onItemClickListener) {
        this.subjectArrayList = subjectArrayList;
        this.mcontext = mcontext;
        this.onItemClickListener = onItemClickListener;
        this.fromSubject = fromSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        View view = inflater.inflate(R.layout.item_lectures, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences theme = mcontext.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        if (dark) {
            holder.subject.setTextColor(Color.parseColor("#ffffff"));
            if (!fromSubject) {
                holder.subject.setText(subjectArrayList.get(position).getSubject());
                holder.icon.setImageResource(R.drawable.folder_dark);
            } else {
                holder.subject.setText(subjectArrayList.get(position).getName());
                holder.icon.setImageResource(R.drawable.file_outline_dark);
            }
        } else {
            if (!fromSubject) {
                holder.subject.setText(subjectArrayList.get(position).getSubject());
                holder.icon.setImageResource(R.drawable.folder_light);
            } else {
                holder.subject.setText(subjectArrayList.get(position).getName());
                holder.icon.setImageResource(R.drawable.file_outline_light);
            }
        }
        holder.cardView.setOnClickListener(view -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClicked(subjectArrayList.get(position).getSubject(), subjectArrayList.get(position).getLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView subject;
        MaterialCardView cardView;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            subject = itemView.findViewById(R.id.subject_name);
            icon = itemView.findViewById(R.id.icon);

        }
    }

    public interface OnItemClickListener {
        void onClicked(String subject_name, String video_link);
    }
}
