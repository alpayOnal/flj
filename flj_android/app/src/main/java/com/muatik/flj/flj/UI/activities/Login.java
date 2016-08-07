package com.muatik.flj.flj.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.squareup.otto.Subscribe;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Login extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener  {
    Context thisContext;
    LoginButton fbLoginButton;
    CallbackManager fbCallbackManager;

    private static final int RC_SIGN_IN = 9001;

    private static GoogleApiClient googleApiClient;

    @BindView(R.id.input_email) EditText emailText;
    @BindView(R.id.input_password) EditText passwordText;
    @BindView(R.id.signin) Button btSignin;
    @BindView(R.id.signup) Button btSignup;
    private Unbinder unbinder;


    private void showApp() {
        startActivity(new Intent(this, Main.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AccountManager.init(PreferenceManager.getDefaultSharedPreferences(this));
        if (AccountManager.isAuthenticated()) {
            showApp();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());


        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);


        prepareGoogle();
        prepareFacebookLogin();
        BusManager.get().register(this);
    }

    private void prepareFacebookLogin() {

        List<String> permissions = Arrays.asList("public_profile, email");
        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        fbLoginButton.setReadPermissions(permissions);
        fbCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String profileId = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                AccountManager.signinViaFacebook(profileId, token);
            }
            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {}
        });
    }


    void prepareGoogle() {
        findViewById(R.id.google_signin).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(String.valueOf(R.string.google_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        SignInButton signInButton = (SignInButton) findViewById(R.id.google_signin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setEnabled(true);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setScopes(gso.getScopeArray());
    }

    @Subscribe
    public void onSuccessfulSignIn(AccountManager.EventSuccessfulSignIn event) {
        Log.e("FLJ", "login onSuccessfulSignIn");
        AccountManager.saveState();
        showApp();
    }

    @Subscribe
    public void onSignout(AccountManager.EventSignout event) {
        Log.e("FLJ", "login onSignout");
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                Auth.GoogleSignInApi.signOut(googleApiClient);
                googleApiClient.disconnect();
            }
        }
        LoginManager.getInstance().logOut();
        showApp();
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        BusManager.get().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }else{
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d("flj-googlelogin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            AccountManager.signinViaGoogle(result.getSignInAccount().getIdToken());
        } else {
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void googleSignOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }

    @OnClick(R.id.google_signin)
    public void google_signin(View view) {
        googleSignIn();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "Google sign in failed. Please try again.", Toast.LENGTH_LONG).show();
        Log.d("FLJ-googlelogin", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_signin:
                googleSignIn();
                break;
        }
    }

    @OnClick(R.id.signin)
    public void formSignin() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (validateBasicAuthForn())
            AccountManager.signinBasicAuth(email, password);
    }


    @OnClick(R.id.signup)
    public void formSignup() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        int userId = 0;
        Account.UserProfile userprofile = new Account.UserProfile();
        Account account  = new Account(userId, email, email, password);
        account.userprofile = userprofile;
        if (validateBasicAuthForn())
            AccountManager.signupBasicAuth(account);
    }


    public boolean validateBasicAuthForn() {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(),
                    "Enter a valid email addres.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            Toast.makeText(getApplicationContext(),
                    "Between 4 and 10 alphanumeric characters.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
