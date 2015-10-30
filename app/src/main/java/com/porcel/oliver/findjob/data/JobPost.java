package com.porcel.oliver.findjob.data;

import android.content.ContentResolver;
import android.database.Cursor;
import static com.porcel.oliver.findjob.data.JobPostDbContract.*;

/**
 * Created by OLiVeR on 30/10/2015.
 */
public class JobPost {
    private String[] contacts;

    public JobPost(){}

    public String[] getContacts() {
        return contacts;
    }

    public void setContacts(ContentResolver contentResolver,int current_job_id) {

        String[] columnas_contacts={ContactEntry._ID, ContactEntry.COLUMN_JOB_ID, ContactEntry.COLUMN_NUMBER};
        String[] whereArgs={current_job_id+""};


        Cursor cursor_contacts = contentResolver.query(ContactEntry.CONTENT_URI, columnas_contacts, ContactEntry.COLUMN_JOB_ID+"=?", whereArgs,  ContactEntry._ID + " DESC");
        contacts = new String[cursor_contacts.getCount()];
        for (int i = 0; i < cursor_contacts.getCount(); i++) {
            cursor_contacts.moveToNext();
            contacts[i]=cursor_contacts.getString(2);
        }
        cursor_contacts.close();
    }
}
