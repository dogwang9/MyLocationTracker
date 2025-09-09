package com.example.mylocationtracker;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mylocationtracker.fragment.ContactTabFragment;
import com.example.mylocationtracker.fragment.HomeTabFragment;
import com.example.mylocationtracker.fragment.SettingsTabFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainUpdateActivity extends AppCompatActivity {
    public static final int TAB_FRAGMENT_TAG_HOME_INDEX = 0;
    public static final int TAB_FRAGMENT_TAG_CONTACT_INDEX = 1;
    public static final int TAB_FRAGMENT_TAG_SETTINGS_INDEX = 2;
    private ViewPager2 mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
    }

    private void initView() {
        TabLayout tabLayout = findViewById(R.id.tl_titles);
        mViewPager = findViewById(R.id.vp_content);
        mViewPager.setUserInputEnabled(false);
        mViewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case TAB_FRAGMENT_TAG_HOME_INDEX:
                        return new HomeTabFragment();
                    case TAB_FRAGMENT_TAG_CONTACT_INDEX:
                        return new ContactTabFragment();
                    case TAB_FRAGMENT_TAG_SETTINGS_INDEX:
                        return new SettingsTabFragment();
                    default:
                        throw new IllegalArgumentException("Unexpected position, " + position);
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, mViewPager, true, false, (tab, position) -> {
            switch (position) {
                case TAB_FRAGMENT_TAG_HOME_INDEX:
                    tab.setText(R.string.home);
                    break;
                case TAB_FRAGMENT_TAG_CONTACT_INDEX:
                    tab.setText(R.string.contact);
                    break;
                case TAB_FRAGMENT_TAG_SETTINGS_INDEX:
                    tab.setText(R.string.settings);
                    break;
            }
        }).attach();
    }
}