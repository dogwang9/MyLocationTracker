package com.example.mylocationtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    public interface OnFriendClickListener {
        void onClick(Friend friend);
    }

    private List<Friend> mFriends;
    private OnFriendClickListener listener;

    public FriendsAdapter(List<Friend> friends, OnFriendClickListener listener) {
        this.mFriends = friends;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = mFriends.get(position);
        holder.text.setText(friend.name);
        holder.itemView.setOnClickListener(v -> listener.onClick(friend));
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}
