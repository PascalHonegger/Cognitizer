package com.example.informatik.cognitizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Informatik on 10.03.2017.
 */

public class CustomAdapter extends ArrayAdapter<Tag> {
    public CustomAdapter(Context context, ArrayList<Tag> tags) {
        super(context, 0, tags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Tag tag = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_content, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tag_name);
        // Populate the data into the template view using the data object
        tvName.setText(tag.name);
        // Return the completed view to render on screen
        return convertView;
    }
}
