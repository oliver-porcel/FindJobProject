package com.porcel.oliver.findjob;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by OLiVeR on 30/10/2015.
 */
public class PostNewJobActivity extends AppCompatActivity {
    EditText et_title,et_description,et_contact;
    Button post_button;
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);
        et_title= (EditText) findViewById(R.id.edit_text_title);
        et_description= (EditText) findViewById(R.id.edit_text_description);
        et_contact= (EditText) findViewById(R.id.edit_text_contact);
        post_button=(Button) findViewById(R.id.button_post);
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

    public void postJob(View view) throws JSONException, UnsupportedEncodingException{

        final String URLDirection = "http://dipandroid-ucb.herokuapp.com/work_posts.json";
        post_button.setEnabled(false);
        String title, description, contacts;
        title = et_title.getText().toString();
        description = et_description.getText().toString();
        contacts = et_contact.getText().toString();
        JSONObject jobToPost = new JSONObject();
        jobToPost.put("title",title);
        jobToPost.put("description", description);
        jobToPost.put("contacts", new JSONArray(Arrays.toString(contacts.split(","))));
        JSONObject work_post = new JSONObject();
        work_post.put("work_post", jobToPost);

        System.out.println("EL JSON A ENVIAR");
        System.out.println(work_post.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        StringEntity entity = new StringEntity(work_post.toString());
        entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

        client.post(this, URLDirection ,entity,"application/json",new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if(response.getInt("id")>0){
                        AlertDialog.Builder builder=new AlertDialog.Builder(PostNewJobActivity.this);
                        builder.setTitle(R.string.dialog_title).setMessage(R.string.dialog_message);
                        builder.setPositiveButton(R.string.dialog_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onBackPressed();
                            }
                        });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("onFailure", "onFailure", throwable);
                post_button.setEnabled(true);
                AlertDialog.Builder builder=new AlertDialog.Builder(PostNewJobActivity.this);
                builder.setTitle(R.string.dialog__error_title).setMessage(R.string.dialog_error_message).show();
            }

        });
    }
}
