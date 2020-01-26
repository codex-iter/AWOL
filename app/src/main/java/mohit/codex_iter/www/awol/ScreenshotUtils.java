package mohit.codex_iter.www.awol;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ScreenshotUtils {

    /*  Method which will return Bitmap after taking screenshot. We have to pass the view which we want to take screenshot.  */
    static Bitmap getScreenShot(View view) {
        RecyclerView recyclerView = (RecyclerView) view;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        int itemscount = 0;
        if (adapter != null) {
            itemscount = adapter.getItemCount();
        }
        int allitemsheight = 0;
        List<Bitmap> bmps = new ArrayList<>();

        for (int i = 0; i < itemscount; i++) {
            View childView = null;
            if (layoutManager != null) {
                childView = layoutManager.getChildAt(i);
            }
            if (childView != null) {
                childView.measure(MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

                childView.layout(0, 0, childView.getMeasuredWidth(), childView.getMeasuredHeight());
                childView.setDrawingCacheEnabled(true);
                childView.buildDrawingCache();
                bmps.add(childView.getDrawingCache());
                allitemsheight += childView.getMeasuredHeight();
            }
        }

        Bitmap bigbitmap = Bitmap.createBitmap(recyclerView.getMeasuredWidth(), allitemsheight, Bitmap.Config.ARGB_8888);
        Canvas bigcanvas = new Canvas(bigbitmap);

        Paint paint = new Paint();
        int iHeight = 0;

        for (int i = 0; i < bmps.size(); i++) {
            Bitmap mBitmap = bmps.get(i);
            bigcanvas.drawBitmap(mBitmap, 0, iHeight, paint);
            iHeight += mBitmap.getHeight();

            if (!mBitmap.isRecycled()) {
                mBitmap.recycle();
            }
        }


        return bigbitmap;
    }


    /*  Create Directory where screenshot will save for sharing screenshot  */
    public static File getMainDirectoryName(Context context) {
        //Here we will use getExternalFilesDir and inside that we will make our Demo folder
        //benefit of getExternalFilesDir is that whenever the app uninstalls the images will get deleted automatically.
        File mainDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo");

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : " + mainDir);
        }
        return mainDir;
    }

    /*  Store taken screenshot into above created path  */
    public static File store(Bitmap bm, String fileName, File saveFilePath) {
        File dir = new File(saveFilePath.getAbsolutePath());
        if (!dir.exists())
            if (!dir.mkdir()) {
                Log.e("msg", "Can't be created");
            } else {
                Log.e("msg", "Created");
            }
        File file = new File(saveFilePath.getAbsolutePath(), fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
}