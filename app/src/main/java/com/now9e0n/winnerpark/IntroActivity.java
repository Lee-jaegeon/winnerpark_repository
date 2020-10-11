package com.now9e0n.winnerpark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.next_tv)
    TextView nextTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        init();
    }

    private void init() {
        ArrayList<Integer> layoutList = new ArrayList<>();
        for (int i = 1; i <= 5; i++)
            layoutList.add(getResources().getIdentifier("page_intro_" + i, "layout", getPackageName()));

        viewPager.setAdapter(new ViewPagerAdapter(getApplicationContext(), layoutList));
    }

    @OnClick(R.id.skip_tv)
    void onSkipTvClicked() {
        startLoginActivity();
    }

    @OnClick(R.id.next_tv)
    void onNextTvClicked() {
        int page = viewPager.getCurrentItem();

        if ((page + 1) < 5) {
            viewPager.setCurrentItem(++page);
            if ((page + 1) == 5) nextTv.setText("시작");
        }

        else startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);

        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private static class ViewPagerAdapter extends PagerAdapter {
        private Context context;
        private List<Integer> layoutList;

        private ViewPagerAdapter(Context context, ArrayList<Integer> layoutList) {
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
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return (view == o);
        }
    }
}