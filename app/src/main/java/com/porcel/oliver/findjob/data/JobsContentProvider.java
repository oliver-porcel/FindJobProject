package com.porcel.oliver.findjob.data;

/**
 * Created by OLiVeR on 30/10/2015.
 */
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class JobsContentProvider extends ContentProvider {
    /*Build a URI Matcher*/
    private static final UriMatcher uriMatcher = buildUriMatcher();
    private static final int JOBS = 100;
    private static final int JOBS_WITH_ID = 101;
    private static final int CONTACTS = 200;
    private static final int CONTACTS_WITH_JOB = 201;

    private JobPostDbHelper dbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = JobPostDbContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, JobPostDbContract.JOBS_PATH, JOBS);
        matcher.addURI(authority, JobPostDbContract.JOBS_PATH + "/#", JOBS_WITH_ID);
        matcher.addURI(authority, JobPostDbContract.CONTACTS_PATH, CONTACTS);
        matcher.addURI(authority, JobPostDbContract.CONTACTS_PATH + "/#", CONTACTS_WITH_JOB);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new JobPostDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case JOBS:
                return JobPostDbContract.JobEntry.CONTENT_TYPE;
            case JOBS_WITH_ID:
                return JobPostDbContract.JobEntry.CONTENT_ITEM_TYPE;
            case CONTACTS:
                return JobPostDbContract.ContactEntry.CONTENT_TYPE;
            case CONTACTS_WITH_JOB:
                return JobPostDbContract.ContactEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        System.out.println(uri);
        System.out.println(match);


        switch (match) {
            case JOBS:
                return dbHelper.getReadableDatabase()
                        .query(JobPostDbContract.JobEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null, null, sortOrder);
            case JOBS_WITH_ID:
                String id = uri.getPathSegments().get(2);
                return dbHelper.getReadableDatabase()
                        .query(true, JobPostDbContract.JobEntry.TABLE_NAME,
                                projection,
                                JobPostDbContract.JobEntry._ID + "= ?",
                                new String[] {id}, null, null, null, null);
            case CONTACTS:
                return dbHelper.getReadableDatabase()
                        .query(JobPostDbContract.ContactEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null, null, sortOrder);
            case CONTACTS_WITH_JOB:
                System.out.println(uri.getPathSegments().get(1));
                String jobID = uri.getPathSegments().get(1);
                return dbHelper.getReadableDatabase()
                        .query(JobPostDbContract.ContactEntry.TABLE_NAME,
                                projection,
                                JobPostDbContract.ContactEntry.COLUMN_JOB_ID + "= ?",
                                new String[] {jobID}, null, null, null, null);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = uriMatcher.match(uri);
        Uri returnUri;
        long retValue;

        switch (match) {
            case JOBS:
                retValue = dbHelper.getWritableDatabase().insert(JobPostDbContract.JobEntry.TABLE_NAME,
                        null, values);
                if (retValue != -1) {
                    returnUri = JobPostDbContract.JobEntry
                            .CONTENT_URI.buildUpon().appendPath(Long.toString(retValue)).build();
                } else {
                    throw new SQLException("Error inserting Job");
                }
                break;
            case CONTACTS:
                retValue = dbHelper.getWritableDatabase().insert(JobPostDbContract.ContactEntry.TABLE_NAME,
                        null, values);
                if (retValue != -1) {
                    returnUri = JobPostDbContract.ContactEntry.CONTENT_URI;
                } else {
                    throw new SQLException("Error inserting Job");
                }
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
