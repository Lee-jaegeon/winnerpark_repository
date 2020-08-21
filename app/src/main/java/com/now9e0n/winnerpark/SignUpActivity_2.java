package com.now9e0n.winnerpark;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity_2 extends AppCompatActivity {

    private AppManager app;

    private String name, email, birthDate;

    @BindView(R.id.id_input_edit_text)
    TextInputEditText idEditText;
    @BindView(R.id.password_input_edit_text)
    TextInputEditText passwordEditText;
    @BindView(R.id.password_confirm__input_edit_text)
    TextInputEditText passwordConfirmEditText;

    private boolean[] isInputComplete = new boolean[3];

    @BindView(R.id.sign_up_button)
    ImageView signUpImageButton;

    boolean isSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_2);

        ButterKnife.bind(this);

        app = (AppManager) getApplication();

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        birthDate = getIntent().getStringExtra("birth_date");

        idEditText.addTextChangedListener(getTextWatcher("id"));
        passwordEditText.addTextChangedListener(getTextWatcher("password"));
        passwordConfirmEditText.addTextChangedListener(getTextWatcher("passwordConfirm"));
    }

    private TextWatcher getTextWatcher(String viewName) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9]+$");

                switch (viewName) {
                    case "id": {
                        isInputComplete[0] = false;

                        if (editable.length() == 0)
                            idEditText.setError("아이디를 입력해주세요.");

                        else if (editable.length() < 6)
                            idEditText.setError("아이디는 6글자 이상이어야 합니다.");

                        else if (editable.length() > 10)
                            idEditText.setError("아이디는 10글자 제한입니다.");

                        else if (!pattern.matcher(editable).matches())
                            idEditText.setError("아이디는 영문과 숫자로만 입력해주세요.");

                        else {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_list");
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                @SuppressWarnings("unchecked")
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Map<String, String>> userMap = (Map<String, Map<String, String>>) snapshot.getValue();
                                    if (userMap != null) {
                                        for (Map<String, String> user : userMap.values()) {
                                            if (user.get("id").equals(idEditText.getText().toString()))
                                                idEditText.setError("이미 존재하는 아이디입니다.");

                                            else {
                                                idEditText.setError(null);

                                                isInputComplete[0] = true;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.w("Firebase", "Value Event Listen Cancelled", error.toException());
                                }
                            });
                        }

                        break;
                    }

                    case "password": {
                        isInputComplete[1] = false;

                        if (editable.length() == 0)
                            passwordEditText.setError("패스워드를 입력해주세요.");

                        else if (editable.length() < 8)
                            passwordEditText.setError("비밀번호는 8글자 이상이어야 합니다.");

                        else if (editable.length() > 16)
                            passwordEditText.setError("패스워드는 16글자 제한입니다.");

                        else if (!pattern.matcher(editable).matches())
                            passwordEditText.setError("패스워드는 영문과 숫자로만 입력해주세요.");

                        else {
                            passwordEditText.setError(null);

                            isInputComplete[1] = true;
                        }

                        break;
                    }

                    case "passwordConfirm": {
                        isInputComplete[2] = false;

                        if (editable.length() == 0)
                            passwordConfirmEditText.setError("패스워드를 입력해주세요.");

                        else if (editable.length() < 8)
                            passwordConfirmEditText.setError("비밀번호는 8글자 이상이어야 합니다.");

                        else if (editable.length() > 16)
                            passwordConfirmEditText.setError("패스워드는 16글자 제한입니다.");

                        else if (!pattern.matcher(editable).matches())
                            passwordConfirmEditText.setError("패스워드는 영문과 숫자로만 입력해주세요.");

                        else if (!passwordEditText.getText().toString()
                                .equals(passwordConfirmEditText.getText().toString()))
                            passwordConfirmEditText.setError("패스워드와 일치하지 않습니다.");

                        else {
                            passwordConfirmEditText.setError(null);

                            isInputComplete[2] = true;
                        }

                        break;
                    }
                }

                if (isInputComplete[0] && isInputComplete[1] && isInputComplete[2])
                    signUpImageButton.setVisibility(View.VISIBLE);

                else signUpImageButton.setVisibility(View.INVISIBLE);
            }
        };
    }

    @OnClick(R.id.sign_up_button)
    void onSignUpButtonClicked() {
        if (!isSignUp) {
            UserModel userModel = UserModel.builder()
                    .name(name)
                    .email(email)
                    .birthDate(birthDate)
                    .id(idEditText.getText().toString())
                    .password(passwordEditText.getText().toString())
                    .build();

            app.setUser(userModel);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user_list");
            reference.push().setValue(userModel);
            isSignUp = true;

            nextActivity();
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }
}