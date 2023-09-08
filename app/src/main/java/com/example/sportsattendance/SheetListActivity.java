package com.example.sportsattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SheetListActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout scrollView;

    BarChart barChart;

    BarData barData;

    BarDataSet barDataSet;

    ArrayList barEntriesArrayList;
    ArrayList<String> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_list);
        scrollView = findViewById(R.id.main_linear);
        barChart = findViewById(R.id.idBarChart);
        barEntriesArrayList = new ArrayList();
        entries = new ArrayList<>();
        entries.add("0");

        setToolbar();
        showTable();

        toolbar.inflateMenu(R.menu.print_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));
    }

    private void setChartData() {


        barDataSet = new BarDataSet(barEntriesArrayList, "Student Attendace System");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        // setting text color.
        barDataSet.setValueTextColor(Color.BLACK);

        // setting text size
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);


        xAxis.setGranularity(1f); // Ensure one label per bar
        // xAxis.setValueFormatter(new IndexAxisValueFormatter() {
        xAxis.setValueFormatter(new

                IndexAxisValueFormatter(entries));

        xAxis.setLabelCount(entries.size());
        barChart.invalidate();


    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                captureScreenAndGeneratePDF();
            } else {
                requestPermission();
            }
        }
    }

    private void captureScreenAndGeneratePDF() {
        try {
            // Capture the screen as a Bitmap
            View rootView = getWindow().getDecorView().getRootView();
            rootView.setDrawingCacheEnabled(true);
            rootView.buildDrawingCache();
            Bitmap screenBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
            rootView.setDrawingCacheEnabled(false);

            // Create a PDF document
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    screenBitmap.getWidth(),
                    screenBitmap.getHeight(),
                    1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(screenBitmap, 0, 0, null);
            document.finishPage(page);

            // Save the PDF to external storage
            File pdfFile = new File(Environment.getExternalStorageDirectory(), "screenshot.pdf");
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();

            // Notify the user that the PDF has been created
            // You can also save the PDF file path or open it here
            // For this example, we simply print a message
            System.out.println("Screen captured and saved as " + pdfFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void convertToPdf() {
        PdfGenerator pdfGenerator = new PdfGenerator(this);


        HorizontalScrollView scrollView = findViewById(R.id.horizontal);
        LinearLayout constraintLayout = findViewById(R.id.main_linear);
        LinearLayout barLinearLayout = findViewById(R.id.ll_bar);
        int totalWidth = scrollView.getChildAt(0).getWidth();

        // Create a bitmap that can hold the entire content
        Bitmap scrollBitmap = Bitmap.createBitmap(totalWidth, constraintLayout.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap chatBitMap = Bitmap.createBitmap(totalWidth, barLinearLayout.getHeight(), Bitmap.Config.ARGB_8888);

        // Create a canvas to draw the bitmap
        Canvas canvas = new Canvas(scrollBitmap);
        Canvas canvas1 = new Canvas(chatBitMap);
        barLinearLayout.draw(canvas1);

        // Iterate through the scrollable content and capture it in sections
        int xOffset = 0;
        while (xOffset < totalWidth) {
            scrollView.scrollTo(xOffset, 0);
            scrollView.draw(canvas);
            xOffset += scrollView.getWidth();
        }


        String filePath = pdfGenerator.saveImageToPDF(scrollBitmap, chatBitMap);
        if (!filePath.isEmpty() && !filePath.equals("")) {
            File pdfFile = new File(filePath);  // -> filename = manual.pdf

            Uri excelPath;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                excelPath = FileProvider.getUriForFile(this, "com.example.sportsattendance.fileprovider", pdfFile);
            } else {
                excelPath = Uri.fromFile(pdfFile);
            }


            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(excelPath, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                // Instruct the user to install a PDF reader here, or something
            }
        }
    }

    private boolean checkPermissionGranted() {
        if ((ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED)) {
            // Permission has already been granted
            return true;
        } else {
            return false;
        }
    }

    private boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.print_report) {
            if (checkPermissionGranted()) {
                convertToPdf();
            } else {
                requestPermission();
            }
        }
        return true;
    }

    private void showTable() {
        DbHelper dbHelper = new DbHelper(this);
        TableLayout tableLayout = findViewById(R.id.tableLayout);
        long[] id_Array = getIntent().getLongArrayExtra("idArray");
        int[] rollArray = getIntent().getIntArrayExtra("rollArray");
        String[] nameArray = getIntent().getStringArrayExtra("nameArray");
        String month = getIntent().getStringExtra("month");

        int DAY_IN_MONTH = getDayInMonth(month);

        int rowSize = id_Array.length + 1;
        TableRow[] tableRow = new TableRow[rowSize];
        TextView[] tv_rolls = new TextView[rowSize];
        TextView[] tv_name = new TextView[rowSize];
        TextView[] tv_count = new TextView[rowSize];
        TextView[] tv_totalDays = new TextView[rowSize];
        TextView[][] tv_status = new TextView[rowSize][DAY_IN_MONTH + 1];


        for (int i = 0; i < rowSize; i++) {
            tv_rolls[i] = new TextView(this);
            tv_name[i] = new TextView(this);
            tv_count[i] = new TextView(this);
            tv_totalDays[i] = new TextView(this);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                tv_status[i][j] = new TextView(this);

            }
        }

        tv_rolls[0].setText("Roll");
        tv_rolls[0].setTypeface(tv_rolls[0].getTypeface(), Typeface.BOLD);
        tv_name[0].setText("Name");
        tv_name[0].setTypeface(tv_rolls[0].getTypeface(), Typeface.BOLD);

        tv_count[0].setText("Present Days");
        tv_count[0].setTypeface(tv_count[0].getTypeface(), Typeface.BOLD);

        tv_totalDays[0].setText("Attendance %");
        tv_totalDays[0].setTypeface(tv_count[0].getTypeface(), Typeface.BOLD);


        for (int i = 1; i <= DAY_IN_MONTH; i++) {
            tv_status[0][i].setText(String.valueOf(i));
            tv_status[0][i].setTypeface(tv_status[0][i].getTypeface(), Typeface.BOLD);
        }

        for (int i = 1; i < rowSize; i++) {
            tv_rolls[i].setText(String.valueOf(rollArray[i - 1]));
            tv_name[i].setText(String.valueOf(nameArray[i - 1]));
            entries.add(String.valueOf(nameArray[i - 1]));

            String dayPresent = String.valueOf(1);
            if (dayPresent.length() == 1) dayPresent = "0" + dayPresent;
            int presentCount = 0;
            String datePresent = dayPresent + "." + month;
            presentCount = dbHelper.getNumberOfDaysStudentPresentInSchool(String.valueOf(id_Array[i - 1]), datePresent);
            //tv_count[i].setText(String.valueOf(presentCount));


            int totalDays = dbHelper.getStatusWithPresent(id_Array[i - 1], datePresent);


            double percentageAttended = (double) presentCount / totalDays * 100;

            System.out.println("Percentage Attended: " + percentageAttended + "%");

            barEntriesArrayList.add(new BarEntry((float) i, (float) percentageAttended));
            tv_totalDays[i].setText(String.valueOf((int) percentageAttended));


            String status = "";

            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                String day = String.valueOf(j);
                if (day.length() == 1) day = "0" + day;

                String date = day + "." + month;
                status = dbHelper.getStatus(id_Array[i - 1], date);


                tv_status[i][j].setText(status);

            }

        }

        for (int i = 0; i < rowSize; i++) {
            tableRow[i] = new TableRow(this);
            if (i % 2 == 0) {
                tableRow[i].setBackgroundColor(Color.parseColor("#EEEEEE"));
            } else {
                tableRow[i].setBackgroundColor(Color.parseColor("#E4E4E4"));
            }
            tv_rolls[i].setPadding(16, 16, 16, 16);
            tv_name[i].setPadding(16, 16, 16, 16);
            tv_totalDays[i].setPadding(16, 16, 16, 16);
            tableRow[i].addView(tv_name[i]);
            tableRow[i].addView(tv_rolls[i]);
            //tableRow[i].addView(tv_count[i]);
            tableRow[i].addView(tv_totalDays[i]);
            for (int j = 1; j <= DAY_IN_MONTH; j++) {
                tv_status[i][j].setPadding(16, 16, 16, 16);
                tableRow[i].addView(tv_status[i][j]);


            }
            tableLayout.addView(tableRow[i]);
        }
        tableLayout.setShowDividers(TableLayout.SHOW_DIVIDER_MIDDLE);
        setChartData();

    }


    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        title.setText("Sports Attendance Sheet");
        subtitle.setVisibility(View.GONE);
        back.setVisibility(View.INVISIBLE);
        save.setVisibility(View.INVISIBLE);
    }


    private int getDayInMonth(String month) {
        int monthIndex = Integer.parseInt(month.substring(0, 2)) - 1;
        int year = Integer.parseInt(month.substring(3));

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, monthIndex);
        calendar.set(Calendar.YEAR, year);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


}