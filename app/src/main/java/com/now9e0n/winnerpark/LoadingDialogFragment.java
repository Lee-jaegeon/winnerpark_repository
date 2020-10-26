package com.now9e0n.winnerpark;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingDialogFragment extends DialogFragment {

    @BindView(R.id.loading_imv)
    ImageView loadingImv;
    @BindView(R.id.loading_tv)
    TextView loadingTv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading_dialog, container, false);
        ButterKnife.bind(this, view);

        init();

        return view;
    }

    private void init() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);

        ((AnimationDrawable) loadingImv.getDrawable()).start();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> updateLoadingTv());
            }
        }, 0, 750);
    }

    private void updateLoadingTv() {
        String text = loadingTv.getText().toString();
        int dotCount = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '.') dotCount++;
        }

        if (dotCount < 3) text = text.concat(".");
        else text = text.substring(0, text.length() - 2);

        loadingTv.setText(text);
    }
}