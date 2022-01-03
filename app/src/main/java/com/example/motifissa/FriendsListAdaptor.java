package com.example.motifissa;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class FriendsListAdaptor extends ArrayAdapter<String>{
    private String[] names;
    private String[] desc;
    private String[] friends;
    private Activity context;

    public FriendsListAdaptor(Activity context, String[] names, String[] desc, String[] friends) {
        super(context, R.layout.list_friends, names);
        this.context = context;
        this.names = names;
        this.desc = desc;
        this.friends = friends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_friends, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewDesc = (TextView) listViewItem.findViewById(R.id.textViewDesc);
        TextView icon = (TextView) listViewItem.findViewById(R.id.icon_view);

        textViewName.setText(names[position]);
        Log.d("LIST", names[position]);
        textViewDesc.setText(desc[position]);

        if (Arrays.asList(friends).contains(names[position])){
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.INVISIBLE);
        }
        return  listViewItem;
    }
}
