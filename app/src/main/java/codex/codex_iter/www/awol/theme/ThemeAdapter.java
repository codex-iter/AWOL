package codex.codex_iter.www.awol.theme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import codex.codex_iter.www.awol.R;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

List<ThemeItem> themes;
int selectedPosition;
    private boolean isBackgroundDark;

    public ThemeAdapter(List<ThemeItem> themes, int selectedPosition, boolean isBackgroundDark) {
        this.themes = themes;
        this.selectedPosition = selectedPosition;
        this.isBackgroundDark = isBackgroundDark;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item_layout, parent, false);
        return new ThemeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ThemeViewHolder holder, final int position) {
        if(position==selectedPosition)
        {
            holder.selector.setVisibility(View.VISIBLE);

        }
        else
        {
            holder.selector.setVisibility(View.GONE);
        }
        ThemeItem item = themes.get(position);
        holder.preview.setColor(item.getMainColor(), item.isDark(),isBackgroundDark);
        holder.itemView.setOnClickListener(view -> {
            int pos = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(pos);
            holder.selector.setVisibility(View.VISIBLE);
        });
    }

    int getSelectedPosition()
    {
        return selectedPosition;
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

    public static class ThemeViewHolder extends RecyclerView.ViewHolder{
        ThemeDrawable preview;
        ImageView selector;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            selector = itemView.findViewById(R.id.checked);
        }
    }
}
