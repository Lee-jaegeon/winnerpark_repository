package com.now9e0n.winnerpark;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private AppManager app;

    private GoogleSignInClient googleSignInClient;

    private CallbackManager callbackManager;
    private FacebookCallback<LoginResult> loginCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        app = (AppManager) getApplicationContext();

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

                nextActivity();
            }

            @Override
            public void onCancel() {
                Log.w("Facebook", "LogIn Cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Facebook", "LogIn Failed", error);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuth(account.getIdToken(), "Google");

                    nextActivity();
                }
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
                                String userName = user.getDisplayName();
                                app.setUser(UserModel.builder().name(userName).build());
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

    @OnClick(R.id.google_sign_in_button)
    void onGoogleSignInButtonClicked() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.facebook_login_button)
    void onFacebookLogInButtonClicked() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, loginCallback);
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