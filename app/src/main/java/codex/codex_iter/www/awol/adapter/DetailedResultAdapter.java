package codex.codex_iter.www.awol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.DetailResult;
import codex.codex_iter.www.awol.utilities.Constants;

public class DetailedResultAdapter extends RecyclerView.Adapter<DetailedResultAdapter.DetailViewHolder> {

    private final Context ctx;
    private final List<DetailResult> detailResultData;

    public DetailedResultAdapter (Context ctx, List<DetailResult>  detailResultData) {
        this.ctx = ctx;
        this.detailResultData = detailResultData;
    }

    @NonNull
    @Override
    public DetailedResultAdapter.DetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.item_detailresults, parent, false);
        return new DetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailedResultAdapter.DetailViewHolder holder, int position) {
        holder.textViewSubName.setText(Constants.convertToTitleCaseIteratingChars(detailResultData.get(position).getSubjectdesc()));
        holder.textViewSubCode.setText(detailResultData.get(position).getSubjectcode());
        holder.textViewGrade.setText(detailResultData.get(position).getGrade());
        holder.textViewIndvCredits.setText(detailResultData.get(position).getEarnedcredit());
    }

    @Override
    public int getItemCount() {
        return detailResultData.size();
    }

    public static class DetailViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView textViewSubName, textViewSubCode, textViewGrade, textViewIndvCredits;
        public DetailViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSubName = itemView.findViewById(R.id.textViewSubName);
            textViewSubCode = itemView.findViewById(R.id.textViewSubCode);
            textViewGrade = itemView.findViewById(R.id.textViewGrade);
            textViewIndvCredits = itemView.findViewById(R.id.textViewIndvCredits);
        }
    }
}
