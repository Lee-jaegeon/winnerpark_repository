package com.now9e0n.winnerpark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreatePasswordFragment extends Fragment {

    @BindView(R.id.password_et)
    EditText passwordEt;
    @BindView(R.id.confirm_password_et)
    EditText confirmPasswordEt;

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

        View.OnFocusChangeListener listener = (v, hasFocus) -> {
            TextInputLayout layout = (TextInputLayout) v.getParent().getParent();

            if (hasFocus) layout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else layout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        };

        passwordEt.setOnFocusChangeListener(listener);
        confirmPasswordEt.setOnFocusChangeListener(listener);
    }

    @OnClick(R.id.complete_btn)
    void onCompleteBtnClicked() {

    }
}