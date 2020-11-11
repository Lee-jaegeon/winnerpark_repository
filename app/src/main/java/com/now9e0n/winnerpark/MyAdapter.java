package com.now9e0n.winnerpark;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

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
import static com.now9e0n.winnerpark.AppManager.animEndListener;
import static com.now9e0n.winnerpark.AppManager.getHashCode;
import static com.now9e0n.winnerpark.AppManager.getMyColor;

public abstract class MyAdapter {

    public static final int NORMAL_ITEM_TYPE = getHashCode("NORMAL_ITEM_TYPE");

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

        public static final int ADD_ITEM_TYPE = getHashCode("ADD_ITEM_TYPE");

        private final AppManager app;

        private final List<Drawable> drawableList = new ArrayList<>();
        private final Consumer<Integer> itemClickConsumer;
        @Getter @Setter private int currentItem = 0;

        public HomeRecyclerAdapter(AppManager app, Consumer<Integer> itemClickConsumer) {
            this.app = app;
            this.itemClickConsumer = itemClickConsumer;
        }

        public void addDrawable(Drawable drawable) {
            drawableList.add(drawable);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.icon_imv)
            ImageView iconImv;
            private ImageView removeImv;
            @Getter private ImageView focusImv;

            private final Rect viewRect = new Rect();
            private boolean isCanceled;
            private boolean isLongClicked;

            public ViewHolder(View itemView, int type) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                itemView.setOnTouchListener(getViewTouchListener());

                if (type == NORMAL_ITEM_TYPE) {
                    removeImv = itemView.findViewById(R.id.remove_imv);
                    focusImv = itemView.findViewById(R.id.focus_imv);

                    itemView.setOnLongClickListener(view -> {
                        if (removeImv.getVisibility() == View.GONE) {
                            iconImv.setColorFilter(getMyColor(R.color.dark_gray), PorterDuff.Mode.MULTIPLY);
                            removeImv.setVisibility(View.VISIBLE);
                        }

                        else if (removeImv.getVisibility() == View.VISIBLE) {
                            iconImv.setColorFilter(null);
                            removeImv.setVisibility(View.GONE);
                        }

                        isLongClicked = true;
                        return false;
                    });

                    removeImv.setOnClickListener(view -> {
                        int pos = getAdapterPosition();

                        app.getUser().getGameKindList().remove(pos);
                        drawableList.remove(pos);
                        notifyItemRemoved(pos);

                        if (getAdapterPosition() == currentItem) currentItem = 0;
                    });
                }
            }

            private View.OnTouchListener getViewTouchListener() {
                return (view, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN : {
                            view.getHitRect(viewRect);
                            isCanceled = false;

                            animate(view, R.anim.scale_down, () -> {});
                            break;
                        }

                        case MotionEvent.ACTION_MOVE : {
                            int x = view.getLeft() + (int) event.getX();
                            int y = view.getTop() + (int) event.getY();

                            if (!viewRect.contains(x, y) && !isCanceled) {
                                animate(view, R.anim.scale_up, () -> {
                                    viewRect.setEmpty();
                                    isCanceled = true;
                                    isLongClicked = false;
                                });

                                view.clearAnimation();
                            }
                            break;
                        }

                        case MotionEvent.ACTION_UP : {
                            animate(view, R.anim.scale_up, () -> {
                                if (!isLongClicked) {
                                    itemClickConsumer.accept(getAdapterPosition());
                                    view.performClick();
                                    isLongClicked = false;
                                }
                            });

                            view.clearAnimation();
                            break;
                        }
                    }

                    return false;
                };
            }

            private void animate(View view, int animRes, Runnable animEndRunnable) {
                Animation animation = AnimationUtils.loadAnimation(app.getApplicationContext(), animRes);
                animation.setAnimationListener(animEndListener(animEndRunnable));
                animation.setFillAfter(true);
                view.startAnimation(animation);
            }
        }

        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = 0;
            if (viewType == ADD_ITEM_TYPE) layout = R.layout.item_icon_home_add;
            if (viewType == NORMAL_ITEM_TYPE) layout = R.layout.item_icon_home_normal;

            View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new ViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.iconImv.setImageDrawable(drawableList.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) return ADD_ITEM_TYPE;
            else return NORMAL_ITEM_TYPE;
        }

        @Override
        public int getItemCount() {
            return drawableList.size();
        }
    }

    public static class GameListRecyclerAdapter extends RecyclerView.Adapter<GameListRecyclerAdapter.ViewHolder> {

        private final List<Pair<Drawable, String>> gameList = new ArrayList<>();

        public void addPair(Drawable drawable, String name) {
            gameList.add(Pair.create(drawable, name));
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.game_imv)
            ImageView gameImv;
            @BindView(R.id.name_tv)
            TextView nameTv;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_list, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.gameImv.setImageDrawable(gameList.get(position).first);
            holder.nameTv.setText(gameList.get(position).second);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return NORMAL_ITEM_TYPE;
        }

        @Override
        public int getItemCount() {
            return gameList.size();
        }
    }
}
