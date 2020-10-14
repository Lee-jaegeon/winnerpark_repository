package com.now9e0n.winnerpark;

import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.mukesh.OtpView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthCodeFragment extends Fragment {

    private MSGSignUpActivity activity;

    private SMSReceiver smsReceiver;

    @BindView(R.id.annotation_tv)
    TextView annotationTv;
    @BindView(R.id.otp_view)
    OtpView otpView;
    @BindView(R.id.re_send_tv)
    TextView reSendTv;

    private String sentCode;
    private String inputCode;

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

        activity = ((MSGSignUpActivity) getActivity());

        SmsRetrieverClient client = SmsRetriever.getClient(getContext());
        Task<Void> task = client.startSmsRetriever();
        smsReceiver = new SMSReceiver(code -> otpView.setText(code));

        task.addOnSuccessListener(aVoid -> {
            IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
            activity.registerReceiver(smsReceiver, intentFilter);
        });
        task.addOnFailureListener(e -> Log.e("SMS Retriever", "Start SMSRetriever Failed", e));

        if (getArguments() != null) {
            String address = getArguments().getString("address");
            sentCode = getArguments().getString("code");

            String[] array = annotationTv.getText().toString().split("#");
            String text = array[0] + address + array[2];
            annotationTv.setText(text);
        }

        spanString();

        otpView.setOtpCompletionListener(otp -> inputCode = otp);
    }

    private void spanString() {
        String content = reSendTv.getText().toString();
        content = content.replaceAll("\\d", String.valueOf(activity.reSendCount));

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new RelativeSizeSpan(0.5f), content.indexOf("("), content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        reSendTv.setText(spannableString);
    }

    @OnClick(R.id.re_send_tv)
    void onReSendTvClicked() {
        if (activity.reSendCount > 0) {
            sentCode = activity.sendCode();
            --activity.reSendCount;
            spanString();
        }
    }

    @OnClick(R.id.confirm_tv)
    void onConfirmTvClicked() {
        if (inputCode.equals(sentCode)) {
            Snackbar.make(getView(), "인증에 성공하였습니다.", Snackbar.LENGTH_LONG).show();
            activity.addFragment(new CreatePasswordFragment(), "create_password");
        }

        else Toast.makeText(getContext(), "인증코드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        activity.unregisterReceiver(smsReceiver);
    }
}