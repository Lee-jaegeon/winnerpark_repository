package com.now9e0n.winnerpark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private AppManager app;

    @BindView(R.id.id_input_edit_text)
    TextInputEditText idEditText;
    @BindView(R.id.password_input_edit_text)
    TextInputEditText passwordEditText;

    private boolean[] isInputComplete = new boolean[2];

    @BindView(R.id.login_image_button)
    ImageView loginImageButton;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        app = (AppManager) getApplication();

        idEditText.addTextChangedListener(getTextWatcher("id"));
        passwordEditText.addTextChangedListener(getTextWatcher("password"));

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

    @OnClick(R.id.back_button)
    void onBackButtonClicked() {

    }

    @OnClick(R.id.login_help_button)
    void onLoginHelpButtonClicked() {

    }

    @OnClick(R.id.sign_up_button)
    void onSignUpButtonClicked() {
        Intent intent = new Intent(this, SignUpActivity_1.class);
        startActivity(intent);
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

                if (viewName.equals("id")) {
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
                        idEditText.setError(null);

                        isInputComplete[0] = true;
                    }
                }

                if (viewName.equals("password")) {
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
                }

                if (isInputComplete[0] && isInputComplete[1])
                    loginImageButton.setVisibility(View.VISIBLE);

                else loginImageButton.setVisibility(View.INVISIBLE);
            }
        };
    }

    @OnClick(R.id.login_image_button)
    void onLoginButtonClicked() {

        nextActivity();
    }

    @OnClick(R.id.google_sign_in_button)
    void onGoogleSignInButtonClicked() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.facebook_login_button)
    void onFacebookLoginButtonClicked() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, loginCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
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
                if (credential != null) {
                    auth.signInWithCredential(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                UserModel userModel = UserModel.builder()
                                        .name(user.getDisplayName())
                                        .email(user.getEmail())
                                        .phoneNumber(user.getPhoneNumber())
                                        .build();

                                app.setUser(userModel);
                                writeUserIntoFirebase(user);
                            }

                            else app.setUser(UserModel.builder().build());
                        } else {
                            Log.e("Firebase", "SignIn With Credential Failed", task.getException());
                        }
                    });
                }
            }
        }.start();
    }

    private void writeUserIntoFirebase(FirebaseUser user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());
        databaseReference.setValue(app.getUser());

        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("profiles/" + user.getUid());

        CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                storageReference.putBytes(data).addOnCompleteListener(task -> nextActivity());
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                Log.w("Glide", "Image Load Cancelled");
            }
        };

        Glide.with(this).asBitmap().load(user.getPhotoUrl()).into(target);
    }

    private void nextActivity() {
        Intent intent = new Intent(this, MainActivity.class);
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