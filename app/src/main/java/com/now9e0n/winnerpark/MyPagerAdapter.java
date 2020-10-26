package com.now9e0n.winnerpark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MyPagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<Integer> layoutList;

    public MyPagerAdapter(Context context, ArrayList<Integer> layoutList) {
        this.context = context;
        this.layoutList = layoutList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(layoutList.get(position), null);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return layoutList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == o);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}