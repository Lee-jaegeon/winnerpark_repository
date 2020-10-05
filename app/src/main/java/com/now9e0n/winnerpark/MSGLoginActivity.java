package com.now9e0n.winnerpark;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MSGLoginActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_HINT = 101;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.phone_number_edit)
    EditText phoneNumberEdit;
    @BindView(R.id.email_edit)
    EditText emailEdit;

    @BindView(R.id.send_code_button)
    Button sendCodeButton;

    private String phoneNumber;
    private String phoneNumberCode;
    private String email;
    private String emailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_back);

        GoogleApiClient apiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, 0, null)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        phoneNumberEdit.setOnFocusChangeListener((view, hasFocus) -> {
            if (phoneNumberEdit.getEditableText().length() == 0) {
                if (hasFocus) {
                    PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
                    try {
                        startIntentSenderForResult(intent.getIntentSender(), REQUEST_PHONE_HINT, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e("Phone", "Request Phone Number Failed", e);
                    }
                }
            }
        });
        phoneNumberEdit.addTextChangedListener(getTextWatcher("phone"));
        phoneNumberEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        emailEdit.addTextChangedListener(getTextWatcher("email"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getFragments().size() > 0)
            fm.beginTransaction().replace(R.id.fragment_layout, new Fragment()).commit();

        else finish();
    }

    private TextWatcher getTextWatcher(String name) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                switch (name) {
                    case "phone" : {
                        if (s.length() > 0) {
                            emailEdit.setEnabled(false);
                            applyButton(true);
                        }
                        else {
                            emailEdit.setEnabled(true);
                            applyButton(false);
                        }

                        break;
                    }
                    case "email" : {
                        if (s.length() > 0) {
                            phoneNumberEdit.setEnabled(false);
                            applyButton(true);
                        }
                        else {
                            phoneNumberEdit.setEnabled(true);
                            applyButton(false);
                        }
                    }
                }
            }
        };
    }

    private void applyButton(boolean isPrepared) {
        if (isPrepared) {
            sendCodeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.activated_button, getTheme()));
            sendCodeButton.setTextColor(getColor(android.R.color.white));
            sendCodeButton.setTag("prepared");
        }

        else {
            sendCodeButton.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.normal_button, getTheme()));
            sendCodeButton.setTextColor(getColor(android.R.color.black));
            sendCodeButton.setTag(null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHONE_HINT && resultCode == Activity.RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            phoneNumber = credential.getId().replace("+82", "0");
            phoneNumberEdit.setText(phoneNumber);
        }
    }

    @OnClick(R.id.send_code_button)
    void onSendCodeButtonClicked() {
        if (sendCodeButton.getTag().equals("prepared")) {
            if (phoneNumberEdit.isEnabled()) {
                if (phoneNumberEdit.getEditableText().length() > 0) {
                    SendSMSClient client = SendSMSClient.getInstance();
                    client.sendSMS(phoneNumber);
                    phoneNumberCode = client.getCode();

                    Snackbar.make(getWindow().getDecorView(), "휴대폰 인증 코드를 전송하였습니다.", Snackbar.LENGTH_SHORT).show();
                    startAuthCodeFragment();
                }

                else Snackbar.make(getWindow().getDecorView(), "입력란을 채워주세요.", Snackbar.LENGTH_SHORT).show();
            }

            if (emailEdit.isEnabled()) {
                if (emailEdit.getEditableText().length() > 0) {
                    GMailSender gMailSender = GMailSender.getInstance();
                    email = emailEdit.getEditableText().toString();
                    gMailSender.sendMail(email);
                    emailCode = gMailSender.getEmailCode();

                    Snackbar.make(getWindow().getDecorView(), "이메일 인증 코드를 전송하였습니다.", Snackbar.LENGTH_SHORT).show();
                    startAuthCodeFragment();
                }

                else Snackbar.make(getWindow().getDecorView(), "입력란을 채워주세요.", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void startAuthCodeFragment() {
        Bundle bundle = new Bundle();
        String address = null, code = null;

        if (phoneNumberEdit.isEnabled()) {
            address = phoneNumber;
            code = phoneNumberCode;
        }
        if (emailEdit.isEnabled()) {
            address = email;
            code = emailCode;
        }

        bundle.putString("address", address);
        bundle.putString("code", code);
        AuthCodeFragment fragment = new AuthCodeFragment();
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, 0, 0);
        transaction.replace(R.id.fragment_layout, fragment);
        transaction.commit();
    }
}