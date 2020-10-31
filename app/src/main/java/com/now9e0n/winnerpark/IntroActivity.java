package com.now9e0n.winnerpark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.now9e0n.winnerpark.AppManager.activityWindowSet;

public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    @BindView(R.id.next_tv)
    TextView nextTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        activityWindowSet(this);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ArrayList<Integer> layoutList = new ArrayList<>();
        for (int i = 1; i <= 5; i++)
            layoutList.add(getResources().getIdentifier("page_intro_" + i, "layout", getPackageName()));

        viewPager.setAdapter(new MyAdapter.MyPagerAdapter(getApplicationContext(), layoutList));
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
}