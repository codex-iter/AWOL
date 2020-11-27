package codex.codex_iter.www.awol.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.Result;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {

    private Context ctx;
    private List<Result> resultData;
    private OnItemClickListener listener;

    public ResultAdapter(Context ctx, List<Result> resultData, OnItemClickListener listener) {
        this.ctx = ctx;
        this.resultData = resultData;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultAdapter.ResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.item_results, parent, false);
        return new ResultHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ResultHolder holder, int position) {
        SharedPreferences theme = ctx.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        holder.semTextView.setText(String.valueOf(resultData.get(position).getStynumber()));
        holder.creditsTextView.setText(resultData.get(position).getTotalearnedcredit());
        holder.sgpaTextView.setText(resultData.get(position).getSgpaR());
        holder.cgpaTextView.setText(resultData.get(position).getCgpaR());

        double currSGPA = Double.parseDouble(resultData.get(position).getSgpaR());
        if (currSGPA >= 8.5 && currSGPA <= 10.0) {
            //excellent
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_excellent);
        } else if (currSGPA >= 7.0 && currSGPA < 8.5) {
            //good
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_good);
        } else if (currSGPA >= 5.0 && currSGPA < 7.0) {
            //average
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_average);
        } else {
            //poor
            holder.imageViewResultEmotion.setImageResource(R.drawable.ic_poor);
        }
        holder.cardView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onResultClicked(resultData.get(position).getStynumber(), resultData.get(position).getTotalearnedcredit(),
                        resultData.get(position).getFail(), resultData.get(position).getSgpaR());
            }
        });

        if (!dark) {
            holder.semTextView.setTextColor(Color.parseColor("#141831"));
            holder.sgpaTextView.setTextColor(Color.parseColor("#141831"));
            holder.cgpaTextView.setTextColor(Color.parseColor("#141831"));
            holder.creditsTextView.setTextColor(Color.parseColor("#141831"));
        } else {
            holder.semTextView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.sgpaTextView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.cgpaTextView.setTextColor(Color.parseColor("#FFFFFF"));
            holder.creditsTextView.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return resultData.size();
    }

    public static class ResultHolder extends RecyclerView.ViewHolder {
        MaterialTextView semTextView, sgpaTextView, creditsTextView, cgpaTextView;
        LinearLayout recyclerResultParent;
        ImageView imageViewResultEmotion;
        MaterialCardView cardView;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            recyclerResultParent = itemView.findViewById(R.id.recyclerResultParent);
            semTextView = itemView.findViewById(R.id.textViewSem);
            creditsTextView = itemView.findViewById(R.id.textViewCredits);
            sgpaTextView = itemView.findViewById(R.id.textViewSGPA);
            cgpaTextView = itemView.findViewById(R.id.textViewCGPA);
            imageViewResultEmotion = itemView.findViewById(R.id.imageViewResultEmotion);
        }
    }

    public interface OnItemClickListener {
        void onResultClicked(int sem, String totalCredits, String status, String sgpa);
    }
}
