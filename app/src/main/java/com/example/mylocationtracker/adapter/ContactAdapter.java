package com.example.mylocationtracker.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mylocationtracker.R;
import com.example.mylocationtracker.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Contact> mContactList;
    private String googleMapsApiKey;

    public ContactAdapter(List<Contact> contactList, String apiKey) {
        this.mContactList = contactList;
        this.googleMapsApiKey = apiKey;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.tvName.setText(contact.getName());

        String url = "https://maps.googleapis.com/maps/api/staticmap?" +
                "center=" + contact.getLatitude() + "," + contact.getLongitude() +
                "&zoom=12&size=400x200&markers=color:red%7C" +
                contact.getLatitude() + "," + contact.getLongitude() +
                "&key=" + googleMapsApiKey;

        Log.d("StaticMapURL", url);

        Glide.with(holder.imgMap.getContext())
                .load(url)
                .into(holder.imgMap);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView imgMap;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            imgMap = itemView.findViewById(R.id.imgMap);
        }
    }
}
