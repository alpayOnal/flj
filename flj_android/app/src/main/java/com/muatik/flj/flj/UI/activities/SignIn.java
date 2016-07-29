package com.muatik.flj.flj.UI.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.muatik.flj.flj.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.StringTokenizer;


public class SignIn extends AppCompatActivity {
    Context thisContext;
    LoginButton fbLoginButton;
    CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());     //this row must be BEFORE setContentView()
        setContentView(R.layout.sign_in_activity);
        // facebooklogin
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null) {
            Log.d("aaa","logged in");
            // user has logged in
        } else {
            Log.d("aaa","not logged in");
            // user has not logged in
        }

//        BEGIN OF KEYHASH CODE (once retrieved the keyhash you can remove this code)
//        try{
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.example.alpay.myapplication", PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                //This line will log the KeyHash you have to put in your facebook app settings in the facebook developer panel
//
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }
//        // END OF KEYHASH CODE

        fbLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        fbCallbackManager = CallbackManager.Factory.create();
        // If using in a fragment
        // fbLoginButton.setFragment(thisContext);
        // Other app specific specialization

        // Callback registration
        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                Log.v("LoginActivity", response.toString());

                                try {
                                    Log.v("Name:", response.getJSONObject().get("name").toString());
                                    Toast.makeText(getApplicationContext(), "Welcome " + response.getJSONObject().get("name").toString() +
                                                    "\n" + response.getJSONObject().get("email").toString(),
                                            Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(thisContext, "Cancel!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(thisContext, "Facebook error!", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
