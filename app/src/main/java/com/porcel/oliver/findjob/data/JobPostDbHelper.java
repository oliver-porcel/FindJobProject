package com.porcel.oliver.findjob.data;

/**
 * Created by OLiVeR on 30/10/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.porcel.oliver.findjob.data.JobPostDbContract.*;

public class JobPostDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "job_posts.db";
    private static final int VERSION = 1;

    public JobPostDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_JOB = "CREATE TABLE " + JobEntry.TABLE_NAME + "(" +
                JobEntry._ID + " INTEGER PRIMARY KEY ON CONFLICT REPLACE," +
                JobEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                JobEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL," +
                JobEntry.COLUMN_POSTED_DATE + " TEXT NOT NULL)";
        final String SQL_CREATE_CONTACT = "CREATE TABLE " + ContactEntry.TABLE_NAME + "(" +
                ContactEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ContactEntry.COLUMN_JOB_ID + " INTEGER NOT NULL," +
                ContactEntry.COLUMN_NUMBER + " TEXT NOT NULL," +
                "FOREIGN KEY (" + ContactEntry.COLUMN_JOB_ID + ") REFERENCES " +
                JobEntry.TABLE_NAME + " (" + JobEntry._ID + ")," +
                "UNIQUE(" + ContactEntry.COLUMN_JOB_ID + ","
                + ContactEntry.COLUMN_NUMBER + ") ON CONFLICT REPLACE);";
        db.execSQL(SQL_CREATE_JOB);
        db.execSQL(SQL_CREATE_CONTACT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ContactEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + JobEntry.TABLE_NAME);
        onCreate(db);
    }
}
