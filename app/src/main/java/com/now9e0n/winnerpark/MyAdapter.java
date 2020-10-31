package com.now9e0n.winnerpark;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.now9e0n.winnerpark.AppManager.getMyColor;

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

    public static class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();

        public MainFragmentPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment) {
            fragmentList.add(fragment);
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

        private static final int ITEM_ADD = 1;
        private static final int ITEM_NORMAL = 2;

        private final AppManager app;

        private final List<Drawable> drawableList = new ArrayList<>();
        private final Consumer<Integer> itemClickConsumer;
        @Getter @Setter private int currentItem;

        public HomeRecyclerAdapter(AppManager app, Consumer<Integer> itemClickConsumer) {
            this.app = app;
            this.itemClickConsumer = itemClickConsumer;
        }

        public void addDrawable(Drawable drawable) {
            drawableList.add(drawable);
        }

        public int getListSize() {
            return drawableList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item_imv)
            ImageView itemImv;
            private ImageView removeImv;
            @Getter private ImageView focusImv;

            public ViewHolder(View itemView, int type) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnClickListener((view) -> itemClickConsumer.accept(getAdapterPosition()));

                if (type == ITEM_NORMAL) {
                    removeImv = itemView.findViewById(R.id.remove_imv);
                    focusImv = itemView.findViewById(R.id.focus_imv);

                    itemView.setOnLongClickListener(view -> {
                        if (removeImv.getVisibility() == View.GONE) {
                            itemImv.setColorFilter(getMyColor(R.color.dark_gray), PorterDuff.Mode.MULTIPLY);
                            removeImv.setVisibility(View.VISIBLE);
                        }

                        else if (removeImv.getVisibility() == View.VISIBLE) {
                            itemImv.setColorFilter(null);
                            removeImv.setVisibility(View.GONE);
                        }

                        return false;
                    });

                    removeImv.setOnClickListener(view -> {
                        int pos = getAdapterPosition();

                        app.getUser().getGameKindList().remove(pos);
                        drawableList.remove(pos);
                        notifyItemRemoved(pos);
                    });
                }
            }
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = 0;
            if (viewType == ITEM_ADD) layout = R.layout.item_icon_home_add;
            if (viewType == ITEM_NORMAL) layout = R.layout.item_icon_home_normal;

            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

            return new ViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemImv.setImageDrawable(drawableList.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return ITEM_ADD;
            else return ITEM_NORMAL;
        }

        @Override
        public int getItemCount() {
            return drawableList.size();
        }
    }
}
