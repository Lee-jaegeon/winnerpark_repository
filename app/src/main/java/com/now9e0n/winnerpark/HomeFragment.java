package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.now9e0n.winnerpark.MyAdapter.HomeRecyclerAdapter;
import com.now9e0n.winnerpark.MyAdapter.HomeRecyclerAdapter.ViewHolder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;

import static com.now9e0n.winnerpark.AppManager.getIdentifier;
import static com.now9e0n.winnerpark.AppManager.getReSizedDrawable;

public class HomeFragment extends Fragment {

    private AppManager app;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    private ViewHolder viewHolder;

    @Getter private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        app = (AppManager) getActivity().getApplication();

        Consumer<Integer> itemClickConsumer = position -> {
            HomeRecyclerAdapter adapter = (HomeRecyclerAdapter) recyclerView.getAdapter();
            int current = adapter.getCurrentItem();

            if (position == 0) {
                getParentFragmentManager().beginTransaction().show(fragment).commit();
                ((MainActivity) getActivity()).applyTheme(true);
            }

            else if (position != current) {
                if (viewHolder != null) viewHolder.getFocusImv().setVisibility(View.GONE);

                viewHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                viewHolder.getFocusImv().setVisibility(View.VISIBLE);
                adapter.setCurrentItem(position);
            }
        };

        HomeRecyclerAdapter adapter = new HomeRecyclerAdapter(app, itemClickConsumer);
        adapter.addDrawable(getReSizedDrawable(R.drawable.icon_add, 60, 60));

        if (app.getUser().getGameKindList() != null) {
            List<String> gameKindArray = app.getUser().getGameKindList();

            for (String gameKind : gameKindArray) {
                gameKind = gameKind.toLowerCase().replaceAll("[^a-z ]", "").replace(" ", "_");
                int drawable = getIdentifier("icon_" + gameKind, "drawable");
                adapter.addDrawable(getReSizedDrawable(drawable, 60, 60));
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.getRecycledViewPool().setMaxRecycledViews(MyAdapter.NORMAL_ITEM_TYPE, 0);
        recyclerView.setItemViewCacheSize(adapter.getItemCount());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                int pos = parent.getChildAdapterPosition(view);
                int count = parent.getAdapter().getItemCount();
                int size = (int) (10 * AppManager.getDensityRatio());

                if (pos == 0) outRect.left = size;
                if (count - pos == 1) outRect.right = size;
                if (count - pos > 1) outRect.right = size;
            }
        });

        if (recyclerView.getAdapter().getItemCount() > 1) {
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    recyclerView.findViewHolderForAdapterPosition(1).itemView.performClick();
                    adapter.setCurrentItem(1);

                    recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        fragment = new GameListFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_layout, fragment).hide(fragment).commit();
    }
}