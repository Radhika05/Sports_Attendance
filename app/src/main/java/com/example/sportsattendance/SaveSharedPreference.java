package com.example.sportsattendance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreference {

    static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     */
    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean("LOGGED_IN_PREF", loggedIn);
        editor.apply();
    }

    public static void setLoggedInUserId(Context context, long loggedInUserID) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong("LOGGED_IN_USER_ID", loggedInUserID);
        editor.apply();
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean("LOGGED_IN_PREF", false);
    }

    public static long getLoggedUserID(Context context) {
        return getPreferences(context).getLong("LOGGED_IN_USER_ID",0);
    }
}