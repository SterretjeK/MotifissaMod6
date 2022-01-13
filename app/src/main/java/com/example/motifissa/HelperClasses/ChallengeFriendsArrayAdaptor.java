package com.example.motifissa.HelperClasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.motifissa.R;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ChallengeFriendsArrayAdaptor extends ArrayAdapter<User> implements Filterable {
    private ArrayList<User> friends;
    private ArrayList<User> originalFriends;
    private final Activity context;

    public ChallengeFriendsArrayAdaptor(Activity context, ArrayList<User> friends) {
        super(context, R.layout.list_friends, friends);
        this.context = context;
        this.friends = friends;
        this.originalFriends = this.friends;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChallengeFriendsArrayAdaptor.ViewHolder viewHolder;
        if (convertView == null){ // if the element isn't generated before (or deleted), make a new one
            viewHolder = new ChallengeFriendsArrayAdaptor.ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_friends, parent, false);

            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.textViewDesc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.icon = convertView.findViewById(R.id.icon_view);

            // set the tag so it can be recovered
            convertView.setTag(viewHolder);

        } else { // else use the old one
            viewHolder = (ChallengeFriendsArrayAdaptor.ViewHolder) convertView.getTag();
        }

        //change the contents of the view element
        //try catch because working with JSON
        viewHolder.textViewName.setText(this.friends.get(position).getName());
        viewHolder.textViewDesc.setText(MessageFormat.format("#{0}", this.friends.get(position).getID()));
        viewHolder.icon.setVisibility(View.GONE);

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

    Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            ArrayList<User> filteredList = new ArrayList<>();

            // make what the user entered lowercase so cases are ignored:
            String input = constraint.toString().toLowerCase();

            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(originalFriends != null) {
                for (User user : originalFriends) {
                    String userName = user.getName();
                    String userID = user.getID();

                    String searchable = userName + userID;

                    if (searchable.toLowerCase().contains(input))
                        filteredList.add(user);

                }

                // Store the JSONObject[] in a filterResult
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            friends = (ArrayList<User>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public int getCount () {
        return friends.size();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return this.friends.get(position);
    }

    public void changeFriends(ArrayList<User> friends) {
        this.friends = friends;
        this.originalFriends = this.friends;
        notifyDataSetChanged();
    }
}
