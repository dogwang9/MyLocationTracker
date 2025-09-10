package com.example.mylocationtracker.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mylocationtracker.R;
import com.example.mylocationtracker.adapter.ContactAdapter;
import com.example.mylocationtracker.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactTabFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ContactAdapter mContactAdapter;
    private List<Contact> mContactList;
    private final String API_KEY = "XXXX";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        mRecyclerView = view.findViewById(R.id.contact_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mContactList = new ArrayList<>();
        mContactList.add(new Contact("AAA", 31.95, 118.75));
        mContactList.add(new Contact("BBB", 31.97, 118.75));
        mContactList.add(new Contact("CCC", 31.97, 118.768));
        mContactList.add(new Contact("DDD", 31.975, 118.757));
        mContactList.add(new Contact("EEE", 31.975, 118.750));

        mContactAdapter = new ContactAdapter(mContactList, API_KEY);
        mRecyclerView.setAdapter(mContactAdapter);

        return view;
    }
}
