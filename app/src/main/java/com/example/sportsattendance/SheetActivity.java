package com.example.sportsattendance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SheetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    private ListView sheetList;
    private ArrayAdapter arrayAdapter;

    private ArrayList<String> listItems = new ArrayList();

    private long cid;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        setToolbar();
        cid = getIntent().getLongExtra("cid",-1);
        loadListItem();
        sheetList = findViewById(R.id.sheet_list);
        arrayAdapter = new ArrayAdapter(this,R.layout.sheet_list,R.id.date_list_item,listItems);
        sheetList.setAdapter(arrayAdapter);
        sheetList.setOnItemClickListener(this);

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

    private void loadListItem(){
        Cursor cursor = new DbHelper(this).getDistinctMonths(cid);
        String date = "";
        while (cursor.moveToNext()) {
            date = cursor.getString(cursor.getColumnIndex(DbHelper.DATE_KEY));
            listItems.add(date.substring(3));
        }



    }





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long[] id_Array = getIntent().getLongArrayExtra("idArray");
        int[] rollArray =  getIntent().getIntArrayExtra("rollArray");
        String[] nameArray =  getIntent().getStringArrayExtra("nameArray");
        Intent intent = new Intent(this, SheetListActivity.class);
        intent.putExtra("idArray", id_Array);
        intent.putExtra("rollArray", rollArray);
        intent.putExtra("nameArray", nameArray);
        intent.putExtra("month",listItems.get(position));
        startActivity(intent);
    }
}