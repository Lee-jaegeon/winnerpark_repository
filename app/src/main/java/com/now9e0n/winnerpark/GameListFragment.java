package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.now9e0n.winnerpark.MyAdapter.GameListRecyclerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.now9e0n.winnerpark.AppManager.getIdentifier;
import static com.now9e0n.winnerpark.AppManager.getReSizedDrawable;

public class GameListFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_list, container, false);
        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        GameListRecyclerAdapter adapter = new GameListRecyclerAdapter();
        int arrayId = getIdentifier("game_name", "array");
        String[] gameNameArray = getResources().getStringArray(arrayId);

        for (String gameName : gameNameArray) {
            String resourceName = gameName.toLowerCase().replaceAll("[^a-z ]", "").replace(" ", "_");
            int drawableId = getIdentifier(resourceName, "drawable");
            Drawable drawable = getReSizedDrawable(drawableId, 180, 240);

            adapter.addPair(drawable, gameName);
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.getRecycledViewPool().setMaxRecycledViews(MyAdapter.NORMAL_ITEM_TYPE, 0);
        recyclerView.setItemViewCacheSize(adapter.getItemCount());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = (int) (10 * AppManager.getDensityRatio());
            }
        });
    }
}