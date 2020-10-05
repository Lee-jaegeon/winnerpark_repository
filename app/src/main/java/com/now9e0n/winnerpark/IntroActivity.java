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

import static com.now9e0n.winnerpark.AppManager.getDensityRatio;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.circle_indicator)
    CircleIndicator circleIndicator;
    @BindView(R.id.next_text_view)
    TextView nextTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ButterKnife.bind(this);

        ArrayList<Integer> layoutList = new ArrayList<>();
        for (int i = 1; i <= 5; i++)
            layoutList.add(getResources().getIdentifier("intro_page_" + i, "layout", getPackageName()));

        viewPager.setAdapter(new ViewPagerAdapter(getApplicationContext(), layoutList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                circleIndicator.selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        circleIndicator.setItemMargin((int) (5 * getDensityRatio(this)));
        circleIndicator.setAnimDuration(300);
        circleIndicator.createDot(layoutList.size(), R.drawable.round_cell, R.drawable.round_cell_selected);
    }

    @OnClick(R.id.skip_text_view)
    public void onSkipTextViewClicked() {
        startNextActivity();
    }

    @OnClick(R.id.next_text_view)
    public void onNextTextViewClicked() {
        int page = viewPager.getCurrentItem();

        if ((page + 1) < 5) {
            viewPager.setCurrentItem(++page);
            if ((page + 1) == 5) nextTextView.setText("시작");
        }

        else startNextActivity();
    }

    private void startNextActivity() {
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

    public static class ViewPagerAdapter extends PagerAdapter {
        private Context context;
        private List<Integer> layoutList;

        ViewPagerAdapter(Context context, ArrayList<Integer> layoutList) {
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