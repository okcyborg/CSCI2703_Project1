package com.mykrobb2020.presshere.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mykrobb2020.presshere.PressHereApplication;
import com.mykrobb2020.presshere.R;
import com.mykrobb2020.presshere.constants.ParseConstants;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SurveyActivity extends AppCompatActivity {

    private int mStressLevel;
    private ParseUser mCurrentUser;
    public static final String TAG = SurveyActivity.class.getSimpleName();
    @InjectView(R.id.progressBar) protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = PressHereApplication.getCurrentParseUser();
        if (!mCurrentUser.getBoolean(ParseConstants.KEY_SURVEY_ELIGIBLE)) {
            Intent intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_survey, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_logout == item.getItemId()) {
            ParseUser.logOut();
            navigateToLogin();
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.stress_level_1:
                if (checked) {
                    mStressLevel = 1;
                }
                break;
            case R.id.stress_level_2:
                if (checked) {
                    mStressLevel = 2;
                }
                break;
            case R.id.stress_level_3:
                if (checked) {
                    mStressLevel = 3;
                }
                break;
            case R.id.stress_level_4:
                if (checked) {
                    mStressLevel = 4;
                }
                break;
            case R.id.stress_level_5:
                if (checked) {
                    mStressLevel = 5;
                }
                break;
        }
    }

    public void submitSurvey(View view) {
        if (mStressLevel == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.survey_error_msg);
            builder.setTitle(R.string.error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            final ParseObject holdEvent = new ParseObject(ParseConstants.CLASS_SURVEY_ANSWERS);
            holdEvent.put(ParseConstants.KEY_STRESS_LEVEL, mStressLevel);
            holdEvent.put(ParseConstants.KEY_USER_ID, mCurrentUser.getObjectId());
            holdEvent.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                mProgressBar.setVisibility(View.GONE);
                if (e == null) {
                    mCurrentUser.put(ParseConstants.KEY_SURVEY_ELIGIBLE, false);
                    mCurrentUser.saveInBackground();
                    if (mCurrentUser.getBoolean(ParseConstants.KEY_CONTROL_USER)) {
                        setContentView(R.layout.message_layout);
                        ((TextView)findViewById(R.id.messageTextView)).setText("Your survey has been submitted, nothing more to do");
                    } else {
                        Toast.makeText(getBaseContext(), "Survey submitted", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SurveyActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SurveyActivity.this);
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

}
