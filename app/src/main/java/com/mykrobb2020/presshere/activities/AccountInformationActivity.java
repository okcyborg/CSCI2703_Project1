package com.mykrobb2020.presshere.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mykrobb2020.presshere.PressHereApplication;
import com.mykrobb2020.presshere.R;
import com.mykrobb2020.presshere.constants.AppConstants;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class AccountInformationActivity extends AppCompatActivity {

    private ParseUser mCurrentUser;
    @InjectView(R.id.usernameTextView) protected TextView mUserName;
    @InjectView(R.id.emailTextView) protected TextView mEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = PressHereApplication.getCurrentParseUser();
        mUserName.setText(mCurrentUser.getUsername());
        mEmail.setText(mCurrentUser.getEmail());
    }

    @OnClick(R.id.cancelButton)
    public void cancel() {
        finish();
    }

    @OnClick(R.id.editUsername)
    public void editEmail() {
        Intent intent = new Intent(this, EditAccountInformationActivity.class);
        intent.putExtra(AppConstants.EDIT_ACCOUNT_FIELD, AppConstants.EDIT_ACCOUNT_USERNAME);
        startActivity(intent);
    }

    @OnClick(R.id.editPassword)
    public void editPassword() {
        Intent intent = new Intent(this, EditAccountInformationActivity.class);
        intent.putExtra(AppConstants.EDIT_ACCOUNT_FIELD, AppConstants.EDIT_ACCOUNT_PASSWORD);
        startActivity(intent);
    }
}
