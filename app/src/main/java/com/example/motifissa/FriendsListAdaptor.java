package com.example.motifissa;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class FriendsListAdaptor extends ArrayAdapter<JSONObject> implements Filterable {

    private JSONObject[] users;
    private final JSONObject[] originalUsers;
    private String[] friends;
    private final Activity context;

    public FriendsListAdaptor(Activity context, JSONObject[] users, String[] friends) {
        super(context, R.layout.list_friends, users);
        this.context = context;
        this.users = users;
        this.originalUsers = users;

        this.friends = friends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){ // if the element isn't generated before (or deleted), make a new one
            viewHolder = new ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_friends, parent, false);

            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.textViewDesc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.icon = convertView.findViewById(R.id.icon_view);

            // set the tag so it can be recovered
            convertView.setTag(viewHolder);

        } else { // else use the old one
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //change the contents of the view element
        try { //try catch because working with JSON
            viewHolder.textViewName.setText(this.users[position].getString("Name"));
            viewHolder.textViewDesc.setText(this.users[position].getString("ID"));

            if (Arrays.asList(friends).contains(this.users[position].getString("Name"))) { // needs updating so it also contains the id
                viewHolder.icon.setVisibility(View.VISIBLE);
            } else {
                viewHolder.icon.setVisibility(View.INVISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private static class ViewHolder{
        private TextView textViewName;
        private TextView textViewDesc;
        private TextView icon;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return myFilter;
    }

    public void changeFriends(String[] friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<JSONObject> filteredList = new ArrayList<>();

            // make what the user entered lowercase so cases are ignored:
            String input = constraint.toString().toLowerCase();

            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(input != null && originalUsers!=null) {
                for (JSONObject user : originalUsers) {
                    String userName = "";
                    String userID = "";

                    try {
                        userName = user.getString("Name");
                        userID = user.getString("ID");
                    } catch (JSONException e) {
                        Log.e("Friends List adaptor", "name and/or ID of user wasn't found: " + user);
                        e.printStackTrace();
                    }
                    String searchable = userName + userID;

                    if (searchable.toLowerCase().contains(input))
                        filteredList.add(user);


                }

                // Change the ArrayList<JSONObject> to a JSONObject[]
                JSONObject[] tempList = new JSONObject[filteredList.size()];
                filteredList.toArray(tempList);

                // Store the JSONObject[] in a filterResult
                filterResults.values = tempList;
                filterResults.count = tempList.length;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            users = (JSONObject[]) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public int getCount () {
        return users.length;
    }

    @Nullable
    @Override
    public JSONObject getItem(int position) {
        return this.users[position];
    }
}
