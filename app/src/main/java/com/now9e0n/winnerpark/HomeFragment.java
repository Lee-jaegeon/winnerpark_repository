package com.now9e0n.winnerpark;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.now9e0n.winnerpark.AppManager.getMyDrawable;

public class HomeFragment extends Fragment {

    private AppManager app;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        //init();

        return view;
    }

    private void init() {
        app = (AppManager) getActivity().getApplication();

        String[] gameKindArray = app.getUser().getGameKind().split(" ");
        ArrayList<Drawable> drawableList = new ArrayList<>();

        for (String gameKind : gameKindArray) {
            int drawable = getResources().getIdentifier("icon_" + gameKind, "drawable", getContext().getPackageName());
            drawableList.add(getMyDrawable(drawable));
        }

        recyclerView.setAdapter(new MyAdapter.HomeRecyclerAdapter(drawableList));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                if (parent.getItemDecorationCount() - parent.getChildAdapterPosition(view) > 1)
                    outRect.right = (int) (30 * AppManager.getDensityRatio());
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
}