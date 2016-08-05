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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
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

    private GoogleApiClient googleApiClient;

    @BindView(R.id.signup_name)  EditText etUsername_signup;
    @BindView(R.id.signup_email) EditText etEmail_signup;
    @BindView(R.id.signup_password) EditText etPassword_signup;

    @BindView(R.id.signin_email) EditText etEmail_signin;
    @BindView(R.id.signin_password) EditText etPassword_signin;
    @BindView(R.id.signin) Button btSignin;
    @BindView(R.id.signup) Button btSignup;
    @BindView(R.id.link_signup)  TextView linkSignup;
    @BindView(R.id.link_login)  TextView linkLogin;
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

        //this row must be BEFORE setContentView()
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        unbinder = ButterKnife.bind(this);

        prepareGoogle();
        prepareFacebookLogin();
        BusManager.get().register(this);
    }

    private void prepareFacebookLogin() {
//        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
//        if (profile != null) {
//            Log.d("aaa","logged in");
//        } else {
//            // user has not logged in
//        }

        List<String> permissions = Arrays.asList("public_profile, email");
        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        fbLoginButton.setReadPermissions(permissions);
        fbCallbackManager = CallbackManager.Factory.create();
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String profileId = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                Log.e("FLJ", "login fb onSuccess");
                AccountManager.signinViaFacebook(profileId, token);

            }
            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException exception) {}
        });
    }


    void prepareGoogle() {
        //google login

        findViewById(R.id.google_signin).setOnClickListener(this);
        findViewById(R.id.google_signout).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("223265251589-b1ojj9mj6pitskb9oh0rksifg3qijje5.apps.googleusercontent.com")
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.google_signin);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setEnabled(true);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);

        signInButton.setScopes(gso.getScopeArray());
        // end of google login
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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        }else{
            //facebook callback
            fbCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


      @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            //handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    Toast.makeText(getApplicationContext(), "" + googleSignInResult.getStatus(), Toast.LENGTH_LONG).show();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }
    }

    // [START handleSignInResult]
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        Log.d("flj-googlelogin", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            updateGoogleLoginUI(true);
            AccountManager.signinViaGoogle(result.getSignInAccount().getIdToken());
        } else {
            updateGoogleLoginUI(false);
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
                        updateGoogleLoginUI(false);
                    }
                });
    }

    private void googleRevokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        updateGoogleLoginUI(false);
                    }
                });
    }


    private void updateGoogleLoginUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.google_signout).setVisibility(View.VISIBLE);
            findViewById(R.id.google_signin).setVisibility(View.GONE);

        } else {
            findViewById(R.id.google_signin).setVisibility(View.VISIBLE);
            findViewById(R.id.google_signout).setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.google_signin)
    public void google_signin(View view) {
        googleSignIn();
    }

    @OnClick(R.id.google_signout)
    public void google_signout(View view) {
        googleSignOut();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("FLJ-googlelogin", "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_signin:
                googleSignIn();
                break;
            case R.id.google_signout:
                googleSignOut();
                break;
        }
    }


    // form signinBasicAuth and signup
    @OnClick(R.id.signin)
    public void formSignin() {
        Log.d("FLJ", etEmail_signin.getText() +"-" + etPassword_signin.getText() );
        AccountManager.signinBasicAuth(
                etEmail_signin.getText().toString(),
                etPassword_signin.getText().toString());
    }


    @OnClick(R.id.signup)
    public void formSignup() {
        String username = etEmail_signup.getText().toString();
        String email = etEmail_signup.getText().toString();
        String password = etPassword_signup.getText().toString();
        int userId = 0;
        Account.UserProfile userprofile = new Account.UserProfile();
        Account account  = new Account(userId, username, email, password);
        account.userprofile = userprofile;

        AccountManager.signupBasicAuth(account);
        Log.d("FLJ", etUsername_signup.getText() +"-" +etEmail_signup.getText() +"-" + etPassword_signup.getText());
    }

    @OnClick(R.id.link_signup)
    public void link_signup() {
        findViewById(R.id.signin_layout).setVisibility(View.GONE);
        findViewById(R.id.signup_layout).setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.link_login)
    public void link_login() {
        findViewById(R.id.signin_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.signup_layout).setVisibility(View.GONE);
    }
}
