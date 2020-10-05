package com.now9e0n.winnerpark;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

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
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private AppManager app;

    private FragmentManager fm;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ButterKnife.bind(this);
        app = (AppManager) getApplication();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        fm = getSupportFragmentManager();

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

    @OnClick(R.id.msg_login_layout)
    public void onMSGLoginClicked() {
        Intent intent = new Intent(getApplicationContext(), MSGLoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.google_login_layout)
    public void onGoogleLoginClicked() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @OnClick(R.id.facebook_login_layout)
    public void onFacebookLoginClicked() {
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
        } else callbackManager.onActivityResult(requestCode, resultCode, data);
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
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        app.setUser(UserModel.getUserBySnapshot((Map<String, String>) snapshot));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.w("Firebase", "Value Event Listen Cancelled", error.toException());
                                    }
                                });

                                StorageReference storageReference = FirebaseStorage.getInstance()
                                        .getReference("profiles/" + user.getUid());

                                CustomTarget<Bitmap> target = new CustomTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, byteOutStream);
                                        byte[] data = byteOutStream.toByteArray();

                                        storageReference.putBytes(data).addOnCompleteListener(task -> startNextActivity());
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        Log.w("Glide", "Image Load Cancelled");
                                    }
                                };

                                Glide.with(getApplicationContext()).asBitmap().load(user.getPhotoUrl()).into(target);
                            } else app.setUser(UserModel.builder().build());
                        } else {
                            Log.e("Firebase", "SignIn With Credential Failed", task.getException());
                        }
                    });
                }
            }
        }.start();
    }

    private void startNextActivity() {
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