package com.mykrobb2020.presshere.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mykrobb2020.presshere.PressHereApplication;
import com.mykrobb2020.presshere.R;
import com.mykrobb2020.presshere.constants.AppConstants;
import com.mykrobb2020.presshere.constants.ParseConstants;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class EditAccountInformationActivity extends AppCompatActivity {

    @InjectView(R.id.editUsernameWrapper) protected LinearLayout mEditUsernameWrapper;
    @InjectView(R.id.editPasswordWrapper) protected LinearLayout mEditPasswordWrapper;
    @InjectView(R.id.editReenterPasswordWrapper) protected LinearLayout mEditReenterPasswordWrapper;
    @InjectView(R.id.usernameField) protected EditText mUserNameField;
    @InjectView(R.id.passwordField) protected EditText mPasswordField;
    @InjectView(R.id.reenteredPasswordField) protected EditText mReenteredPasswordField;
    @InjectView(R.id.progressBar) protected ProgressBar mProgressBar;


    private String mEditAccountField;
    private String mUserNameValue;
    private String mPasswordValue;
    private String mReenteredPasswordValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account_information);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        mEditAccountField = intent.getStringExtra(AppConstants.EDIT_ACCOUNT_FIELD);
        if (AppConstants.EDIT_ACCOUNT_USERNAME.equals(mEditAccountField)
                || (savedInstanceState != null && AppConstants.EDIT_ACCOUNT_USERNAME.equals(savedInstanceState.getString(AppConstants.EDIT_ACCOUNT_FIELD)))) {
            mEditUsernameWrapper.setVisibility(View.VISIBLE);
            mEditPasswordWrapper.setVisibility(View.GONE);
            mEditReenterPasswordWrapper.setVisibility(View.GONE);
        } else {
            mEditPasswordWrapper.setVisibility(View.VISIBLE);
            mEditReenterPasswordWrapper.setVisibility(View.VISIBLE);
            mEditUsernameWrapper.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.cancelButton)
    public void cancel() {
        finish();
    }

    @OnClick(R.id.submitButton)
    public void submitChanges() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        mUserNameValue = mUserNameField.getText().toString().trim();
        mPasswordValue = mPasswordField.getText().toString().trim();
        mReenteredPasswordValue = mReenteredPasswordField.getText().toString().trim();

        if (AppConstants.EDIT_ACCOUNT_USERNAME.equals(mEditAccountField) && verifyUserName()) {
            currentUser.setUsername(mUserNameValue);
        } else if (AppConstants.EDIT_ACCOUNT_PASSWORD.equals(mEditAccountField) && verifyPassword()) {
            currentUser.setPassword(mPasswordValue);
        } else {
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (AppConstants.EDIT_ACCOUNT_PASSWORD.equals(mEditAccountField)) {
                        ParseUser.logInInBackground(currentUser.getUsername(), mPasswordValue, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (e == null) {
                                    PressHereApplication.updateParseInstallation(parseUser);
                                    mProgressBar.setVisibility(View.GONE);
                                    Toast.makeText(EditAccountInformationActivity.this, R.string.changes_successful, Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    showDialog(e.getMessage(), 0, R.string.error_title);
                                }
                            }
                        });
                    } else {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(EditAccountInformationActivity.this, R.string.changes_successful, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    showDialog(e.getMessage(), 0, R.string.error_title);
                }
            }
        });

    }

    private boolean verifyUserName() {
        if (mUserNameValue.isEmpty()) {
            showDialog(null, R.string.edit_account_username_empty, R.string.error_title);
            return false;
        }

        return true;
    }

    private boolean verifyPassword() {
        if (mPasswordValue.isEmpty() ^ mReenteredPasswordValue.isEmpty()) {
            showDialog(null, R.string.edit_account_password_empty, R.string.error_title);
            return false;
        } else if (!mPasswordValue.equals(mReenteredPasswordValue)) {
            showDialog(null, R.string.password_error_message, R.string.error_title);
            return false;
        }

        return true;
    }

    private void showDialog(String messageString, int messageId, int titleId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (messageString != null) {
            builder.setMessage(messageString);
        } else {
            builder.setMessage(messageId);
        }
        builder.setMessage(messageId);
        builder.setTitle(titleId);
        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(AppConstants.EDIT_ACCOUNT_FIELD, mEditAccountField);
        super.onSaveInstanceState(savedInstanceState);
    }
}
