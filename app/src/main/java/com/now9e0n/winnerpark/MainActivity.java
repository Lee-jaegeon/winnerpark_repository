package com.now9e0n.winnerpark;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    @BindView(R.id.background_imv)
    ImageView backgroundImv;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private Drawable homeAsUpIndicator;
    @BindView(R.id.title_tv)
    TextView titleTv;

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private ProfileFragment profileFragment;

    private Drawable homeIcon;
    private Drawable addIcon;
    private Drawable profileIcon;

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
        homeAsUpIndicator = getReSizedDrawable(R.drawable.menu, 20, 20);
        actionBar.setHomeAsUpIndicator(homeAsUpIndicator);

        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer));

        MyAdapter.MainFragmentPagerAdapter adapter = new MyAdapter.MainFragmentPagerAdapter
                (getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        profileFragment = new ProfileFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(addFragment);
        adapter.addFragment(profileFragment);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        tabLayout.setupWithViewPager(viewPager);

        homeIcon = getReSizedDrawable(R.drawable.tab_home, 25, 25);
        addIcon = getReSizedDrawable(R.drawable.tab_add, 25, 25);
        profileIcon = getReSizedDrawable(R.drawable.tab_profile, 25, 25);

        tabLayout.getTabAt(0).setIcon(homeIcon).setTag("home");
        tabLayout.getTabAt(1).setIcon(addIcon).setTag("add");
        tabLayout.getTabAt(2).setIcon(profileIcon).setTag("profile");

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag().equals("home")) applyTheme(homeFragment.getFragment().isVisible());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getTag().equals("home")) applyTheme(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void applyTheme(boolean isTurn) {
        int white = getColor(android.R.color.white);
        int black = getColor(android.R.color.black);
        Drawable tabIndicator = tabLayout.getTabSelectedIndicator();

        if (isTurn) {
            tabIndicator.setColorFilter(white, PorterDuff.Mode.SRC_ATOP);
            homeIcon.setColorFilter(white, PorterDuff.Mode.SRC_ATOP);
            addIcon.setColorFilter(white, PorterDuff.Mode.SRC_ATOP);
            profileIcon.setColorFilter(white, PorterDuff.Mode.SRC_ATOP);

            homeAsUpIndicator.setColorFilter(white, PorterDuff.Mode.SRC_ATOP);
            titleTv.setTextColor(white);
            backgroundImv.setVisibility(View.VISIBLE);
        }

        else {
            tabIndicator.setColorFilter(black, PorterDuff.Mode.SRC_ATOP);
            homeIcon.setColorFilter(black, PorterDuff.Mode.SRC_ATOP);
            addIcon.setColorFilter(black, PorterDuff.Mode.SRC_ATOP);
            profileIcon.setColorFilter(black, PorterDuff.Mode.SRC_ATOP);

            homeAsUpIndicator.setColorFilter(black, PorterDuff.Mode.SRC_ATOP);
            titleTv.setTextColor(black);
            backgroundImv.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = homeFragment.getFragment();
        if (fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction().hide(fragment).commit();
            applyTheme(false);
        }

        else super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        app.saveUser();
    }
}