package mohit.codex_iter.www.awol.theme;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import mohit.codex_iter.www.awol.Constants;
import mohit.codex_iter.www.awol.R;

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
        preferences = Objects.requireNonNull(getActivity()).getSharedPreferences("theme", 0);
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
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                ThemeItem item = items.get(adapter.getSelectedPosition());

                editor.putInt(POSITION,adapter.getSelectedPosition());
                editor.putInt(THEME,item.getTheme());
                editor.putBoolean(PREF_DARK_THEME,item.isDark());
                editor.apply();
                if(getActivity()!=null)
                getActivity().recreate();


            }
        });

        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final BottomSheetDialog d = (BottomSheetDialog)dialog;

                FrameLayout bottomSheet = d.findViewById(R.id.design_bottom_sheet) ;
                Drawable drawable = bottomSheet.getContext().getResources().getDrawable(R.drawable.theme_picker_bg);
                int bgColor = isDark?bottomSheet.getResources().getColor(R.color.darkBackground):Color.WHITE;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    DrawableCompat.setTint(drawable, bgColor);

                } else {
                    drawable.mutate().setColorFilter(bgColor, PorterDuff.Mode.SRC_IN);
                }

                bottomSheet.setBackground(drawable);
                BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
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
    };

    public interface Callback
    {

    }
}
