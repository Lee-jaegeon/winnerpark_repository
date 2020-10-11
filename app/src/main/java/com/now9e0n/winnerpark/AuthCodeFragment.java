package com.now9e0n.winnerpark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mukesh.OtpView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthCodeFragment extends Fragment {

    @BindView(R.id.annotation_tv)
    TextView annotationTv;
    @BindView(R.id.otp_view)
    OtpView otpView;

    private String code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_code, container, false);
        ButterKnife.bind(this, view);

        init(view);

        return view;
    }

    private void init(View view) {
        view.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP ) view.performClick();
            return true;
        });

        if (getArguments() != null) {
            String address = getArguments().getString("address");
            code = getArguments().getString("code");

            String[] array = annotationTv.getText().toString().split("#");
            String text = array[0] + address + array[2];
            annotationTv.setText(text);
        }

        otpView.setOtpCompletionListener(otp -> {

        });
    }

    @OnClick(R.id.re_send_tv)
    void onReSendTvClicked() {

    }

    @OnClick(R.id.confirm_btn)
    void onConfirmBtnClicked() {

    }
}