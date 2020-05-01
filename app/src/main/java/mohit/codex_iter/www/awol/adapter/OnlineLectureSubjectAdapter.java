package mohit.codex_iter.www.awol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mohit.codex_iter.www.awol.R;
import mohit.codex_iter.www.awol.model.Lecture;

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
        View view = inflater.inflate(R.layout.item_results, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!fromSubject) {
            holder.creditsTextView.setVisibility(View.GONE);
            holder.sgpaTextView.setVisibility(View.GONE);
            holder.subject.setText(subjectArrayList.get(position).getSubject());
        } else {
            holder.subject.setText(subjectArrayList.get(position).getName());
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
        TextView subject, sgpaTextView, creditsTextView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            subject = itemView.findViewById(R.id.textViewSem);
            creditsTextView = itemView.findViewById(R.id.textViewCredits);
            sgpaTextView = itemView.findViewById(R.id.textViewSGPA);

        }
    }

    public interface OnItemClickListener {
        void onClicked(String subject_name, String video_link);
    }
}
