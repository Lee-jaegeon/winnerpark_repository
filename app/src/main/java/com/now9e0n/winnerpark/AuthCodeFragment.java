package com.now9e0n.winnerpark;

import android.app.AlertDialog;
import android.content.Intent;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OtpView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import lombok.SneakyThrows;

public class AuthCodeFragment extends Fragment {

    private MSGSignUpActivity activity;

    private SMSReceiver smsReceiver;

    @BindView(R.id.subtitle_tv)
    TextView subtitleTv;
    @BindView(R.id.otp_view)
    OtpView otpView;
    @BindView(R.id.error_tv)
    TextView errorTv;
    @BindView(R.id.re_send_tv)
    TextView reSendTv;

    private String address;
    private String type;
    private String sentCode;
    private String inputCode;

    @Getter private String timer;

    private DatabaseReference reference;
    private AlertDialog alertDialog;

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

        if (getArguments() != null) {
            address = getArguments().getString("address");
            sentCode = getArguments().getString("code");
            type = getArguments().getString("type");

            String[] array = subtitleTv.getText().toString().split("#");
            String text = array[0] + address + array[2];
            subtitleTv.setText(text);
        }

        otpView.setOtpCompletionListener(otp -> inputCode = otp);
        spanString();

        reference = FirebaseDatabase.getInstance().getReference("user_list");
        alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("회원가입")
                .setMessage("이미 계정이 존재합니다.")
                .setCancelable(false)
                .setPositiveButton("확인", (dialog, id) -> {
                    dialog.dismiss();
                    Snackbar.make(getView(), "로그인 화면으로 이동합니다 :)", Snackbar.LENGTH_LONG).show();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .create();
    }

    public void startSMSReceiver() {
        SmsRetriever.getClient(getContext()).startSmsRetriever();
        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        smsReceiver = new SMSReceiver(code -> otpView.setText(code));
        activity.registerReceiver(smsReceiver, intentFilter);
    }

    public void spanString() {
        SimpleDateFormat format = new SimpleDateFormat("m분 ss초", Locale.KOREA);
        timer = format.format(new Date(activity.getDeltaTime()));

        String content = reSendTv.getText().toString();
        content = content.replaceAll("\\d{1,2}분 \\d{1,2}초", timer);

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new RelativeSizeSpan(0.5f), content.indexOf("("), content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        reSendTv.setText(spannableString);
    }

    @OnClick(R.id.re_send_tv)
    void onReSendTvClicked() {
        if (activity.getDeltaTime() == 0) {
            sendCode();
            if (smsReceiver != null) SmsRetriever.getClient(getContext()).startSmsRetriever();
        }

        else Toast.makeText(getContext(), "인증코드 재전송 : " + timer + " 남음", Toast.LENGTH_SHORT).show();
    }

    private void sendCode() {
        if (type.equals("phone")) {
            SendSMSClient client = SendSMSClient.getInstance();
            client.sendSMS(address, () -> {
                sentCode = client.getCode();

                Snackbar.make(getView(), "휴대폰 인증 코드를 전송하였습니다.", Snackbar.LENGTH_LONG).show();
            });
        }

        if (type.equals("email")) {
            GMailSender gMailSender = GMailSender.getInstance();
            gMailSender.sendMail(address, () -> {
                sentCode = gMailSender.getEmailCode();

                Snackbar.make(getView(), "이메일 인증 코드를 전송하였습니다.", Snackbar.LENGTH_LONG).show();
            });
        }
    }

    @OnClick(R.id.confirm_tv)
    void onConfirmTvClicked() {
        if (inputCode.equals(sentCode)) {
            LoadingDialogFragment loadingFragment = new LoadingDialogFragment();
            loadingFragment.show(getParentFragmentManager(), null);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @SneakyThrows
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Map<String, String>> userList = (Map<String, Map<String, String>>) snapshot.getValue();
                    for (Map<String, String> userData : userList.values()) {
                        User user = User.getUserBySnapshot(userData);
                        if (user.getPhoneNumber().equals(address) || user.getEmail().equals(address)) {
                            loadingFragment.dismiss();
                            alertDialog.show();
                            return;
                        }
                    }

                    loadingFragment.dismiss();
                    Snackbar.make(getView(), "인증에 성공하였습니다.", Snackbar.LENGTH_LONG).show();
                    startCreatePasswordFragment();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Value Event Listen Cancelled", error.toException());
                }
            });
        }

        else showErrorTv();
    }

    private void showErrorTv() {
        errorTv.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                errorTv.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        AnimationSet animation = new AnimationSet(false);
        animation.setDuration(1000);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);

        errorTv.setAnimation(animation);
    }

    private void startCreatePasswordFragment() {
        CreatePasswordFragment fragment = new CreatePasswordFragment();
        Bundle bundle = new Bundle();
        bundle.putString("address", address);
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        activity.addFragment(fragment, "create_password");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (smsReceiver != null) activity.unregisterReceiver(smsReceiver);
        smsReceiver = null;
    }
}