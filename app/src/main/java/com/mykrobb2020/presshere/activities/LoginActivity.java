package com.mykrobb2020.presshere.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mykrobb2020.presshere.PressHereApplication;
import com.mykrobb2020.presshere.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.usernameField) protected EditText mUserName;
    @InjectView(R.id.passwordField) protected EditText mPassword;
    @InjectView(R.id.progressBar) protected ProgressBar mProgressBar;
    @InjectView(R.id.yellow_circle) protected ImageView mYellowCircle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);
        getSupportActionBar().hide();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.loginButton)
    public void login() {
        String username = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.login_error_message);
            builder.setTitle(R.string.error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    mProgressBar.setVisibility(View.GONE);
                    if (e == null) {
                        PressHereApplication.updateParseInstallation(parseUser);
                        Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

    @OnClick(R.id.signUpText)
    public void startSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

}
