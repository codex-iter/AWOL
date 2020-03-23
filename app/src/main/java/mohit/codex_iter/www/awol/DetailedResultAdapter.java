package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
        SharedPreferences theme = ctx.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        holder.textViewSubName.setText(detailResultData.get(position).getSubjectdesc());
        holder.textViewSubCode.setText(detailResultData.get(position).getSubjectcode());
        holder.textViewGrade.setText(detailResultData.get(position).getGrade());
        holder.textViewIndvCredits.setText(detailResultData.get(position).getEarnedcredit());

        if (!dark) {
            holder.textViewSubCode.setTextColor(Color.parseColor("#141831"));
            holder.textViewGrade.setTextColor(Color.parseColor("#141831"));;
            holder.textViewIndvCredits.setTextColor(Color.parseColor("#141831"));
            holder.textViewSubName.setTextColor(Color.parseColor("#141831"));
        }
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
