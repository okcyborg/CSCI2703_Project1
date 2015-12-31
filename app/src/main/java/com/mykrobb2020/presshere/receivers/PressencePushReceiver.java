package com.mykrobb2020.presshere.receivers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.mykrobb2020.presshere.activities.SurveyActivity;

/**
 * Created by Mike on 12/31/2015.
 */
public class PressencePushReceiver extends com.parse.ParsePushBroadcastReceiver{

    @Override
    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        return SurveyActivity.class;
    }
}
