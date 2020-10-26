package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.now9e0n.winnerpark.AppManager.getMyDrawable;
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

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        app = (AppManager) getApplication();

        init();
        //recyclerViewInit();
    }

    private void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getReSizedDrawable(R.drawable.menu, 20, 20));

        drawerLayout.addDrawerListener(new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer));

        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter
                (getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new Fragment());
        adapter.addFragment(new Fragment());
        adapter.addFragment(new Fragment());

        viewPager.setAdapter(adapter);
        tabLayout.addTab(new TabLayout.Tab().setIcon(getReSizedDrawable(R.drawable.home, 25, 25)));
        tabLayout.addTab(new TabLayout.Tab().setIcon(getReSizedDrawable(R.drawable.add, 25, 25)));
        tabLayout.addTab(new TabLayout.Tab().setIcon(getReSizedDrawable(R.drawable.profile, 25, 25)));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void recyclerViewInit() {
        String[] gameKindArray = app.getUser().getGameKind().split(" ");
        ArrayList<Drawable> drawableList = new ArrayList<>();

        for (String gameKind : gameKindArray) {
            int drawable = getResources().getIdentifier("icon_" + gameKind, "drawable", getPackageName());
            drawableList.add(getMyDrawable(drawable));
        }

        recyclerView.setAdapter(new MainRecyclerAdapter(drawableList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getItemDecorationCount() - parent.getChildAdapterPosition(view) > 1)
                    outRect.right = (int) (30 * AppManager.getDensityRatio());
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent motionEvent) {
                View child = rv.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                int position = rv.getChildAdapterPosition(child);
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        app.saveUser();
    }
}