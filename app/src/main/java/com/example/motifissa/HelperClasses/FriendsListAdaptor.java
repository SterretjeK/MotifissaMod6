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

public class FriendsListAdaptor extends ArrayAdapter<User> implements Filterable {

    private ArrayList<User> users;
    private final ArrayList<User> originalUsers;
    private ArrayList<String> friends;
//    private final User currentUser;
    private final Activity context;

    public FriendsListAdaptor(Activity context, ArrayList<User> users, ArrayList<String> friends){//, User currentUser) {
        super(context, R.layout.list_friends, users);
        this.context = context;
        this.friends = friends;

        this.users = users;
//        this.currentUser = currentUser;
//        this.users.remove(currentUser);
        this.originalUsers = users;
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
        //try catch because working with JSON
        viewHolder.textViewName.setText(this.users.get(position).getName());
        viewHolder.textViewDesc.setText(MessageFormat.format("#{0}", this.users.get(position).getID()));

        if (friends.contains(this.users.get(position).getUID())) { // needs updating so it also contains the id
            viewHolder.icon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.icon.setVisibility(View.INVISIBLE);
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

    public void changeFriends(ArrayList<String> friends) {
        this.friends = friends;
        notifyDataSetChanged();
    }
    public void changeUsers(ArrayList<User> users) {
        this.users = users;
        notifyDataSetChanged();
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
            if(originalUsers != null) {
                for (User user : originalUsers) {
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
        protected void publishResults(CharSequence contraint, FilterResults results) {
            users = (ArrayList<User>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public int getCount () {
        return users.size();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return this.users.get(position);
    }
}
