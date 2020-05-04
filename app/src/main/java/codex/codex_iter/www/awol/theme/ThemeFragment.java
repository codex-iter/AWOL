package codex.codex_iter.www.awol.theme;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import codex.codex_iter.www.awol.R;
import codex.codex_iter.www.awol.utilities.Constants;

public class ThemeFragment extends BottomSheetDialogFragment {

    private static final String POSITION="position";
    private boolean isDark=false;
    private SharedPreferences preferences;
    private static final String PREF_DARK_THEME="dark_theme";
    private static final String THEME="theme_pref";
    private List<ThemeItem> items;
    public static ThemeFragment newInstance() {
        return new ThemeFragment();
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.theme_select_layout,container,false);
        items = Constants.getThemes();
        preferences = requireActivity().getSharedPreferences("theme", 0);
        isDark = preferences.getBoolean(PREF_DARK_THEME, false);
        ((TextView) view.findViewById(R.id.title)).setTextColor(isDark? Color.WHITE:Color.BLACK);
        RecyclerView recyclerView = view.findViewById(R.id.theme_list);
        final int selectedPosition;
        if(getActivity()!=null) selectedPosition = preferences.getInt(POSITION,0);
        else selectedPosition=0;
        final ThemeAdapter adapter = new ThemeAdapter(items,selectedPosition,isDark);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false));
        recyclerView.setAdapter(adapter);

        Button apply = view.findViewById(R.id.apply_btn);
        apply.setOnClickListener(view1 -> {
            SharedPreferences.Editor editor = preferences.edit();
            ThemeItem item = items.get(adapter.getSelectedPosition());

            editor.putInt(POSITION, adapter.getSelectedPosition());
            editor.putInt(THEME, item.getTheme());
            editor.putBoolean(PREF_DARK_THEME, item.isDark());
            editor.apply();
            if (getActivity() != null)
                getActivity().recreate();


        });

        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            final BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet);
            Drawable drawable = null;
            if (bottomSheet != null) {
                drawable = bottomSheet.getContext().getResources().getDrawable(R.drawable.theme_picker_bg);
            }
            int bgColor = 0;
            if (bottomSheet != null) {
                bgColor = isDark ? bottomSheet.getResources().getColor(R.color.darkBackground) : Color.WHITE;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (drawable != null) {
                    DrawableCompat.setTint(drawable, bgColor);
                }

            } else {
                if (drawable != null) {
                    drawable.mutate().setColorFilter(bgColor, PorterDuff.Mode.SRC_IN);
                }
            }

            if (bottomSheet != null) {
                bottomSheet.setBackground(drawable);
            }
            BottomSheetBehavior bottomSheetBehavior = null;
            if (bottomSheet != null) {
                bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            }
            if (bottomSheetBehavior != null) {
                bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int i) {
                        if (i == 5) {
                            d.cancel();
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View view, float v) {

                    }
                });
            }
        });
        return dialog;
    }

    public interface Callback
    {

    }
}
