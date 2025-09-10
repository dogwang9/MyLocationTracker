package com.example.mylocationtracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentActivity;

public class MyZoneActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_zone);

        initView();
    }

    private void initView() {
        LinearLayout homeLayout = findViewById(R.id.item_home);
        LinearLayout companyLayout = findViewById(R.id.item_company);
        LinearLayout schoolLayout = findViewById(R.id.item_school);
        LinearLayout newAreaLayout = findViewById(R.id.item_new_area);
        homeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewAreaActivity.class);
            startActivity(intent);
        });
        companyLayout.setOnClickListener(v -> {
            homeLayout.callOnClick();
        });
        schoolLayout.setOnClickListener(v -> {
            homeLayout.callOnClick();
        });
        newAreaLayout.setOnClickListener(v -> {
            homeLayout.callOnClick();
        });
    }
}