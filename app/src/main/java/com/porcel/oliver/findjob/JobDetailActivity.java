package com.porcel.oliver.findjob;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.porcel.oliver.findjob.data.JobPost;
import com.porcel.oliver.findjob.data.JobPostDbContract;

/**
 * Created by OLiVeR on 30/10/2015.
 */
public class JobDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        TextView titleTextView = (TextView)findViewById(R.id.job_title_text_view);
        TextView descriptionTextView = (TextView)findViewById(R.id.job_description_text_view);
        ListView contactsListView = (ListView)findViewById(R.id.lv_contacts);

        titleTextView.setText(getIntent().getStringExtra("TITLE"));
        descriptionTextView.setText(getIntent().getStringExtra("DESCRIPTION"));
        String [] Contacts = getIntent().getStringArrayExtra("CONTACTS");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,Contacts);
        contactsListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
