package com.giansoft.cryptedchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Created by gianv on 22/03/2017.
 */

public class ContactListAdapter extends ArrayAdapter<Contact> {
    private int resource;
    Context ctx;

    public  ContactListAdapter(Context ctx, int resourceId, Contact[] objects) {
        super(ctx, resourceId, objects);
        this.ctx = ctx;
        resource = resourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater contactInflater = LayoutInflater.from(getContext());
        View customView = contactInflater.inflate(resource, parent, false);

        Contact singleContact = getItem(position);
        TextView tvName = (TextView) customView.findViewById(R.id.tvName);
        TextView tvSurname = (TextView) customView.findViewById(R.id.tvSurname);
        TextView tvUsername = (TextView) customView.findViewById(R.id.tvUsername);

        tvName.setText(singleContact.getName());
        tvSurname.setText(singleContact.getSurname());
        return customView;
    }
}
