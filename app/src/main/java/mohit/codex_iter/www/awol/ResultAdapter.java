package mohit.codex_iter.www.awol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {

    private Context ctx;
    private List<ResultData> resultData;
    private OnItemClickListener listener;

    public ResultAdapter (Context ctx, List<ResultData> resultData, OnItemClickListener listener) {
        this.ctx = ctx;
        this.resultData = resultData;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ResultAdapter.ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.results_item, parent, false);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultHolder holder, int position) {
        holder.semTextView.setText(String.valueOf(resultData.get(position).getStynumber()));
//        holder.semTextView.setText("51");
        holder.creditsTextView.setText(resultData.get(position).getTotalearnedcredit());
        holder.sgpaTextView.setText(resultData.get(position).getSgpaR());

        holder.recyclerResultParent.setOnClickListener(view -> {
            if (listener != null) {
                listener.onResultClicked(resultData.get(position).getStynumber(), resultData.get(position).getTotalearnedcredit(),
                        resultData.get(position).getFail(), resultData.get(position).getSgpaR());
            }
        });
    }

    @Override
    public int getItemCount() {
        return resultData.size();
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

//        CardView cardView;
        TextView semTextView, sgpaTextView, creditsTextView;
        LinearLayout recyclerResultParent;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            recyclerResultParent = itemView.findViewById(R.id.recyclerResultParent);
//            cardView = itemView.findViewById(R.id.card_view);
            semTextView = itemView.findViewById(R.id.textViewSem);
            creditsTextView = itemView.findViewById(R.id.textViewCredits);
            sgpaTextView = itemView.findViewById(R.id.textViewSGPA);
        }
    }
    public interface OnItemClickListener {
        void onResultClicked(int sem, String totalCredits, String status, String sgpa);
    }

}