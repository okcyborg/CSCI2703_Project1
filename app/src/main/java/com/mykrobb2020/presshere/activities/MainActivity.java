package com.mykrobb2020.presshere.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mykrobb2020.presshere.constants.ParseConstants;
import com.mykrobb2020.presshere.views.MyView;
import com.mykrobb2020.presshere.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @InjectView(R.id.mainContainer) RelativeLayout mMainContainer;
    @InjectView(R.id.circleView) MyView mCircleView;
    @InjectView(R.id.circleText) TextView mCircleText;
    @InjectView(R.id.completionTextContainer) RelativeLayout mCompletionTextContainer;

    private ParseUser mCurrentUser;
    private float lengthOfTime;
    private ValueAnimator mColorAnimator;
    private boolean performingCompletionSteps;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!performingCompletionSteps && !((MyView) view).isResettingCircle()) {
            if (MotionEvent.ACTION_DOWN == motionEvent.getActionMasked()) {
                toggleTextViewVisiblity();
                ((MyView) view).startDrawingArc();
            } else if (MotionEvent.ACTION_UP == motionEvent.getActionMasked()) {
                ((MyView) view).stopDrawingArc();
            }
        }
//      startColorAnimation(2000);

        return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //some changes here
        //other changes here
        //a third change here
        //a fourth change here
        //a fifth change here
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        navigateToSurvey();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            navigateToLogin();
        }

        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            lengthOfTime = Float.parseFloat(mySharedPreferences.getString(getString(R.string.lengthOfTimeKey), "1"));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception");
            lengthOfTime = 1;
        }

        mCircleView.setLengthOfTime(lengthOfTime);
        mCircleView.setOnTouchListener(mOnTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);

                break;
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggleTextViewVisiblity() {
        if (View.VISIBLE == mCircleText.getVisibility()) {
            mCircleText.setVisibility(View.INVISIBLE);
        } else {
            mCircleText.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToSurvey() {
        Intent intent = new Intent(this, SurveyActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void resetColorAnimator() {
        if (mColorAnimator != null && mColorAnimator.isRunning()) {
            mColorAnimator.cancel();
        }

        int colorStart = ((ColorDrawable)mMainContainer.getBackground()).getColor();
        int colorEnd = colorStart == ContextCompat.getColor(this, R.color.main_background) && !mCircleView.isResettingCircle() ?
                ContextCompat.getColor(this, R.color.main_transition_color) : ContextCompat.getColor(this, R.color.main_background);

        mColorAnimator = ObjectAnimator.ofInt(mMainContainer, "backgroundColor", colorStart, colorEnd);
        mColorAnimator.setEvaluator(new ArgbEvaluator());
        mColorAnimator.setRepeatCount(0);
    }

    public void startColorAnimation(long duration) {
        resetColorAnimator();
        mColorAnimator.setDuration(duration);
        mColorAnimator.start();
    }

    public void performPostHoldCompletionSteps() {
        performingCompletionSteps = true;
        recordHoldCompletion();
        activateCompletionText();
    }

    public void recordHoldCompletion() {
        final ParseObject holdEvent = new ParseObject(ParseConstants.CLASS_HOLD_EVENT);
        holdEvent.put(ParseConstants.KEY_DURATION, lengthOfTime);
        holdEvent.put(ParseConstants.KEY_USER_ID, mCurrentUser.getObjectId());
        holdEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseRelation<ParseObject> userHoldEvent = mCurrentUser.getRelation(ParseConstants.KEY_USER_HOLD_EVENT);
                    userHoldEvent.add(holdEvent);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Unable to create UserHoldEvent");
                            }
                        }
                    });

                } else {
                    Log.e(TAG, "Unable to create HoldEvent");
                }
            }
        });
    }

    public void activateCompletionText() {
        final TextView completionText = new TextView(MainActivity.this);
        completionText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        completionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        completionText.setText(getResources().getText(R.string.completion_text));
        completionText.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        mCompletionTextContainer.addView(completionText);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCompletionTextContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        mCompletionTextContainer.removeView(completionText);
                        mCircleView.stopDrawingArc();
                        performingCompletionSteps = false;
                    }
                });
            }
        }, 4000);
    }

}
