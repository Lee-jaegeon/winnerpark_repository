package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private AppManager app;

    @BindView(R.id.drawer)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_host)
    TabHost tabHost;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (AppManager) getApplication();
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);

        tabHost.setup();

        TabHost.TabSpec homeTabSpec = tabHost.newTabSpec("home_tab_spec");
        homeTabSpec.setContent(R.id.home);
        ImageView homeImage = new ImageView(this);
        homeImage.setImageResource(R.drawable.home);
        homeTabSpec.setIndicator(homeImage);
        tabHost.addTab(homeTabSpec);

        TabHost.TabSpec addTabSpec = tabHost.newTabSpec("add_tab_spec");
        addTabSpec.setContent(R.id.add);
        ImageView addImage = new ImageView(this);
        addImage.setImageResource(R.drawable.add);
        addTabSpec.setIndicator(addImage);
        tabHost.addTab(addTabSpec);

        TabHost.TabSpec profileTabSpec = tabHost.newTabSpec("profile_tab_spec");
        profileTabSpec.setContent(R.id.profile);
        ImageView profileImage = new ImageView(this);
        profileImage.setImageResource(R.drawable.profile);
        profileTabSpec.setIndicator(profileImage);
        tabHost.addTab(profileTabSpec);

        gameExistCheck();

        String[] gameKindArray = app.getUser().getGameKind().split(" ");
        ArrayList<Drawable> drawableList = new ArrayList<>();

        for (String gameKind : gameKindArray) {
            int id = getResources().getIdentifier(gameKind + "_icon", "drawable", getPackageName());
            drawableList.add(ResourcesCompat.getDrawable(getResources(), id, getTheme()));
        }

        recyclerView.setAdapter(new KindRecyclerAdapter(drawableList));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
                    outRect.right = 30;
                }
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

    private void gameExistCheck() {
        if (TextUtils.isEmpty(app.getUser().getGameKind())) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        app.saveUser();
    }

    public static class KindRecyclerAdapter extends RecyclerView.Adapter<KindRecyclerAdapter.ViewHolder> {

        private List<Drawable> drawableList;

        private KindRecyclerAdapter(ArrayList<Drawable> drawableList) {
            this.drawableList = drawableList;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.image_view_item)
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.imageView.setImageDrawable(drawableList.get(position));
        }

        @Override
        public int getItemCount() {
            return drawableList.size();
        }
    }
}