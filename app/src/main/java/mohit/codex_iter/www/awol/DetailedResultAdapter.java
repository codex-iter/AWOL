package mohit.codex_iter.www.awol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.textViewSubName.setText(detailResultData.get(position).getSubjectdesc());
        holder.textViewSubCode.setText(detailResultData.get(position).getSubjectcode());
        holder.textViewGrade.setText(detailResultData.get(position).getGrade());
        holder.textViewIndvCredits.setText(detailResultData.get(position).getEarnedcredit());
        Toast.makeText(ctx, "Sub " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return detailResultData.size();
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder {

//        CardView cardView;
        TextView textViewSubName, textViewSubCode, textViewGrade, textViewIndvCredits;
        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);

//            cardView = itemView.findViewById(R.id.card_view);
            textViewSubName = itemView.findViewById(R.id.textViewSubName);
            textViewSubCode = itemView.findViewById(R.id.textViewSubCode);
            textViewGrade = itemView.findViewById(R.id.textViewGrade);
            textViewIndvCredits = itemView.findViewById(R.id.textViewIndvCredits);
        }
    }
}
