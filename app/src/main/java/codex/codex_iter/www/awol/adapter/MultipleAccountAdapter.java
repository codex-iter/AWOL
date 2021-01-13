package codex.codex_iter.www.awol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.model.Student;

import static codex.codex_iter.www.awol.utilities.Constants.convertToTitleCaseIteratingChars;

public class MultipleAccountAdapter extends RecyclerView.Adapter<MultipleAccountAdapter.ViewHolder> {

    private final Context ctx;
    private final List<Student> multipleAccountList;
    private final OnItemClickListener onItemClickListener;

    public MultipleAccountAdapter(Context ctx, List<Student> multipleAccountList, OnItemClickListener onItemClickListener) {
        this.ctx = ctx;
        this.multipleAccountList = multipleAccountList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MultipleAccountAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.item_multiple_account, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultipleAccountAdapter.ViewHolder holder, int position) {
        String student_name = multipleAccountList.get(position).getName();
        if (!student_name.equals("Add Account")) {
            String[] split = student_name.split("\\s+");
            try {
                if (!split[0].isEmpty()) {
                    holder.nameTextView.setText(convertToTitleCaseIteratingChars(split[0]));
                } else {
                    holder.nameTextView.setText(student_name);
                }
            } catch (Exception e) {
                holder.nameTextView.setText(student_name);
            }
        } else {
            holder.studentImage.setImageResource(R.drawable.account_multiple_plus_outline);
            holder.nameTextView.setText(student_name);
        }
        holder.idTextView.setText(multipleAccountList.get(position).getRedgNo());
        holder.materialItem.setOnClickListener(view -> {
            // switch to particular account
            if (onItemClickListener != null) {
                if (student_name.equals("Add Account")) {
                    onItemClickListener.addAccountClicked();
                } else {
                    onItemClickListener.switchToClickedAccount(multipleAccountList.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return multipleAccountList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialTextView nameTextView, idTextView;
        ConstraintLayout materialItem;
        ImageView studentImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            studentImage = itemView.findViewById(R.id.accountImage);
            nameTextView = itemView.findViewById(R.id.accountName);
            materialItem = itemView.findViewById(R.id.accountItem);
            idTextView = itemView.findViewById(R.id.accountId);
        }
    }

    public interface OnItemClickListener {
        void switchToClickedAccount(Student student);

        void addAccountClicked();
    }
}
