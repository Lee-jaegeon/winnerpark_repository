package com.now9e0n.winnerpark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.now9e0n.winnerpark.AppManager.activityWindowSet;
import static com.now9e0n.winnerpark.AppManager.getCurrentDate;
import static com.now9e0n.winnerpark.AppManager.getMyColor;
import static com.now9e0n.winnerpark.AppManager.getMyDrawable;
import static com.now9e0n.winnerpark.AppManager.isTextEqual;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_GOOGLE_SIGN_IN = 100;

    private AppManager app;

    private TextInputLayout idLayout;
    @BindView(R.id.id_et)
    EditText idEt;
    private TextInputLayout passwordLayout;
    @BindView(R.id.password_et)
    EditText passwordEt;

    @BindView(R.id.login_imv)
    ImageView loginImv;

    private DatabaseReference reference;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activityWindowSet(this);

        ButterKnife.bind(this);
        init();
        signInInit();
    }

    private void init() {
        app = (AppManager) getApplication();

        idLayout = (TextInputLayout) idEt.getParent().getParent();
        passwordLayout = (TextInputLayout) passwordEt.getParent().getParent();

        passwordEt.setOnFocusChangeListener((view, focus) -> {
            if (focus) passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            else passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
        });

        loginImv.setTag("");

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (getCurrentFocus() == idEt) idLayout.setError("");
                if (getCurrentFocus() == passwordEt) passwordLayout.setError("");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Drawable drawable = loginImv.getDrawable();

                if (!TextUtils.isEmpty(idEt.getEditableText()) && !TextUtils.isEmpty(passwordEt.getEditableText())) {
                    loginImv.setTag("prepared");
                    drawable.setTint(getMyColor(android.R.color.white));
                    loginImv.setImageDrawable(drawable);
                    loginImv.setBackground(getMyDrawable(R.drawable.bg_login_activated));
                } else {
                    loginImv.setTag("");
                    drawable.setTint(getMyColor(R.color.light_gray));
                    loginImv.setImageDrawable(drawable);
                    loginImv.setBackground(getMyDrawable(R.drawable.bg_login_normal));
                }
            }
        };

        idEt.addTextChangedListener(textWatcher);
        passwordEt.addTextChangedListener(textWatcher);
    }

    private void signInInit() {
        reference = FirebaseDatabase.getInstance().getReference("user_list");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
        loginCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String tokenId = loginResult.getAccessToken().getToken();
                firebaseAuth(tokenId, "Facebook");
            }

            @Override
            public void onCancel() {
                Log.w("Facebook", "Login Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook", "Login Failed", error);
            }
        };
    }

    @OnClick(R.id.login_imv)
    void onLoginImvClicked() {
        if (TextUtils.isEmpty(idEt.getEditableText()))
            idLayout.setError("입력란을 채워주세요 :)");

        if (TextUtils.isEmpty(passwordEt.getEditableText()))
            passwordLayout.setError("입력란을 채워주세요 :)");

        if (loginImv.getTag().equals("prepared")) {
            LoadingDialogFragment loadingFragment = new LoadingDialogFragment();
            loadingFragment.show(getSupportFragmentManager(), null);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<User> userList = User.getUserBySnapshot(snapshot);
                    for (User user : userList) {
                        if (isTextEqual(idEt, user.getPhoneNumber(), user.getEmail()) && isTextEqual(passwordEt, user.getPassword())) {
                            loadingFragment.dismiss();
                            app.setUser(user);
                            startMainActivity();
                            Toasty.success(getApplicationContext(), "로그인에 성공하였습니다.", Toast.LENGTH_SHORT, true).show();
                            return;
                        }
                    }

                    loadingFragment.dismiss();
                    Toasty.error(getApplicationContext(), "일치하는 계정의 정보가 없습니다.", Toast.LENGTH_SHORT, true).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("Firebase", "Value Event Listen Cancelled", error.toException());
                }
            });
        }
    }

    @OnClick(R.id.google_login_btn)
    void onGoogleLoginBtnClicked() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_GOOGLE_SIGN_IN);
    }

    @OnClick(R.id.facebook_login_btn)
    void onFacebookLoginBtnClicked() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, loginCallback);
    }

    @OnClick(R.id.sign_up_tv)
    void onSignUpTvClicked() {
        startActivity(new Intent(getApplicationContext(), MSGSignUpActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuth(account.getIdToken(), "Google");
            } catch (ApiException e) {
                Log.e("Google", "Get SignInAccount Failed", e);
            }
        }

        else callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuth(String idToken, String type) {
        new Thread() {
            @Override
            public void run() {
                AuthCredential credential = null;
                if (type.equals("Google"))
                    credential = GoogleAuthProvider.getCredential(idToken, null);
                if (type.equals("Facebook"))
                    credential = FacebookAuthProvider.getCredential(idToken);

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.hasChild(firebaseUser.getUid())) {
                                    User user = User.builder()
                                            .name(firebaseUser.getDisplayName())
                                            .phoneNumber(firebaseUser.getPhoneNumber())
                                            .email(firebaseUser.getEmail())
                                            .createdDate(getCurrentDate(String.class))
                                            .build();

                                    reference.push().setValue(user);
                                    reference.setValue(firebaseUser.getUid());

                                    writeStorage(firebaseUser);
                                }

                                User user = User.getUserByUserData((Map<String, Object>) snapshot.child(firebaseUser.getUid()));
                                app.setUser(user);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w("Firebase", "Value Event Listen Cancelled", error.toException());
                            }
                        });
                    }

                    else Log.e("Firebase", "SignIn With Credential Failed", task.getException());
                });
            }
        }.start();
    }

    private void writeStorage(@NonNull FirebaseUser user) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profiles/" + user.getUid());
        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 100, byteOutStream);
                byte[] data = byteOutStream.toByteArray();

                storageReference.putBytes(data).addOnCompleteListener(task -> startMainActivity());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Log.w("Glide", "Image Load Cancelled");
            }
        };

        Glide.with(getApplicationContext()).asBitmap().load(user.getPhotoUrl()).into(target);
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (app.getUser() != null) app.saveUser();
    }
}