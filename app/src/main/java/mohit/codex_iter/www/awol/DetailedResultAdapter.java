package mohit.codex_iter.www.awol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DetailedResultAdapter extends RecyclerView.Adapter<DetailedResultAdapter.DetailViewHolder> {

    private Context ctx;
    private List<DetailResultData> detailResultData;

    public DetailedResultAdapter (Context ctx, List<DetailResultData>  detailResultData) {
        this.ctx = ctx;
        this.detailResultData = detailResultData;
    }

    @NonNull
    @Override
    public DetailedResultAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.detailedresult_item, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedResultAdapter.DetailViewHolder holder, int position) {
        holder.subject_name.setText(detailResultData.get(position).getSubjectdesc());
        holder.subject_grade.setText(detailResultData.get(position).getGrade());
    }

    @Override
    public int getItemCount() {
        return detailResultData.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView subject_name, subject_grade;
        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            subject_name = itemView.findViewById(R.id.subjectName);
            subject_grade = itemView.findViewById(R.id.subjectGrade);
        }
    }
}
