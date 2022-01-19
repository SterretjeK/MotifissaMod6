package com.example.motifissa.HelperClasses;

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
import androidx.appcompat.content.res.AppCompatResources;

import com.example.motifissa.R;

import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;

public class UsersArrayAdaptor extends ArrayAdapter<User> implements Filterable {
    private ArrayList<User> users;
    private ArrayList<User> originalUsers;
    private final Activity context;
    private boolean showOnline;

    public UsersArrayAdaptor(Activity context, ArrayList<User> users, boolean showOnline) {
        super(context, R.layout.list_notifications, users);
        this.context = context;
        this.users = users;
        this.originalUsers = this.users;
        this.showOnline = showOnline;
        sortArray();
    }

    // sorts the array to make sure everyone who are online are at the top
    private void sortArray(){
        Comparator<User> comparator = (o1, o2) -> (o2.isOnline() ? 1 : 0) - (o1.isOnline() ? 1 : 0);
        users.sort(comparator);
        this.originalUsers = this.users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UsersArrayAdaptor.ViewHolder viewHolder;
        if (convertView == null){ // if the element isn't generated before (or deleted), make a new one
            viewHolder = new UsersArrayAdaptor.ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_notifications, parent, false);

            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.textViewDesc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.icon = convertView.findViewById(R.id.icon);

            // set the tag so it can be recovered
            convertView.setTag(viewHolder);

        } else { // else use the old one
            viewHolder = (UsersArrayAdaptor.ViewHolder) convertView.getTag();
        }

        //change the contents of the view element
        viewHolder.textViewName.setText(this.users.get(position).getName());
        viewHolder.textViewDesc.setText(MessageFormat.format("#{0}", this.users.get(position).getID()));

        // icon
        if (!showOnline)
            viewHolder.icon.setVisibility(View.GONE);
        else{
            viewHolder.icon.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.custom_online_icon));
            if (this.users.get(position).isOnline()){
                viewHolder.icon.setImageTintList(getContext().getColorStateList(R.color.color_primary));
            } else {
                viewHolder.icon.setImageTintList(getContext().getColorStateList(R.color.gray_500));
            }
        }

        return convertView;
    }

    private static class ViewHolder{
        private TextView textViewName;
        private TextView textViewDesc;
        private ImageView icon;
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
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

    public void changeUsers(ArrayList<User> users) {
        this.users = users;
//        this.originalUsers = this.users;
        sortArray();
        notifyDataSetChanged();
    }

    public ArrayList<User> getCurrentUsers(){
        return originalUsers;
    }
}
