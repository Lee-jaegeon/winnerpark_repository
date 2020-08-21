package com.now9e0n.winnerpark;

import android.app.DatePickerDialog;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity_1 extends AppCompatActivity {

    @BindView(R.id.root_layout)
    ConstraintLayout layout;

    @BindView(R.id.name_input_edit_text)
    TextInputEditText nameEditText;
    @BindView(R.id.email_input_edit_text)
    TextInputEditText emailEditText;
    @BindView(R.id.birth_date_input_edit_text)
    TextInputEditText birthDateEditText;

    private boolean[] isInputComplete = new boolean[3];

    private SoftKeyboard softKeyboard;

    @BindView(R.id.next_button)
    ImageView nextImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_1);

        ButterKnife.bind(this);

        nameEditText.addTextChangedListener(getTextWatcher("name"));
        emailEditText.addTextChangedListener(getTextWatcher("email"));
        birthDateEditText.addTextChangedListener(getTextWatcher("bd"));

        InputMethodManager controlManager = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
        softKeyboard = new SoftKeyboard(birthDateEditText.getId(), layout, controlManager);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {
            @Override
            public void onSoftKeyboardHide() {

            }

            @Override
            public void onSoftKeyboardShow() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(SignUpActivity_1.this::showDatePickerDialog);
            }
        });
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
                Pattern pattern;

                switch (viewName) {
                    case "name": {
                        isInputComplete[0] = false;
                        pattern = Pattern.compile("^[가-힣]+$");

                        if (editable.length() == 0)
                            nameEditText.setError("이름을 입력해주세요.");

                        else if (editable.length() > 4)
                            nameEditText.setError("이름은 4글자 제한입니다.");

                        else if (!pattern.matcher(editable).matches())
                            nameEditText.setError("입력하신 이름의 형식이 틀렸습니다.");

                        else {
                            nameEditText.setError(null);

                            isInputComplete[0] = true;
                        }

                        break;
                    }

                    case "email": {
                        isInputComplete[1] = false;

                        pattern = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6}$");

                        if (editable.length() == 0)
                            emailEditText.setError("이메일을 입력해주세요.");

                        else if (!pattern.matcher(editable).matches())
                            emailEditText.setError("입력하신 이메일의 형식이 틀렸습니다.");

                        else {
                            emailEditText.setError(null);

                            isInputComplete[1] = true;
                        }

                        break;
                    }

                    case "bd": {
                        isInputComplete[2] = false;

                        pattern = Pattern.compile("^[0-9]{8}$");

                        if (editable.length() == 0)
                            birthDateEditText.setError("생년월일을 입력해주세요.");

                        else if (!pattern.matcher(editable).matches())
                            birthDateEditText.setError("입력하신 생년월일의 형식이 틀렸습니다.");

                        else {
                            birthDateEditText.setError(null);

                            isInputComplete[2] = true;
                        }
                    };
                }

                if (isInputComplete[0] && isInputComplete[1] && isInputComplete[2])
                    nextImageButton.setVisibility(View.VISIBLE);

                else nextImageButton.setVisibility(View.INVISIBLE);
            }
        };
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this, (datePicker, year, month, day) -> {
            String monthText = String.format(Locale.US, "%02d", month + 1);
            String dayText = String.format(Locale.US, "%02d", day);
            String date = year + monthText + dayText;
            birthDateEditText.setText(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();

        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    @OnClick(R.id.email_auth_button)
    void onEmailAuthButtonClicked() {
        String email = emailEditText.getText().toString();
        new EmailAuthDialogFragment(email).show(getSupportFragmentManager(), "dialog_event");
    }

    @OnClick(R.id.next_button)
    void onNextButtonClicked() {
        Intent intent = new Intent(this, SignUpActivity_2.class);
        intent.putExtra("name", nameEditText.getText().toString());
        intent.putExtra("email", emailEditText.getText().toString());
        intent.putExtra("birth_date", birthDateEditText.getText().toString());
        startActivity(intent);
    }
}