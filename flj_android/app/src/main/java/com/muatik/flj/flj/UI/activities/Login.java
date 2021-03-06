package com.muatik.flj.flj.UI.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.entities.Job;
import com.muatik.flj.flj.UI.utilities.BusManager;
import com.squareup.otto.Subscribe;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
// @TODO: klavye acildiginda form yukari kaymali, kaydirilabilir olmali
// @TODO: emaili yazdiktan sonra klavyede enter kisminda next olmali
// @TODO: signin ve signup dugmelerine basildiginda loading gorseli
public class Login extends FragmentActivity implements
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
    @BindView(R.id.input_email_rf) EditText emailTextRemember;
    @BindView(R.id.remember_link) TextView rememberLink;
    @BindView(R.id.login_link) TextView loginLink;
    @BindView(R.id.signinup_layout) LinearLayout signinupLayout;
    @BindView(R.id.remember_layout) LinearLayout rememberLayout;
    @BindView(R.id.remember) Button remember;
    private ProgressDialog loginProgress;


    private Unbinder unbinder;

    private void showApp() {
        startActivity(new Intent(this, Main.class));
//        Job j = new Job(
//                "Senior or junior Android and python Developer",
//                "2016-08-10T09:37:25.233146Z", "izmit", "turkey");
//        j.setJob_id("3");
//        j.setView_counter(5);
//        j.setSource_url("http://www.jobsite.co.uk/job/956507596?src=homepage&v96=ssr&jobTile=lv-charlieOne");
//        j.setEmployer("Hepsiburada.com");
//        j.setDescription(
//                "You will be a part of an intense and autonomous team that works on the most challenging projects to create games that amaze millions of people all around the world. Within the agile environment, you will work alongside software engineers, artists, game designers and product managers in a thriving, fun and fast-paced atmosphere.");
//        startActivity(new Intent(this, JobDetail.class).putExtra("job", j));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // @TODO: burada kayitli login bilgilerinin gecerli olup olmadigi sunucuda sorgulanmali
        AccountManager.init(PreferenceManager.getDefaultSharedPreferences(this));
        if (AccountManager.isAuthenticated()) {
            showApp();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);
        loginProgress = new ProgressDialog(this);
        //loginProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        unbinder = ButterKnife.bind(this);

        prepareGoogle();
        prepareFacebookLogin();
        BusManager.get().register(this);
    }

    @Override
    protected void onStart() {
        if (AccountManager.isAuthenticated()) {
            showApp();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        AccountManager.saveState();
        super.onStop();
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
                loginProgress.setMessage(getResources().getString(R.string.waiting_sign_in_facebook));
                loginProgress.show();
            }
            @Override
            public void onCancel() {
                Log.d("Facebook---","onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Facebook---",exception.getMessage());
            }
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
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setScopes(gso.getScopeArray());
        setGooglePlusButtonText(signInButton, getResources().getString(R.string.google_login_button));
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(16);
                tv.setBackgroundResource(R.drawable.login_google_background);
                tv.setGravity(Gravity.CENTER);
                tv.setWidth(350);
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(16);
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Subscribe
    public void onSuccessfulSignIn(AccountManager.EventSuccessfulSignIn event) {
        Log.e("FLJ", "login onSuccessfulSignIn");
        AccountManager.saveState();
        loginProgress.cancel();
        showApp();
    }

    @Subscribe
    public void onFailureSignIn(AccountManager.EventSignInFailure event) {
        Log.e("FLJ", "login onFailureSignIn");
        Toast.makeText(getApplicationContext(),event.errorMessage.toString(), Toast.LENGTH_LONG).show();
        loginProgress.cancel();
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
            loginProgress.setMessage(getResources().getString(R.string.waiting_sign_in_google));
            loginProgress.show();
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
        Toast.makeText(
            getApplicationContext(),
            getResources().getString(R.string.google_failed_message),
            Toast.LENGTH_LONG
        ).show();
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
        if (validateBasicAuthForn()){
            loginProgress.setMessage(getResources().getString(R.string.waiting_for_sign_in));
            loginProgress.show();
            AccountManager.signinBasicAuth(email, password);
        }
    }

    @OnClick(R.id.signup)
    public void formSignup() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        int userId = 0;
        Account.UserProfile userprofile = new Account.UserProfile();
        Account account  = new Account(userId, email, email, password);
        account.userprofile = userprofile;
        if (validateBasicAuthForn()){
            loginProgress.setMessage(getResources().getString(R.string.waiting_for_sign_up));
            loginProgress.show();
            AccountManager.signupBasicAuth(account);
        }

    }

    public boolean validateBasicAuthForn() {

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.invalid_email_message),
                Toast.LENGTH_LONG
            ).show();
            return false;
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.invalid_password_message),
                Toast.LENGTH_LONG
            ).show();
            return false;
        }

        return true;
    }

    @OnClick(R.id.remember_link)
    public void rememberLink() {
        signinupLayout.setVisibility(View.GONE);
        rememberLayout.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.login_link)
    public void loginLink() {
        signinupLayout.setVisibility(View.VISIBLE);
        rememberLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.remember)
    public void remember() {
        String email = emailTextRemember.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                getApplicationContext(),
                getResources().getString(R.string.invalid_email_message),
                Toast.LENGTH_LONG
            ).show();
        } else {
            Log.d("FLJ-remember", emailTextRemember.getText().toString());
        }
    }

}
