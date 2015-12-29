package com.mykrobb2020.presshere.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.mykrobb2020.presshere.PressHereApplication;
import com.mykrobb2020.presshere.R;
import com.mykrobb2020.presshere.constants.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @InjectView(R.id.usernameField) protected EditText mUsernameField;
    @InjectView(R.id.passwordField) protected EditText mPasswordField;
    @InjectView(R.id.emailField) protected EditText mEmailField;
    @InjectView(R.id.progressBar) protected ProgressBar mProgressBar;

    private String username;
    private String password;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.inject(this);
        getSupportActionBar().hide();
    }

    @OnClick(R.id.signUpButton)
    public void signUp() {
        username = mUsernameField.getText().toString().trim();
        password = mPasswordField.getText().toString().trim();
        email = mEmailField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.signup_incomplete_fields_message);
            builder.setTitle(R.string.error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_USER_AUTHENTICATION);
            query.whereEqualTo(ParseConstants.KEY_EMAIL, email);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {
                        if (scoreList.isEmpty()) {
                            mProgressBar.setVisibility(View.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(R.string.signup_auth_failed_message);
                            builder.setTitle(R.string.error_title);
                            builder.setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
                            final ParseUser newUser = new ParseUser();
                            newUser.setUsername(username);
                            newUser.setPassword(password);
                            newUser.setEmail(email);
                            newUser.signUpInBackground(new SignUpCallback() {
                                @Override
                                public void done(ParseException e) {
                                    mProgressBar.setVisibility(View.GONE);
                                    if (e == null) {
                                        PressHereApplication.updateParseInstallation(ParseUser.getCurrentUser());
                                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                        builder.setMessage(e.getMessage());
                                        builder.setTitle(R.string.error_title);
                                        builder.setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }
                                }
                            });
                        }
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
