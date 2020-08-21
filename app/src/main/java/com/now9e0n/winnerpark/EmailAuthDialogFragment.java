package com.now9e0n.winnerpark;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmailAuthDialogFragment extends DialogFragment {

    @BindView(R.id.email_edit_text)
    EditText emailEditText;

    private String email;

    EmailAuthDialogFragment(String email) { this.email = email; }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getContext(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.email_auth_dialog, container);
        setCancelable(false);

        ButterKnife.bind(this, view);

        emailEditText.setText(email);

        return view;
    }
}