package com.now9e0n.winnerpark;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.now9e0n.winnerpark.AppManager.getCurrentDate;
import static com.now9e0n.winnerpark.AppManager.isTextEqual;

public class CreatePasswordFragment extends Fragment {

    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.confirm_password_et)
    EditText confirmPasswordEt;

    private String address;
    private String type;

    private DatabaseReference reference;

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    private Animator.AnimatorListener animatorListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_password, container, false);
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
            address = getArguments().getString("address");
            type = getArguments().getString("type");
        }

        View.OnFocusChangeListener listener = (v, hasFocus) -> {
            TextInputLayout layout = getTextLayout(v);

            if (hasFocus) layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else layout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        };

        passwordEt.setOnFocusChangeListener(listener);
        confirmPasswordEt.setOnFocusChangeListener(listener);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (getActivity().getCurrentFocus() == passwordEt)
                    getTextLayout(passwordEt).setError("");

                if (getActivity().getCurrentFocus() == confirmPasswordEt)
                    getTextLayout(confirmPasswordEt).setError("");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        passwordEt.addTextChangedListener(textWatcher);
        confirmPasswordEt.addTextChangedListener(textWatcher);

        reference = FirebaseDatabase.getInstance().getReference("user_list");

        animationView.setAnimation("animation_check.json");
        animationView.setRepeatCount(1);
        animatorListener = new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        startMainActivity();
                    }
                }, 500);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        };
    }

    private TextInputLayout getTextLayout(View view) {
        return (TextInputLayout) view.getParent().getParent();
    }

    @OnClick(R.id.complete_btn)
    void onCompleteBtnClicked() {
        boolean isPasswordEmpty, isConfirmPasswordEmpty;
        isPasswordEmpty = TextUtils.isEmpty(passwordEt.getEditableText());
        isConfirmPasswordEmpty = TextUtils.isEmpty(confirmPasswordEt.getEditableText());

        if (isPasswordEmpty) getTextLayout(passwordEt).setError("입력란을 채워주세요 :)");
        if (isConfirmPasswordEmpty) getTextLayout(confirmPasswordEt).setError("입력란을 채워주세요 :)");

        if (!isPasswordEmpty && !isConfirmPasswordEmpty) {
            if (passwordEt.getEditableText().length() <= 12) {
                if (isTextEqual(passwordEt, confirmPasswordEt)) {
                    User user = User.builder()
                            .createdDate(getCurrentDate(String.class))
                            .password(passwordEt.getEditableText().toString()).build();

                    if (type.equals("phone")) user.setPhoneNumber(address);
                    if (type.equals("email")) user.setEmail(address);

                    ((AppManager) getContext()).setUser(user);
                    reference.push().setValue(user);

                    Snackbar.make(getView(), "계정 생성이 완료되었습니다.", Snackbar.LENGTH_LONG).show();
                    animationView.setVisibility(View.VISIBLE);
                    animationView.addAnimatorListener(animatorListener);
                    animationView.playAnimation();
                }

                else getTextLayout(confirmPasswordEt).setError("패스워드가 일치하지 않습니다.");
            }

            else getTextLayout(passwordEt).setError("비밀번호는 12자 내외로 정해주세요 :)");
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }
}