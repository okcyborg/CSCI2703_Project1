package com.mykrobb2020.presshere.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mykrobb2020.presshere.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditLoginInformation extends AppCompatActivity {

    private ParseUser mCurrentUser;
    @InjectView(R.id.usernameField) protected EditText mUserName;
    @InjectView(R.id.passwordField) protected EditText mPassword;
    @InjectView(R.id.passwordReentryField) protected EditText mPasswordReentry;
    @InjectView(R.id.emailField) protected EditText mEmail;
    @InjectView(R.id.progressBar) protected ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_login_information);

        ButterKnife.inject(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mUserName.setText(mCurrentUser.getUsername());
        mEmail.setText(mCurrentUser.getEmail());
    }

    @OnClick(R.id.submitButton)
    public void submitChanges() {
        String username = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String passwordReentry = mPasswordReentry.getText().toString().trim();
        String email = mEmail.getText().toString().trim();

        if (username.isEmpty() || (password.isEmpty() ^ passwordReentry.isEmpty()) || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.signup_incomplete_fields_message);
            builder.setTitle(R.string.error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!password.equals(passwordReentry)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.password_error_message);
            builder.setTitle(R.string.error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mCurrentUser.setUsername(username);
            mCurrentUser.setEmail(email);
            if (!password.isEmpty()) {
                mCurrentUser.setPassword(password);
            }

            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    mProgressBar.setVisibility(View.GONE);
                    if (e == null) {
                        Intent intent = new Intent(EditLoginInformation.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditLoginInformation.this);
                        builder.setMessage(e.getMessage());
                        builder.setTitle(R.string.error_title);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            });
        }
    }

    @OnClick(R.id.cancelButton)
    public void cancel() {
        finish();
    }

}
