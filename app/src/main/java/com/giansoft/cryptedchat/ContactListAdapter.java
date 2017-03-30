package com.giansoft.cryptedchat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by gianv on 22/03/2017.
 */

public class ContactListAdapter extends ArrayAdapter<Contact> {
    private int resource = R.layout.contact_inflater;
    Context ctx;

    public  ContactListAdapter(Context ctx,  ArrayList<Contact> objects) {
        super(ctx, 0, objects);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater contactInflater = LayoutInflater.from(getContext());
        if(convertView == null)
            convertView = contactInflater.inflate(resource, parent, false);

        Contact singleContact = getItem(position);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvSurname = (TextView) convertView.findViewById(R.id.tvSurname);
        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);

        tvName.setText(singleContact.getSurname());
        tvSurname.setText(singleContact.getName());
        tvUsername.setText(singleContact.getUsername());
        return convertView;
    }
}
