package codex.codex_iter.www.awol.theme;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import codex.codex_iter.www.awol.R;

public class ThemeDrawable extends View {
    private int primaryColor;
    private int background;
    private boolean isDark = false;
    private boolean isBackgroundDark;
    Path path1, path2;
    Paint fill;
    int lightBackground, darkBackground;
    int strokeWidth;
    Rect border;

    public ThemeDrawable(Context context) {
        this(context,null);
    }

    public ThemeDrawable(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ThemeDrawable(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path1 = new Path();
        path2 = new Path();
        fill = new Paint();
        fill.setStyle(Paint.Style.FILL);
        lightBackground = getResources().getColor(R.color.lightBackground);
        darkBackground = getResources().getColor(R.color.darkBackground);
        border = new Rect(0,0,0,0);
        strokeWidth = Math.round(getResources().getDisplayMetrics().density*1);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(primaryColor);

       // canvas.drawRect(new Rect(0,0,canvas.getWidth(),canvas.getHeight()),fill);
        canvas.drawPath(path1,fill);
        fill.setColor(background);
        canvas.drawPath(path2,fill);
        fill.setStyle(Paint.Style.STROKE);
        fill.setStrokeWidth(strokeWidth);
        fill.setColor(isBackgroundDark?lightBackground:darkBackground);
        canvas.drawRect(border,fill);




    }

    public void setColor(int primaryColor, boolean isDark, boolean isBackgroundDark)
    {
      //  Log.d("SETCOLOR", "setColor: "+(primaryColor));
        this.primaryColor = getResources().getColor(primaryColor);
        this.background = isDark?darkBackground:lightBackground;
        this.isDark = isDark;
        this.isBackgroundDark = isBackgroundDark;
        invalidate();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d("WIDTH", "onSizeChanged: "+w+"     "+h);
        int threeFourthHeight = Math.round(h/2);
        int threeFourthWidth = Math.round(w/2);
        Log.d("WIDTH", "onSizeChanged: "+threeFourthHeight+"     "+threeFourthWidth);
        int halfStrokeWidth = Math.round(strokeWidth/2);
        border.left = halfStrokeWidth;
        border.top = halfStrokeWidth;
        border.right = w-halfStrokeWidth;
        border.bottom = h-halfStrokeWidth;
        path1.moveTo(0,0);
        path1.lineTo(w,0);
        path1.lineTo(w,threeFourthHeight);
        path1.lineTo(threeFourthWidth,h);
        path1.lineTo(0,h);
        path1.close();
        path2.moveTo(threeFourthWidth,h);
        path2.lineTo(w,h);
        path2.lineTo(w,threeFourthHeight);
        path2.close();
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
