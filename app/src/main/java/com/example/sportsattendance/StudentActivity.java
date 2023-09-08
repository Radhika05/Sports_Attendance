package com.example.sportsattendance;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    private String className;
    private String subjectName;
    private int position;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<StudentItem> studentItems = new ArrayList<>();

    private long cid;

    private MyCalendar myCalendar;

    private TextView subtitle;

    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        myCalendar = new MyCalendar();
        Intent intent = getIntent();
        className = intent.getStringExtra("className");
        subjectName = intent.getStringExtra("subjectName");
        position = intent.getIntExtra("position", -1);
        cid = intent.getLongExtra("cid", -1);
        dbHelper = new DbHelper(this);
        setToolbar();

        recyclerView = findViewById(R.id.student_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new StudentAdapter(this, studentItems);
        recyclerView.setAdapter(adapter);
        loadData();
        loadStatus();
        adapter.setOnItemClickListener(position -> changeStatus(position));
        EditText editText = findViewById(R.id.search_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = s.toString().toLowerCase(Locale.getDefault());
                filterWithQuery(query);


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private ArrayList<StudentItem> onQueryChanged(String filterQuery) {
        // Create a new empty array list to store the filtered results.
        ArrayList<StudentItem> filteredList = new ArrayList<>();

        // Iterate through the original list of sports.
        for (StudentItem currentSport : studentItems) {
            // Convert the current sport's title to lowercase.
            String lowerCaseTitle = currentSport.getName().toLowerCase(Locale.getDefault());

            // Check if the current sport's title contains the filter query.
            if (lowerCaseTitle.contains(filterQuery)) {
                // If it does, add the current sport to the filtered list.
                filteredList.add(currentSport);
            }
        }

        // Return the filtered list.
        return filteredList;
    }

    private void filterWithQuery(String query) {
        // If the query is not empty, perform the filtering operation.
        if (!query.isEmpty()) {
            // Get the filtered list of sports.
            ArrayList<StudentItem> filteredList = onQueryChanged(query);

            // Attach the adapter to the RecyclerView with the filtered list.

            adapter.filterList(filteredList);


            // If the filtered list is empty, hide the RecyclerView and show an error message.
           /* if (filteredList.isEmpty()) {
                toggleRecyclerView(filteredList);
            }
        } else {
            // If the query is empty, attach the adapter to the RecyclerView with the original list of sports.
            attachAdapter(sportsList);
        }*/
        } else {
            adapter.filterList(studentItems);
        }

    }

    private void loadData() {
        Cursor cursor = dbHelper.getStudentTable(cid);
        studentItems.clear();
        while (cursor.moveToNext()) {
            long cid = cursor.getLong(cursor.getColumnIndex(DbHelper.C_ID));
            long sid = cursor.getLong(cursor.getColumnIndex(DbHelper.S_ID));
            int rollnumber = cursor.getInt(cursor.getColumnIndex(DbHelper.STUDENT_ROLL_KEY));
            String name = cursor.getString(cursor.getColumnIndex(DbHelper.STUDENT_NAME_KEY));
            studentItems.add(new StudentItem(sid, cid, rollnumber, name));
        }
    }

    private void changeStatus(int position) {
        String status = studentItems.get(position).getStatus();

        if (status.equals("P")) {
            status = "A";
        } else if (status.equals("A")) {
            status = "L";
        } else {
            status = "P";
        }


        studentItems.get(position).setStatus(status);
        adapter.notifyItemChanged(position);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title_toolbar);
        subtitle = toolbar.findViewById(R.id.subtitle_toolbar);
        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);
        save.setOnClickListener(this);
        title.setText(className);
        subtitle.setText(subjectName + " | " + myCalendar.getData());

        back.setOnClickListener(v -> onBackPressed());
        toolbar.inflateMenu(R.menu.student_menu);
        toolbar.setOnMenuItemClickListener(menuItem -> onMenuItemClick(menuItem));


    }

    private boolean onMenuItemClick(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.add_student) {
            showAddStudentDialog();
        } else if (menuItem.getItemId() == R.id.show_calendar) {
            showCalendarDialog();
        } else if (menuItem.getItemId() == R.id.show_attendance) {
            openSheetList();
        } else if (menuItem.getItemId() == R.id.sort_by_name) {
            openSortByDialog("name");
        } else if (menuItem.getItemId() == R.id.sort_by_roll) {
            openSortByDialog("roll");
        }
        return true;
    }

    private void openSortByDialog(String id) {
        MyDialog dialog = new MyDialog();
        if (id.equals("name")) {
            dialog.show(getSupportFragmentManager(), MyDialog.Student_SORT_NAME_DIALOG);
        } else {
            dialog.show(getSupportFragmentManager(), MyDialog.Student_SORT_ROLL_DIALOG);
        }

        dialog.setListener((roll, name) -> sortStudent(roll, name));

    }

    private void sortStudent(String roll, String name) {
        if (roll.equals("")) {
            if (name.equals("Ascending")) {
                Collections.sort(studentItems, new AscendingCustomComparator(roll));
            } else if (name.equals("Descending")) {
                Collections.sort(studentItems, new DescendingCustomComparator(roll));
            }
        } else {
            if (name.equals("Ascending")) {
                Collections.sort(studentItems, new AscendingCustomComparator(roll));
            } else if (name.equals("Descending")) {
                Collections.sort(studentItems, new DescendingCustomComparator(roll));
            }
        }

        adapter.notifyDataSetChanged();

    }

    public class AscendingCustomComparator implements Comparator<StudentItem> {

        String RollOrName;

        public AscendingCustomComparator(String roll) {
            RollOrName = roll;
        }

        @Override
        public int compare(StudentItem o1, StudentItem o2) {
            if (RollOrName.equals("")) {
                return o1.getName().compareTo(o2.getName());
            } else {
                return ((Integer) o1.getRoll()).compareTo(o2.getRoll());
            }

        }
    }


    public class DescendingCustomComparator implements Comparator<StudentItem> {

        String RollOrName;

        public DescendingCustomComparator(String roll) {
            RollOrName = roll;
        }

        @Override
        public int compare(StudentItem o1, StudentItem o2) {
            if (RollOrName.equals("")) {
                return o2.getName().compareTo(o1.getName());
            } else {
                return ((Integer) o2.getRoll()).compareTo(o1.getRoll());
            }
        }
    }


    private void openSheetList() {
        //loadData();
        long[] id_Array = new long[studentItems.size()];
        for (int i = 0; i < id_Array.length; i++)
            id_Array[i] = studentItems.get(i).getSid();

        int[] rollArray = new int[studentItems.size()];
        for (int i = 0; i < rollArray.length; i++)
            rollArray[i] = studentItems.get(i).getRoll();

        String[] nameArray = new String[studentItems.size()];
        for (int i = 0; i < nameArray.length; i++)
            nameArray[i] = studentItems.get(i).getName();

        Intent intent = new Intent(this, SheetActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("idArray", id_Array);
        intent.putExtra("rollArray", rollArray);
        intent.putExtra("nameArray", nameArray);
        startActivity(intent);
    }

    private void showCalendarDialog() {
        myCalendar.show(getSupportFragmentManager(), "");
        myCalendar.setOnCalendarOkClickListener(this::onCalendarOkClickListener);
    }

    private void onCalendarOkClickListener(int year, int month, int day) {
        loadData();
        loadStatus();
        myCalendar.setData(year, month, day);
        subtitle.setText(subjectName + " | " + myCalendar.getData());
        adapter.notifyDataSetChanged();
    }


    private void showAddStudentDialog() {
        MyDialog dialog = new MyDialog();
        dialog.show(getSupportFragmentManager(), MyDialog.Student_ADD_DIALOG);
        dialog.setListener((roll, name) -> addStudent(roll, name));
    }

    private void addStudent(String roll, String name) {
        long sid = dbHelper.addStudent(cid, Integer.parseInt(roll), name);
        studentItems.add(new StudentItem(sid, cid, Integer.parseInt(roll), name));
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteClass(item.getGroupId());

        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int position) {
        MyDialog myDialog = new MyDialog(studentItems.get(position).getRoll(), studentItems.get(position).getName());
        myDialog.show(getSupportFragmentManager(), MyDialog.Student_UPDATE_DIALOG);
        myDialog.setListener((roll, name) -> updateStudent(position, name));
    }

    private void updateStudent(int position, String name) {
        dbHelper.updateStudent(studentItems.get(position).getSid(), name);
        studentItems.get(position).setName(name);
        adapter.notifyItemChanged(position);
    }


    private void deleteClass(int position) {
        dbHelper.deleteStudent(studentItems.get(position).getSid());
        studentItems.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            saveStatus();
        }
    }


    public void loadStatus() {
        for (StudentItem studentItem : studentItems) {
            String status = dbHelper.getStatus(studentItem.getSid(), myCalendar.getData());
            if (status != null) {
                studentItem.setStatus(status);
            }
            adapter.notifyDataSetChanged();

        }
    }

    private void saveStatus() {
        for (StudentItem studentItem : studentItems) {
            String status = studentItem.getStatus();

            long value = dbHelper.addStatus(studentItem.getSid(), studentItem.getCid(), myCalendar.getData(), status);

            if (value == -1) {
                Toast.makeText(this, "Attendance Updated", Toast.LENGTH_LONG).show();
                dbHelper.updateStatus(studentItem.getSid(), studentItem.getCid(), myCalendar.getData(), status);
            } else {
                Toast.makeText(this, "Attendance Marks", Toast.LENGTH_LONG).show();
            }
        }
    }
}