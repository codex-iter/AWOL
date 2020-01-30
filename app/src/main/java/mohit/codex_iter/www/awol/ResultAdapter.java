package mohit.codex_iter.www.awol;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultHolder> {

    private Context ctx;
    private List<ResultData> resultData;
    private OnItemClickListener listener;

    public ResultAdapter(Context ctx, List<ResultData> resultData, OnItemClickListener listener) {
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
        SharedPreferences theme = ctx.getSharedPreferences("theme", 0);
        boolean dark = theme.getBoolean("dark_theme", false);

        holder.semTextView.setText(String.valueOf(resultData.get(position).getStynumber()));
        holder.creditsTextView.setText(resultData.get(position).getTotalearnedcredit());
        holder.sgpaTextView.setText(resultData.get(position).getSgpaR());

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
        holder.recyclerResultParent.setOnClickListener(view -> {
            if (listener != null) {
                listener.onResultClicked(resultData.get(position).getStynumber(), resultData.get(position).getTotalearnedcredit(),
                        resultData.get(position).getFail(), resultData.get(position).getSgpaR());
            }
        });

        if (!dark) {
            holder.semTextView.setTextColor(Color.parseColor("#141831"));
            holder.sgpaTextView.setTextColor(Color.parseColor("#141831"));
            ;
            holder.creditsTextView.setTextColor(Color.parseColor("#141831"));
        }
    }

    @Override
    public int getItemCount() {
        return resultData.size();
    }

    public class ResultHolder extends RecyclerView.ViewHolder {

        //        CardView cardView;
        TextView semTextView, sgpaTextView, creditsTextView;
        LinearLayout recyclerResultParent;
        ImageView imageViewResultEmotion;

        public ResultHolder(@NonNull View itemView) {
            super(itemView);
            recyclerResultParent = itemView.findViewById(R.id.recyclerResultParent);
//            cardView = itemView.findViewById(R.id.recyclerResultParent);
            semTextView = itemView.findViewById(R.id.textViewSem);
            creditsTextView = itemView.findViewById(R.id.textViewCredits);
            sgpaTextView = itemView.findViewById(R.id.textViewSGPA);
            imageViewResultEmotion = itemView.findViewById(R.id.imageViewResultEmotion);
        }
    }

    public interface OnItemClickListener {
        void onResultClicked(int sem, String totalCredits, String status, String sgpa);
    }

}
