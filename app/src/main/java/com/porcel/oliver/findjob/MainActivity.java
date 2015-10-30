package com.porcel.oliver.findjob;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.porcel.oliver.findjob.data.JobPostDbContract;
import com.porcel.oliver.findjob.data.JobsContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.porcel.oliver.findjob.data.JobPostDbContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private JobsAdapter jobsAdapter;
    private static final int JOBS_LOADER = 0;
    private String[] contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        syncDataBase();
        jobsAdapter = new JobsAdapter(this, null, false);
        ListView listView = (ListView)findViewById(R.id.job_post_list_view);
        listView.setAdapter(jobsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position);
                Intent intent = new Intent(MainActivity.this, JobDetailActivity.class);
                intent.putExtra("ID", cursor.getLong(0));
                intent.putExtra("TITLE", cursor.getString(1));
                intent.putExtra("DESCRIPTION", cursor.getString(2));
                String [] columns = {ContactEntry._ID, ContactEntry.COLUMN_NUMBER};
                String[] whereArgs={cursor.getLong(0)+""};
                System.out.println(ContactEntry.CONTENT_URI.buildUpon().appendPath(cursor.getLong(0)+"").build());
                Cursor otherCursor = getContentResolver().query(ContactEntry.CONTENT_URI.buildUpon().appendPath(cursor.getLong(0)+"").build(), columns,null,null,null );
                //Cursor otherCursor = getContentResolver().query(ContactEntry.CONTENT_URI, columns, ContactEntry.COLUMN_JOB_ID + "=?", whereArgs, ContactEntry._ID + " DESC" );
                contacts = null;
                contacts = new String[otherCursor.getCount()];
                for (int i = 0; i < otherCursor.getCount(); i++) {
                    otherCursor.moveToNext();
                    contacts[i]=otherCursor.getString(1);
                }
                otherCursor.close();
                intent.putExtra("CONTACTS", contacts);
                startActivity(intent);
            }
        });
        getSupportLoaderManager().initLoader(JOBS_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_app, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.sync:
                syncDataBase();
                break;
            case R.id.post:
                Intent post_job_intent = new Intent(this, PostNewJobActivity.class);
                startActivity(post_job_intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }


    public void syncDataBase(){
        NetworkOperationAsyncTask asyncTask = new NetworkOperationAsyncTask();
        asyncTask.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, JobEntry.CONTENT_URI,
                new String[] {JobEntry._ID, JobEntry.COLUMN_TITLE, JobEntry.COLUMN_DESCRIPTION, JobEntry.COLUMN_POSTED_DATE},
                null, null, JobEntry.COLUMN_POSTED_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        jobsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        jobsAdapter.swapCursor(null);
    }


    private class NetworkOperationAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // This is my URL: http://dipandroid-ucb.herokuapp.com/work_posts.json
            HttpURLConnection urlConnection = null; // HttpURLConnection Object
            BufferedReader reader = null; // A BufferedReader in order to read the data as a file
            Uri buildUri = Uri.parse("http://dipandroid-ucb.herokuapp.com").buildUpon() // Build the URL using the Uri class
                    .appendPath("work_posts.json").build();
            try {
                URL url = new URL(buildUri.toString()); // Create a new URL

                urlConnection = (HttpURLConnection) url.openConnection(); // Get a HTTP connection
                urlConnection.setRequestMethod("GET"); // I'm using GET to query the server
                urlConnection.addRequestProperty("Content-Type", "application/json"); // The MIME type is JSON
                urlConnection.connect(); // Connect!! to the cloud!!!

                // Methods in order to read a text file (In this case the query from the server)
                InputStream inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                // Save the data in a String
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }

                saveJSONToDatabase(buffer.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }

                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

            }
            return null;
        }

        private void saveJSONToDatabase(String json) throws JSONException {
            JSONArray array = new JSONArray(json);

            for (int i = 0; i < array.length(); i++) {
                JSONObject jobPostJSON = array.getJSONObject(i);
                int id = jobPostJSON.getInt("id");
                String title = jobPostJSON.getString("title");
                String description = jobPostJSON.getString("description");
                String postedDate = jobPostJSON.getString("posted_date");
                ContentValues contentValues = new ContentValues();

                contentValues.put(JobEntry._ID, id);
                contentValues.put(JobEntry.COLUMN_TITLE, title);
                contentValues.put(JobEntry.COLUMN_DESCRIPTION, description);
                contentValues.put(JobEntry.COLUMN_POSTED_DATE, postedDate);

                getContentResolver().insert(JobEntry.CONTENT_URI, contentValues);

                JSONArray contactsJSON = jobPostJSON.getJSONArray("contacts");
                for (int j = 0; j < contactsJSON.length(); j++) {
                    String contact = contactsJSON.getString(j);
                    ContentValues contactContentValues = new ContentValues();

                    contactContentValues.put(ContactEntry.COLUMN_NUMBER, contact);
                    contactContentValues.put(ContactEntry.COLUMN_JOB_ID, id);

                    getContentResolver().insert(ContactEntry.CONTENT_URI, contactContentValues);
                }
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            getSupportLoaderManager().restartLoader(JOBS_LOADER, null, MainActivity.this);
        }
    }
}
