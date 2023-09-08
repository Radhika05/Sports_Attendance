package com.example.sportsattendance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PdfGenerator {
    private static String TAG = PdfGenerator.class.getSimpleName();
    private File mFile;
    private Context mContext;

    public PdfGenerator(Context context) {
        this.mContext = context;
    }


    /*save image to pdf*/
    public String saveImageToPDF(Bitmap scrollBitMap, Bitmap bitmap) {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS);
        if (!path.exists()) {
            path.mkdirs();
        }
        try {
            String baseFileName = "document";

            // Get the current time in milliseconds
            long currentTimeMillis = System.currentTimeMillis();

            // Create a SimpleDateFormat to format the time as a string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

            // Format the time value as a string
            String formattedTime = sdf.format(new Date(currentTimeMillis));

            // Combine the base file name and formatted time to create a unique file name
            String uniqueFileName = baseFileName + "_" + formattedTime + ".pdf";

            PdfDocument document = new PdfDocument();
            mFile = new File(path + "/", uniqueFileName + ".pdf");
            if (!mFile.exists()) {

                PdfDocument.PageInfo pageInfo1 = new PdfDocument.PageInfo.Builder(scrollBitMap.getWidth(), scrollBitMap.getHeight(), 1).create();
                PdfDocument.Page page1 = document.startPage(pageInfo1);
                Canvas canvas1 = page1.getCanvas();
                canvas1.drawColor(Color.WHITE);
                canvas1.drawBitmap(scrollBitMap, 0, 0, null);
                document.finishPage(page1);

                // Create the second page
                PdfDocument.PageInfo pageInfo2 = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 2).create();
                PdfDocument.Page page2 = document.startPage(pageInfo2);
                Canvas canvas2 = page2.getCanvas();
                canvas2.drawColor(Color.WHITE);
                canvas2.drawBitmap(bitmap, 0, 0, null);
                document.finishPage(page2);


                try {
                    mFile.createNewFile();
                    OutputStream out = new FileOutputStream(mFile);
                    document.writeTo(out);
                    document.close();
                    out.close();
                    Log.e(TAG, "Pdf Saved at:" + mFile.getAbsolutePath());

                    Toast.makeText(mContext, "Pdf Saved at:" + mFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    return mFile.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /*method for generating bitmap from LinearLayout, RelativeLayout etc.*/
    public Bitmap getViewScreenShot(View view, int height, int width) {
        //Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bm = view.getDrawingCache();
        return bm;


    }


    /*method for generating bitmap from ScrollView, NestedScrollView*/
    public Bitmap getScrollViewScreenShot(ScrollView nestedScrollView) {

        int totalHeight = nestedScrollView.getChildAt(0).getHeight();
        int totalWidth = nestedScrollView.getChildAt(0).getWidth();
        return getBitmapFromView(nestedScrollView, totalHeight, totalWidth);
    }


    public Bitmap getBitmapFromView(View view, int totalHeight, int totalWidth) {

        Bitmap returnedBitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }
}