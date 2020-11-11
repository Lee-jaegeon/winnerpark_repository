package com.now9e0n.winnerpark;

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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.emredavarci.noty.Noty;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OtpView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import lombok.Getter;
import lombok.SneakyThrows;

import static com.now9e0n.winnerpark.AppManager.animEndListener;
import static com.now9e0n.winnerpark.AppManager.isTextEqual;

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

    @BindView(R.id.root_layout)
    RelativeLayout layout;
    private Noty dialog;

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

        String message = "이미 계정이 존재합니다.\n\n현재 가입되어 있는 핸드폰 번호나\n이메일을 사용하여 로그인해주세요 :)";
        dialog = Noty.init(getContext(), message, layout, Noty.WarningStyle.ACTION)
                .hasShadow(true)
                .setActionText("확인")
                .setActionTextColor("#FFFFFF")
                .setActionFont("arita_b.otf")
                .setActionTextSizeSp(20)
                .setWarningTextFont("arita_n.otf")
                .setWarningTextSizeSp(17)
                .setWarningBoxBgColor("#9147FF")
                .setWarningBoxRadius(10,10,10,10)
                .setWarningBoxPosition(Noty.WarningPos.CENTER)
                .setWarningBoxMargins(40,10,40,10)
                .setHeight(new RelativeLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT))
                .setAnimation(Noty.RevealAnim.FADE_IN, Noty.DismissAnim.FADE_OUT, 500,500)
                .setTapListener(warning -> {
                    Toasty.normal(getContext(), "로그인 화면으로 이동합니다", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
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

        else Toasty.info(getContext(), "인증코드 재전송 : " + timer + " 남음", Toast.LENGTH_SHORT, true).show();
    }

    private void sendCode() {
        if (type.equals("phone")) {
            SendSMSClient client = SendSMSClient.getInstance();
            client.sendSMS(address, () -> {
                sentCode = client.getCode();

                Toasty.success(getContext(), "휴대폰 인증 코드를 전송하였습니다.", Toast.LENGTH_SHORT).show();
            });
        }

        if (type.equals("email")) {
            GMailSender gMailSender = GMailSender.getInstance();
            gMailSender.sendMail(address, () -> {
                sentCode = gMailSender.getEmailCode();

                Toasty.success(getContext(), "이메일 인증 코드를 전송하였습니다.", Toast.LENGTH_SHORT).show();
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
                    List<User> userList = User.getUserBySnapshot(snapshot);
                    for (User user : userList) {
                        if (isTextEqual(address, user.getPhoneNumber(), user.getEmail())) {
                            loadingFragment.dismiss();
                            dialog.show();
                            return;
                        }
                    }

                    loadingFragment.dismiss();
                    Toasty.success(getContext(), "인증에 성공하였습니다.", Toast.LENGTH_SHORT).show();
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
        fadeOut.setAnimationListener(animEndListener(() -> errorTv.setVisibility(View.INVISIBLE)));

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