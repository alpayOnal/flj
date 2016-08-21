package com.muatik.flj.flj.UI.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class Profile extends DetailActivity {

    @BindView(R.id.firstname)  EditText profile_firstname;
    @BindView(R.id.lastname)  EditText profile_lastname;
    @BindView(R.id.email)  EditText profile_email;
    @BindView(R.id.password)  EditText profile_password;
    @BindView(R.id.profileImage) ImageView profile_image;
    @BindView(R.id.updateProfile) Button button_update;
    @BindView(R.id.changePhoto) TextView changePhoto;
    private ProgressDialog updateProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        init();
        Account account = AccountManager.getAuthenticatedAccount();
        updateProgress = new ProgressDialog(this);
        updateProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        profile_email.setText(account.getEmail());
        profile_firstname.setText(account.getFirstName());
        profile_lastname.setText(account.getLast_name());
        //profile_image.setImageURI(Uri.parse(account.userprofile.getPicture()));
    }


    @OnClick(R.id.updateProfile)
    public void updateProfile() {
        String email = profile_email.getText().toString();
        String firstname = profile_firstname.getText().toString();
        String lastname = profile_lastname.getText().toString();
        String password = profile_password.getText().toString();
        String username = email;
        Account account = new Account(firstname, lastname, email, username, password);
        account.userprofile = AccountManager.getAuthenticatedAccount().userprofile;
        if (validateUpdateForm()) {
            AccountManager.updateAccout(account);
            updateProgress.show();
        }
    }

    @Subscribe
    public void accountUpdateSuccessfull(AccountManager.EventSuccessfulAccountUpdate event){
        AccountManager.setAuthenticatedAccount(event.account);
        updateProgress.cancel();
        Toast.makeText(getApplicationContext(), "Your profile updated", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void accountUpdateFailure(AccountManager.EventAccountUpdateFailure event){
        Toast.makeText(getApplicationContext(),event.errorMessage.toString(), Toast.LENGTH_LONG).show();
        updateProgress.cancel();
    }


    public boolean validateUpdateForm() {

        String email = profile_email.getText().toString();
        String firstname = profile_firstname.getText().toString();
        String lastname = profile_lastname.getText().toString();
        String password = profile_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getApplicationContext(),
                    "Enter a valid email address.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (firstname.isEmpty() || lastname.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "The above fields are required", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @OnClick(R.id.changePhoto)
    public void changePhoto() {
        Toast.makeText(getApplicationContext(),
                "Change picture", Toast.LENGTH_LONG).show();
    }
}
