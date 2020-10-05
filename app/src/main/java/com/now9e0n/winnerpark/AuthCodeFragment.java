package com.now9e0n.winnerpark;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthCodeFragment extends Fragment {

    @BindView(R.id.annotation_text_view)
    TextView annotationTextView;
    @BindView(R.id.code_edit)
    EditText codeEdit;

    private String code;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_auth_code, container, false);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            String address = getArguments().getString("address");
            code = getArguments().getString("code");

            String[] array = annotationTextView.getText().toString().split("#");
            String text = array[0] + address + array[2];
            annotationTextView.setText(text);
        }

        return view;
    }

    @OnClick(R.id.confirm_button)
    void onConfirmButtonClicked() {

    }
}