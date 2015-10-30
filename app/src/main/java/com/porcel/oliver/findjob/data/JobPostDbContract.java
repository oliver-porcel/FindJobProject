package com.porcel.oliver.findjob.data;

/**
 * Created by OLiVeR on 30/10/2015.
 */
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class JobPostDbContract {
    public static final String CONTENT_AUTHORITY = "com.porcel.oliver.findjob";
    /*Basic information for our content provider*/
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String JOBS_PATH = "jobs";
    public static final String CONTACTS_PATH = "contacts";
    public static final String CONTACTS_WITH_JOB = "contacts_with_job";

    public static class JobEntry implements BaseColumns {
        /* Base Uri for our Job table in order to access from
        * the content provider */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(JOBS_PATH).build();

        /* Define the MIME type for the data DIR is for several
        * and ITEM is for a specific resource */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + JOBS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + JOBS_PATH;

        /*Table definition*/
        public static final String TABLE_NAME = "jobs";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_POSTED_DATE = "posted_date";
    }

    public static class ContactEntry implements BaseColumns {
        /* Base Uri for our Job table in order to access from
        * the content provider */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CONTACTS_PATH).build();

        public static final Uri CONTACTS_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CONTACTS_PATH).appendPath(CONTACTS_WITH_JOB).build();

        /* Define the MIME type for the data DIR is for several
        * and ITEM is for a specific resource */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTACTS_PATH;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + CONTACTS_PATH;

        /* Table definition */
        public static final String TABLE_NAME = "contacts";
        public static final String COLUMN_JOB_ID = "job_id";
        public static final String COLUMN_NUMBER = "number";
    }
}