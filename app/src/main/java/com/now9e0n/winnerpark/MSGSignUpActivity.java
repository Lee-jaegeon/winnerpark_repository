package com.now9e0n.winnerpark;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import lombok.Getter;

import static com.now9e0n.winnerpark.AppManager.activityWindowSet;
import static com.now9e0n.winnerpark.AppManager.animEndListener;
import static com.now9e0n.winnerpark.AppManager.getCurrentDate;
import static com.now9e0n.winnerpark.AppManager.getHashCode;
import static com.now9e0n.winnerpark.AppManager.getReSizedDrawable;

public class MSGSignUpActivity extends AppCompatActivity {

    private static final int RC_PHONE_HINT = getHashCode("RC_PHONE_HINT");
    private static final int RC_EMAIL_CHOOSE = getHashCode("RC_EMAIL_CHOOSE");

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.phone_number_et)
    EditText phoneNumberEt;
    @BindView(R.id.email_et)
    EditText emailEt;

    @BindView(R.id.error_tv)
    TextView errorTv;

    @BindView(R.id.send_code_btn)
    Button sendCodeBtn;

    private GoogleApiClient apiClient;
    private HintRequest hintRequest;

    private String phoneNumber;
    private String phoneNumberCode;
    private String email;
    private String emailCode;

    private AuthCodeFragment fragment;
    private boolean isFragmentRemoving;

    private TimerTask task;
    @Getter private long deltaTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_sign_up);
        activityWindowSet(this);

        ButterKnife.bind(this);
        init();
    }

    private void init() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(getReSizedDrawable(R.drawable.arrow_left, 20, 20));

        apiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, 0, null)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();

        phoneNumberEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (TextUtils.isEmpty(phoneNumberEt.getEditableText()) && hasFocus) {
                PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
                try {
                    startIntentSenderForResult(intent.getIntentSender(), RC_PHONE_HINT, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.e("Phone", "Request Phone Number Failed", e);
                }
            }
        });
        emailEt.setOnFocusChangeListener((view, hasFocus) -> {
            if (TextUtils.isEmpty(emailEt.getEditableText()) && hasFocus) {
                Intent googlePicker = AccountPicker.newChooseAccountIntent(new AccountPicker.AccountChooserOptions.Builder().build());
                startActivityForResult(googlePicker, RC_EMAIL_CHOOSE);
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
        if (!isFragmentRemoving) {
            isFragmentRemoving = true;

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment;

            if ((fragment = fm.findFragmentByTag("create_password")) != null)
                removeFragmentAnimation(fm, fragment, this.fragment);

            else if (fm.findFragmentByTag("auth_code") != null)
                removeFragmentAnimation(fm, this.fragment);

            else finish();
        }
    }

    private void removeFragmentAnimation(FragmentManager fm, Fragment... fragments) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.pop_exit);
        animation.setAnimationListener(animEndListener(() -> {
            for (Fragment fragment : fragments) fm.beginTransaction().remove(fragment);
            fm.beginTransaction().commit();

            isFragmentRemoving = false;
        }));

        fragments[0].getView().startAnimation(animation);
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
                    case "phone" :
                        applyUI(emailEt, s.length() > 0);
                        break;

                    case "email" :
                        applyUI(phoneNumberEt, s.length() > 0);
                        break;
                }
            }
        };
    }

    private void applyUI(EditText editText, boolean isInput) {
        if (isInput) {
            editText.setEnabled(false);
            editText.setHintTextColor(getColor(R.color.white_gray));
            sendCodeBtn.setTag("prepared");
        }

        else {
            editText.setEnabled(true);
            editText.setHintTextColor(getColor(R.color.dark_gray));
            sendCodeBtn.setTag("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PHONE_HINT && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                phoneNumber = credential.getId().replace("+82", "0");
                phoneNumberEt.setText(phoneNumber);
            }
        }

        if (requestCode == RC_EMAIL_CHOOSE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                emailEt.setText(email);
            }
        }
    }

    @OnClick(R.id.send_code_btn)
    void onSendCodeBtnClicked() {
        if ((TextUtils.isEmpty(phoneNumberEt.getEditableText()) && TextUtils.isEmpty(emailEt.getEditableText())))
            showErrorTv();

        if (sendCodeBtn.getTag().equals("prepared")) {
            if (deltaTime == 0) sendCode();
            else Toasty.info(getApplicationContext(),
                    "인증코드 재전송 : " + fragment.getTimer() + " 남음", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void showErrorTv() {
        errorTv.setVisibility(View.VISIBLE);

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setAnimationListener(animEndListener(() -> errorTv.setVisibility(View.INVISIBLE)));

        AnimationSet set = new AnimationSet(false);
        set.setDuration(1000);
        set.addAnimation(fadeIn);
        set.addAnimation(fadeOut);

        errorTv.startAnimation(set);
    }

    private void sendCode() {
        LoadingDialogFragment loading = new LoadingDialogFragment();
        fragment = new AuthCodeFragment();

        if (phoneNumberEt.isEnabled()) {
            loading.show(getSupportFragmentManager(), null);

            SendSMSClient client = SendSMSClient.getInstance();
            client.sendSMS(phoneNumber, () -> {
                loading.dismiss();

                startTimer(client.getDate());
                phoneNumberCode = client.getCode();

                startAuthCodeFragment();
                fragment.startSMSReceiver();
                Toasty.success(getApplicationContext(), "휴대폰 인증 코드를 전송하였습니다.", Toast.LENGTH_SHORT).show();
            });
        }

        if (emailEt.isEnabled()) {
            email = emailEt.getEditableText().toString();

            if (GMailSender.isValidEmailAddress(email)) {
                loading.show(getSupportFragmentManager(), null);

                GMailSender gMailSender = GMailSender.getInstance();
                gMailSender.sendMail(email, () -> {
                    loading.dismiss();

                    startTimer(gMailSender.getDate());
                    emailCode = gMailSender.getEmailCode();

                    startAuthCodeFragment();
                    Toasty.success(getApplicationContext(), "이메일 인증 코드를 전송하였습니다.", Toast.LENGTH_SHORT).show();
                });
            }

            else Toasty.error(getApplicationContext(), "이메일 주소가 존재하지 않습니다.", Toast.LENGTH_SHORT, true).show();
        }
    }

    public void startTimer(Date date) {
        if (task != null) task.cancel();

        task = new TimerTask() {
            @Override
            public void run() {
                deltaTime = 180000 - (getCurrentDate(Date.class).getTime() - date.getTime());
                if (deltaTime < 0) {
                    deltaTime = 0;
                    cancel();
                }
                if (fragment != null) runOnUiThread(() -> fragment.spanString());
            }
        };

        new Timer().schedule(task, 0, 1000);
    }

    private void startAuthCodeFragment() {
        Bundle bundle = new Bundle();
        String address = null, code = null, type = null;

        if (phoneNumberEt.isEnabled()) {
            address = phoneNumber;
            code = phoneNumberCode;
            type = "phone";
        }
        if (emailEt.isEnabled()) {
            address = email;
            code = emailCode;
            type = "email";
        }

        bundle.putString("address", address);
        bundle.putString("code", code);
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        addFragment(fragment, "auth_code");
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.add(R.id.fragment_layout, fragment, tag).commit();
    }
}