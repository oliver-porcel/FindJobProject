package com.porcel.oliver.findjob;

/**
 * Created by OLiVeR on 30/10/2015.
 */
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class JobsAdapter extends CursorAdapter {
    public JobsAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.job_post_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleTextView = (TextView)view.findViewById(R.id.title_text_view);
        TextView dateTextView = (TextView)view.findViewById(R.id.date_text_view);

        titleTextView.setText(cursor.getString(1));
        dateTextView.setText(cursor.getString(3));
    }
}
