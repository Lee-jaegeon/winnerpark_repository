package com.now9e0n.winnerpark;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.now9e0n.winnerpark.AppManager.activityWindowSet;
import static com.now9e0n.winnerpark.AppManager.getReSizedDrawable;

public class MainActivity extends AppCompatActivity {

    private AppManager app;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityWindowSet(this);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        app = (AppManager) getApplication();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getReSizedDrawable(R.drawable.menu, 20, 20));

        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer));

        MyAdapter.MainFragmentPagerAdapter adapter = new MyAdapter.MainFragmentPagerAdapter
                (getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new AddFragment());
        adapter.addFragment(new ProfileFragment());
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(getReSizedDrawable(R.drawable.tab_home, 25, 25));
        tabLayout.getTabAt(1).setIcon(getReSizedDrawable(R.drawable.tab_add, 25, 25));
        tabLayout.getTabAt(2).setIcon(getReSizedDrawable(R.drawable.tab_profile, 25, 25));
    }

    public void addFragment(Fragment fragment) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        app.saveUser();
    }
}