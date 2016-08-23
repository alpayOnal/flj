package com.muatik.flj.flj.UI.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.muatik.flj.flj.R;
import com.muatik.flj.flj.UI.entities.Account;
import com.muatik.flj.flj.UI.entities.AccountManager;
import com.muatik.flj.flj.UI.utilities.DownloadImageTask;
import com.squareup.otto.Subscribe;

import butterknife.BindView;
import butterknife.OnClick;

public class Profile extends DetailActivity {

    @BindView(R.id.firstname)  EditText profile_first_name;
    @BindView(R.id.lastname)  EditText profile_last_name;
    @BindView(R.id.email)  EditText profile_email;
    @BindView(R.id.password)  EditText profile_password;
    @BindView(R.id.display_name)  TextView profile_display_name;
    @BindView(R.id.profileImage) ImageView profile_image;
    @BindView(R.id.updateProfile) Button button_update;
    private ProgressDialog updateProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        init();
        Account account = AccountManager.getAuthenticatedAccount();
        updateProgress = new ProgressDialog(this);

        profile_email.setText(account.getEmail());
        profile_first_name.setText(account.getFirstName());
        profile_last_name.setText(account.getLast_name());
        if (!account.userprofile.getPicture().isEmpty())
            new DownloadImageTask(profile_image).execute(account.userprofile.getPicture());
        else
            profile_image.setImageResource(R.drawable.photo);

        getSupportActionBar().setTitle("");
        if (!account.getDisplayName().isEmpty())
            profile_display_name.setText(account.getDisplayName());
        else
            profile_display_name.setText(getResources().getString(R.string.emtpy_account_display_name));
    }


    @OnClick(R.id.updateProfile)
    public void updateProfile() {
        String email = profile_email.getText().toString();
        String firstname = profile_first_name.getText().toString();
        String lastname = profile_last_name.getText().toString();
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
        Toast.makeText(
            getApplicationContext(),
            getResources().getString(R.string.profile_update_message),
            Toast.LENGTH_LONG
        ).show();

        profile_display_name.setText(event.account.getDisplayName());
    }

    @Subscribe
    public void accountUpdateFailure(AccountManager.EventAccountUpdateFailure event){
        Toast.makeText(getApplicationContext(),event.errorMessage.toString(), Toast.LENGTH_LONG).show();
        updateProgress.cancel();
    }


    public boolean validateUpdateForm() {

        String email = profile_email.getText().toString();
        String firstname = profile_first_name.getText().toString();
        String lastname = profile_last_name.getText().toString();
        String password = profile_password.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(
                    getApplicationContext(),
                    getResources().getString(R.string.invalid_email_message),
                    Toast.LENGTH_LONG
            ).show();
            return false;
        }

        if (firstname.isEmpty() || lastname.isEmpty() || password.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.profile_fields_required_message),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}
