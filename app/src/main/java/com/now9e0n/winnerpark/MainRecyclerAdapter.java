package com.now9e0n.winnerpark;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private final List<Drawable> drawableList;

    public MainRecyclerAdapter(ArrayList<Drawable> drawableList) {
        this.drawableList = drawableList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_imv)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_main, parent, false);

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