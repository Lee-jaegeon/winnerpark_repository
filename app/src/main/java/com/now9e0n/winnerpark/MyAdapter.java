package com.now9e0n.winnerpark;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public abstract class MyAdapter {

    public static class MyPagerAdapter extends PagerAdapter {
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

    public static class MainPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList;

        public MainPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);

            fragmentList = new ArrayList<>();
            fragmentList.add(new HomeFragment());
            fragmentList.add(new AddFragment());
            fragmentList.add(new ProfileFragment());
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

    public static class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

        private final List<Drawable> drawableList;

        public HomeRecyclerAdapter(ArrayList<Drawable> drawableList) {
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
}
