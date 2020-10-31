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

import static com.now9e0n.winnerpark.AppManager.getReSizedDrawable;

public class HomeFragment extends Fragment {

    private AppManager app;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

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

            if (position == 0) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                transaction.add(new GameSelectionFragment(), "game_selection").commit();
            }

            else if (position != adapter.getCurrentItem()) {
                ViewHolder viewHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(adapter.getCurrentItem());
                viewHolder.getFocusImv().setVisibility(View.GONE);

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
                int drawable = getResources().getIdentifier("icon_" + gameKind, "drawable", getContext().getPackageName());
                adapter.addDrawable(getReSizedDrawable(drawable, 60, 60));
            }
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemViewCacheSize(adapter.getListSize());
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

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adapter.setCurrentItem(1);
                recyclerView.getLayoutManager().findViewByPosition(1).performClick();

                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}