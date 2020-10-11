package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.now9e0n.winnerpark.AppManager.getMyDrawable;

public class GameSelectorFragment extends Fragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_selector, container, false);
        ButterKnife.bind(this, view);

        LinkedHashMap<String, Drawable> gameInfoMap = new LinkedHashMap<>();
        for (int i = 1; i <= 22; i++) {
            String packageName = getContext().getPackageName();

            String gameName = getString(getResources().getIdentifier("game_name_" + i, "string", packageName));
            String fileGameName = gameName.replaceAll("[^a-z ]", "").replace(" ", "_").trim();

            int drawableId = getResources().getIdentifier(fileGameName, "drawable", packageName);
            Drawable gameDrawable = getMyDrawable(drawableId);

            gameInfoMap.put(gameName, gameDrawable);
        }

        recyclerView.setAdapter(new KindRecyclerAdapter(gameInfoMap));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                
            }
        });

        return view;
    }

    public static class KindRecyclerAdapter extends RecyclerView.Adapter<KindRecyclerAdapter.ViewHolder> {

        private Iterator<Map.Entry<String, Drawable>> gameInfoList;

        KindRecyclerAdapter(LinkedHashMap<String, Drawable> gameInfoMap) {
            this.gameInfoList = gameInfoMap.entrySet().iterator();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_imv)
            ImageView imageView;
            @BindView(R.id.item_tv)
            TextView textView;

            public ViewHolder(View itemView) {
                super(itemView);

                ButterKnife.bind(this, itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_game_selector, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull KindRecyclerAdapter.ViewHolder holder, int position) {
            Map.Entry<String, Drawable> entry = gameInfoList.next();

            holder.imageView.setImageDrawable(entry.getValue());
            holder.textView.setText(entry.getKey());
        }

        @Override
        public int getItemCount() {
            int count = 0;
            while (gameInfoList.hasNext()) count++;

            return count;
        }
    }
}