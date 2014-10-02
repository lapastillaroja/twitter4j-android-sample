package com.lapastillaroja.twittersample.sns;

import android.support.annotation.NonNull;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by Antonio Abad on 2014/10/01.
 */
public class TwitterConfig {

    private static final Profile PROFILE = Profile.load();

    public static final String OAUTH_CONSUMER_KEY = PROFILE.getString("oauth.consumerKey");
    public static final String OAUTH_CONSUMER_SECRET = PROFILE.getString("oauth.consumerSecret");
    public static final String OAUTH_CALLBACK_URL = PROFILE.getString("oauth.callbackUrl");

    private static class Profile {

        private final ResourceBundle bundle;

        private Profile(@NonNull String name) {
            bundle = ResourceBundle.getBundle(name);
        }

        public static Profile load() {
            return new Profile("twitter4j");
        }

        public String getString(@NonNull String key) {
            return getProperty(key);
        }

        public String getString(@NonNull String key, @NonNull Object... args) {
            return String.format(getString(key), args);
        }

        public int getInt(@NonNull String key) {
            return Integer.valueOf(getString(key));
        }

        public long getLong(@NonNull String key) {
            return Long.valueOf(getString(key));
        }

        public boolean getBoolean(@NonNull String key) {
            return Boolean.valueOf(getString(key));
        }

        private String getProperty(@NonNull String key) {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException mre) {
                throw new IllegalArgumentException(key + "is not found.", mre);
            }
        }
    }

}
