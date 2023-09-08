package com.example.sportsattendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 2;


    //class table
    private static final String CLASS_TABLE_NAME = "CLASS_TABLE";
    public static final String C_ID = "_CID";

    public static final String USER_ID = "_UID";


    public static final String CLASS_NAME_KEY = "CLASS_NAME";
    public static final String SUBJECT_NAME_KEY = "SUBJECT_NAME";

    private static final String TABLE_USER = "user";

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private static final String LOG_IN = "LOG_INN";
    private static final String CREATE_CLASS_TABLE =
            "CREATE TABLE " + CLASS_TABLE_NAME + "( " +
                    C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    USER_ID + " INTEGER NOT NULL, " +
                    CLASS_NAME_KEY + " TEXT NOT NULL, " +
                    SUBJECT_NAME_KEY + " TEXT NOT NULL, " +
                    "UNIQUE (" + CLASS_NAME_KEY + "," + SUBJECT_NAME_KEY + ")" +
                    ");";

    private static final String DROP_CLASS_TABLE = "DROP TABLE IF EXISTS " + CLASS_TABLE_NAME;
    private static final String SELECT_CLASS_TABLE = "SELECT * FROM " + CLASS_TABLE_NAME;

    //student table

    private static final String STUDENT_TABLE_NAME = "STUDENT_TABLE";
    public static final String S_ID = "_SID";
    public static final String STUDENT_NAME_KEY = "STUDENT_NAME";
    public static final String STUDENT_ROLL_KEY = "ROLL";

    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE " + STUDENT_TABLE_NAME +
                    "( " +
                    S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    C_ID + " INTEGER NOT NULL, " +
                    STUDENT_NAME_KEY + " TEXT NOT NULL, " +
                    STUDENT_ROLL_KEY + " INTEGER, " +
                    " FOREIGN KEY ( " + C_ID + ") REFERENCES " + CLASS_TABLE_NAME + "(" + C_ID + ")" +
                    ");";

    private static final String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE_NAME;
    private static final String SELECT_STUDENT_TABLE = "SELECT * FROM " + STUDENT_TABLE_NAME;


    //STATUS TABLE

    private static final String STATUS_TABLE_NAME = "STATUS_TABLE";
    public static final String STATUS_ID = "_STATUS_ID";
    public static final String DATE_KEY = "STATUS_DATE";
    public static final String STATUS_KEY = "STATUS";

    private static final String CREATE_STATUS_TABLE =
            "CREATE TABLE " + STATUS_TABLE_NAME +
                    "(" +
                    STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    S_ID + " INTEGER NOT NULL, " +
                    C_ID + " INTEGER NOT NULL, " +
                    DATE_KEY + " DATE NOT NULL, " +
                    STATUS_KEY + " TEXT NOT NULL, " +
                    " UNIQUE (" + S_ID + "," + DATE_KEY + ")," +
                    " FOREIGN KEY (" + S_ID + ") REFERENCES " + STUDENT_TABLE_NAME + "( " + S_ID + ")," +
                    " FOREIGN KEY (" + C_ID + ") REFERENCES " + CLASS_TABLE_NAME + "( " + C_ID + ")" +
                    ");";
    private static final String DROP_STATUS_TABLE = "DROP TABLE IF EXISTS " + STATUS_TABLE_NAME;
    private static final String SELECT_STATUS_TABLE = "SELECT * FROM " + STATUS_TABLE_NAME;

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT,"
            + LOG_IN + " BOOLEAN , "
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";
    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;


    public DbHelper(@Nullable Context context) {
        super(context, "Attendance.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_STATUS_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_CLASS_TABLE);
            db.execSQL(DROP_STUDENT_TABLE);
            db.execSQL(DROP_STATUS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    long addClass(String className, String subjectName, long userId) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME_KEY, className);
        values.put(SUBJECT_NAME_KEY, subjectName);
        values.put(USER_ID, userId);

        return database.insert(CLASS_TABLE_NAME, null, values);
    }


    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(LOG_IN, user.isLoggIN());
        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?";
        // selection argument
        String[] selectionArgs = {email};
        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        // db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    Cursor getClassTable(long UserId) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(CLASS_TABLE_NAME, null, USER_ID + "=?", new String[]{String.valueOf(UserId)}, null, null, null);
        // return database.rawQuery(SELECT_CLASS_TABLE,null);
    }

    int deleteClass(long cid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.delete(CLASS_TABLE_NAME, C_ID + "=?", new String[]{String.valueOf(cid)});
    }

    long updateClass(long cid, String className, String subjectName) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CLASS_NAME_KEY, className);
        values.put(SUBJECT_NAME_KEY, subjectName);

        return database.update(CLASS_TABLE_NAME, values, C_ID + "=?", new String[]{String.valueOf(cid)});
    }

    long addStudent(long cid, int roll, String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_ID, cid);
        values.put(STUDENT_ROLL_KEY, roll);
        values.put(STUDENT_NAME_KEY, name);
        return database.insert(STUDENT_TABLE_NAME, null, values);
    }

    Cursor getStudentTable(long cid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(STUDENT_TABLE_NAME, null, C_ID + "=?", new String[]{String.valueOf(cid)}, null, null, STUDENT_ROLL_KEY);
    }


    int deleteStudent(long sid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.delete(STUDENT_TABLE_NAME, S_ID + "=?", new String[]{String.valueOf(sid)});
    }

    long updateStudent(long sid, String name) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STUDENT_NAME_KEY, name);
        return database.update(STUDENT_TABLE_NAME, values, S_ID + "=?", new String[]{String.valueOf(sid)});
    }

    long addStatus(long sid, long cid, String date, String status) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(S_ID, sid);
        values.put(C_ID, cid);
        values.put(DATE_KEY, date);
        values.put(STATUS_KEY, status);
        return database.insert(STATUS_TABLE_NAME, null, values);
    }

    long updateStatus(long sid, long cid, String date, String status) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_KEY, status);
        values.put(S_ID, sid);
        values.put(C_ID, cid);
        String whereClause = DATE_KEY + "='" + date + "' AND " + S_ID + "=" + sid;
        return database.update(STATUS_TABLE_NAME, values, whereClause, null);
    }

    String getStatus(long sid, String date) {
        String status = null;
        SQLiteDatabase database = this.getReadableDatabase();
        String whereClause = DATE_KEY + "='" + date + "' AND " + S_ID + "=" + sid;
        Cursor cursor = database.query(STATUS_TABLE_NAME, null, whereClause, null, null, null, null);
        if (cursor.moveToFirst())
            status = cursor.getString(cursor.getColumnIndex(STATUS_KEY));
        return status;
    }

    int getStatusWithPresent(long sid, String date) {
        int dayDiff = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = dateFormat.format(new Date());

        try {
            // Convert the start and end date strings to Date objects
            Date startDate = dateFormat.parse(date);
            Date endDate = dateFormat.parse(currentDate);

            // Get the date in milliseconds for the start and end dates
            SimpleDateFormat sqliteDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String sqliteStartDate = sqliteDateFormat.format(startDate);
            String sqliteEndDate = sqliteDateFormat.format(endDate);

            // Calculate the number of days between two dates using DATEDIFF
            String query = "SELECT (strftime('%s', ?) - strftime('%s', ?)) / (60 * 60 * 24) AS day_diff";
            String[] selectionArgs = {sqliteEndDate, sqliteStartDate};
            SQLiteDatabase database = this.getReadableDatabase();
            Cursor cursor = database.rawQuery(query, selectionArgs);
            if (cursor != null) {
                cursor.moveToFirst();
                 dayDiff = cursor.getInt(cursor.getColumnIndex("day_diff"));

                // Now, 'dayDiff' holds the number of days between the two dates
                cursor.close(); // Don't forget to close the cursor when done
            }
        } catch (ParseException e) {
            // Handle parsing exception if needed
            e.printStackTrace();
        }
        return dayDiff;
    }

    long getUserId(String email, String password) {
        long userID = 0;
        SQLiteDatabase database = this.getReadableDatabase();
        String whereClause = COLUMN_USER_EMAIL + "='" + email + "' AND " + COLUMN_USER_PASSWORD + "='" + password + "'";
        Cursor cursor = database.query(TABLE_USER, null, whereClause, null, null, null, null);
        if (cursor.moveToFirst())
            userID = cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID));
        return userID;
    }


    Cursor getDistinctMonths(long cid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.query(STATUS_TABLE_NAME, new String[]{DATE_KEY}, C_ID + "=" + cid, null, "substr(" + DATE_KEY + ",4,7)", null, null);//01.04.2020
    }


    public boolean checkUser(String email, String password) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        // selection arguments
        String[] selectionArgs = {email, password};
        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        //db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public int getNumberOfDaysStudentPresentInSchool(String sid, String date) {
        int count = 0;
        String[] projection = {"COUNT(*)"};

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = dateFormat.format(new Date());

        String selection = "STATUS_DATE BETWEEN ? AND ? AND _SID=? AND STATUS=?";

        String[] selectionArgs = {date, currentDate, sid, "P"};


        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(
                STATUS_TABLE_NAME,             // Table name
                projection,           // Columns to return (in this case, only the count)
                selection,            // WHERE clause
                selectionArgs,        // Values for the WHERE clause
                null,                 // GROUP BY (not needed for counting)
                null,                 // HAVING (not needed for counting)
                null                  // ORDER BY (not needed for counting)
        );

        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0); // Get the count from the first column

            // Now, 'count' holds the count of rows that match the specified conditions
            cursor.close(); // Don't forget to close the cursor when done
        }
        return count;


    }


}

