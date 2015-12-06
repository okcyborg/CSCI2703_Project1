package com.mykrobb2020.presshere.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mykrobb2020.presshere.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WelcomeActivity extends AppCompatActivity {

    @InjectView(R.id.continueText) protected TextView mContinueText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.inject(this);
        getSupportActionBar().hide();
    }

    @OnClick(R.id.continueText)
    public void continueToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
