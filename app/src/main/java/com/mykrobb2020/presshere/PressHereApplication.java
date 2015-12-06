package com.mykrobb2020.presshere;

import android.app.Application;
import android.preference.PreferenceManager;

import com.mykrobb2020.presshere.constants.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Mike on 9/21/2015.
 */
public class PressHereApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "nkJQLtUUN0eHhMGmqrthwFmLByLqUw6djegvNine", "BwnTl5ioqp0UgrzN9OEKij7jGWTBQRPa8WLNUShH");
        ParseInstallation.getCurrentInstallation().saveInBackground();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    public static void updateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

}
