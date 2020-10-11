package com.now9e0n.winnerpark;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

import static com.now9e0n.winnerpark.AppManager.getMyDrawable;

public class MSGSignUpActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_HINT = 101;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.phone_number_et)
    EditText phoneNumberEt;
    @BindView(R.id.email_et)
    EditText emailEt;

    @BindView(R.id.send_code_btn)
    Button sendCodeBtn;

    private GoogleApiClient apiClient;
    private HintRequest hintRequest;

    private String phoneNumber;
    private String phoneNumberCode;
    private String email;
    private String emailCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_sign_up);

        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        getPhoneNumberInit();
        init();
    }

    private void getPhoneNumberInit() {
        apiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, 0, null)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
    }

    private void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Drawable drawable = getMyDrawable(R.drawable.arrow_left);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        int length = (int) (20 * AppManager.getDensityRatio());
        drawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, length, length, true));
        actionBar.setHomeAsUpIndicator(drawable);

        phoneNumberEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (phoneNumberEt.getEditableText().length() == 0) {
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
        phoneNumberEt.addTextChangedListener(getTextWatcher("phone"));
        phoneNumberEt.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        emailEt.addTextChangedListener(getTextWatcher("email"));

        sendCodeBtn.setTag("");
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
        Fragment fragment;

        if ((fragment = fm.findFragmentByTag("create_password")) != null) fm.beginTransaction().remove(fragment).commit();
        else if ((fragment = fm.findFragmentByTag("auth_code")) != null) fm.beginTransaction().remove(fragment).commit();
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
                            emailEt.setEnabled(false);
                            sendCodeBtn.setTag("prepared");
                        }
                        else {
                            emailEt.setEnabled(true);
                            sendCodeBtn.setTag("");
                        }
                    }
                    break;

                    case "email" : {
                        if (s.length() > 0) {
                            phoneNumberEt.setEnabled(false);
                            sendCodeBtn.setTag("prepared");
                        }
                        else {
                            phoneNumberEt.setEnabled(true);
                            sendCodeBtn.setTag("");
                        }
                    }
                    break;
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PHONE_HINT && resultCode == Activity.RESULT_OK) {
            Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
            phoneNumber = credential.getId().replace("+82", "0");
            phoneNumberEt.setText(phoneNumber);
        }
    }

    @OnClick(R.id.send_code_btn)
    void onSendCodeBtnClicked() {
        if (sendCodeBtn.getTag().equals("prepared")) {
            LoadingDialogFragment fragment = new LoadingDialogFragment();

            if (phoneNumberEt.isEnabled()) {
                fragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG_LOADING_DIALOG);

                SendSMSClient client = SendSMSClient.getInstance();
                client.sendSMS(phoneNumber, () -> {
                    fragment.dismiss();

                    phoneNumberCode = client.getCode();

                    startAuthCodeFragment();
                    Snackbar.make(getWindow().getDecorView(), "휴대폰 인증 코드를 전송하였습니다.", Snackbar.LENGTH_LONG).show();
                });
            }

            if (emailEt.isEnabled()) {
                email = emailEt.getEditableText().toString();

                if (GMailSender.isValidEmailAddress(email)) {
                    fragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG_LOADING_DIALOG);

                    GMailSender gMailSender = GMailSender.getInstance();
                    gMailSender.sendMail(email, () -> {
                        fragment.dismiss();

                        emailCode = gMailSender.getEmailCode();

                        startAuthCodeFragment();
                        Snackbar.make(getWindow().getDecorView(), "이메일 인증 코드를 전송하였습니다.", Snackbar.LENGTH_LONG).show();
                    });
                }

                else Toast.makeText(getApplicationContext(), "이메일 주소가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        }

        else Toast.makeText(getApplicationContext(), "입력란을 채워주세요.", Toast.LENGTH_SHORT).show();
    }

    private void startAuthCodeFragment() {
        Bundle bundle = new Bundle();
        String address = null, code = null;

        if (phoneNumberEt.isEnabled()) {
            address = phoneNumber;
            code = phoneNumberCode;
        }
        if (emailEt.isEnabled()) {
            address = email;
            code = emailCode;
        }

        bundle.putString("address", address);
        bundle.putString("code", code);
        AuthCodeFragment fragment = new AuthCodeFragment();
        fragment.setArguments(bundle);

        addFragment(fragment, "auth_code");
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, 0, 0);
        transaction.add(R.id.fragment_layout, fragment, tag);
        transaction.commit();
    }
}