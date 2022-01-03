package com.example.motifissa;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FriendsListAdaptor extends ArrayAdapter<JSONObject>{
//    private String[] names;
//    private String[] desc;
    private JSONObject[] users;
    private String[] friends;
    private Activity context;

    public FriendsListAdaptor(Activity context, JSONObject[] users, String[] friends) {
        super(context, R.layout.list_friends, users);
        this.context = context;
        this.users = users;
//        this.names = names;
//        this.desc = desc;
        this.friends = friends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_friends, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewDesc = listViewItem.findViewById(R.id.textViewDesc);
        TextView icon = listViewItem.findViewById(R.id.icon_view);

//        textViewName.setText(names[position]);
//        Log.d("LIST", names[position]);
//        textViewDesc.setText(desc[position]);

        try {
            textViewName.setText(this.users[position].getString("Name"));
            textViewDesc.setText(this.users[position].getString("ID"));
            if (Arrays.asList(friends).contains(this.users[position].getString("Name"))){
                icon.setVisibility(View.VISIBLE);
            } else {
                icon.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  listViewItem;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    public void changeFriends(String[] friends){
        this.friends = friends;
    }
}
