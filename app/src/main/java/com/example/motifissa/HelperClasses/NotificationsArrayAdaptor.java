package com.example.motifissa.HelperClasses;

import android.app.Activity;
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

import java.text.MessageFormat;
import java.util.ArrayList;

public class NotificationsArrayAdaptor extends ArrayAdapter<Notification> implements Filterable {
    private ArrayList<Notification> notifications;
    private ArrayList<Notification> originalNotifications;
    private final Activity context;

    public NotificationsArrayAdaptor(Activity context, ArrayList<Notification> notifications) {
        super(context, R.layout.list_notifications, notifications);
        this.context = context;
        this.notifications = notifications;
        this.originalNotifications = this.notifications;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationsArrayAdaptor.ViewHolder viewHolder;
        if (convertView == null){ // if the element isn't generated before (or deleted), make a new one
            viewHolder = new NotificationsArrayAdaptor.ViewHolder();

            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_notifications, parent, false);

            viewHolder.textViewName = convertView.findViewById(R.id.textViewName);
            viewHolder.textViewDesc = convertView.findViewById(R.id.textViewDesc);
            viewHolder.icon = convertView.findViewById(R.id.icon);

            // set the tag so it can be recovered
            convertView.setTag(viewHolder);

        } else { // else use the old one
            viewHolder = (NotificationsArrayAdaptor.ViewHolder) convertView.getTag();
        }

        //change the contents of the view element
        //try catch because working with JSON
        viewHolder.textViewName.setText(this.notifications.get(position).getMessage());
        viewHolder.textViewDesc.setText(this.notifications.get(position).getDate());
        switch (this.notifications.get(position).getType()){
            case CHALLENGE:
                viewHolder.icon.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_challenge_icon));
                break;
            case MESSAGE:
                viewHolder.icon.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.custom_email_icon));
                break;
            case FRIEND_REQUEST:
                viewHolder.icon.setImageDrawable(AppCompatResources.getDrawable(getContext(), R.drawable.ic_friends));
                break;
            default:
                viewHolder.icon.setVisibility(View.INVISIBLE);
                break;
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
            ArrayList<Notification> filteredList = new ArrayList<>();

            // make what the user entered lowercase so cases are ignored:
            String input = constraint.toString();

            //constraint is the result from text you want to filter against.
            //objects is your data set you will filter from
            if(originalNotifications != null && !constraint.equals("ALL")) {
                for (Notification notification : originalNotifications) {
                    Notification.NotificationType notificationType = notification.getType();

                    if (notificationType.toString().equals(input))
                        filteredList.add(notification);
                }

                // Store the ArrayList in a filterResult
                filterResults.values = filteredList;
                filterResults.count = filteredList.size();
            } else if (constraint.equals("ALL")){
                filterResults.values = originalNotifications;
                filterResults.count = originalNotifications.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifications = (ArrayList<Notification>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    };

    public int getCount () {
        return notifications.size();
    }

    @Nullable
    @Override
    public Notification getItem(int position) {
        return this.notifications.get(position);
    }

    public void updateNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
        this.originalNotifications = this.notifications;
        notifyDataSetChanged();
    }
}
