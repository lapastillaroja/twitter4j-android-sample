package com.lapastillaroja.twittersample.resources;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import twitter4j.auth.AccessToken;

/**
 * Created by Antonio Abad on 2014/10/01.
 */
public class TwitterUserPreferences {

    private static final String PREFS_NAME_TAG = "twitterPreferences";
    private static final String SHARED_PREF_ACCESS_TOKEN = "accessToken";
    private static final String SHARED_PREF_LOGGED_IN = "loggedIn";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME_TAG, Context.MODE_PRIVATE);
    }

    public static AccessToken getAccessToken(Context context) {
        return new Gson().fromJson(
                getSharedPreferences(context).getString(SHARED_PREF_ACCESS_TOKEN, null),
                AccessToken.class);
    }

    public static boolean getUserLoggedIn(Context context) {
        return getSharedPreferences(context).getBoolean(
                        SHARED_PREF_LOGGED_IN, false);
    }

    public static void setAccessToken(@NonNull Context context, @NonNull AccessToken accessToken) {
        getSharedPreferences(context).edit()
                .putString(SHARED_PREF_ACCESS_TOKEN, new Gson()
                        .toJson(accessToken))
                .putBoolean(SHARED_PREF_LOGGED_IN, true).commit();
    }

    public static void clearAccessToken(@NonNull Context context) {
        getSharedPreferences(context).edit()
                .remove(SHARED_PREF_ACCESS_TOKEN)
                .remove(SHARED_PREF_LOGGED_IN).commit();
    }
}
