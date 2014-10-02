package com.lapastillaroja.twittersample.ui.fragment;

import com.lapastillaroja.twittersample.R;
import com.lapastillaroja.twittersample.event.BusProvider;
import com.lapastillaroja.twittersample.event.TwitterErrorEvent;
import com.lapastillaroja.twittersample.event.TwitterLoggedInEvent;
import com.lapastillaroja.twittersample.event.TwitterLoggedOutEvent;
import com.lapastillaroja.twittersample.event.TwitterShowUserEvent;
import com.lapastillaroja.twittersample.resources.TwitterUserPreferences;
import com.lapastillaroja.twittersample.ui.activity.LoginToTwitterActivity;
import com.squareup.otto.Subscribe;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by Antonio Abad on 2014/10/01.
 */
public class TwitterFragment extends Fragment {

    private static final String PREFS_NAME_TAG = "twitterFragmentPreferences";
    private static final String SHARED_PREF_SWITCH_TWITTER_STATUS = "switchTwitterStatus";

    @InjectView(R.id.button_logout)
    Button mButtonLogout;
    @InjectView(R.id.user_name)
    TextView mUserName;
    @InjectView(R.id.switch_twitter)
    Switch mSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitter, container, false);

        ButterKnife.inject(this, view);

        if (TwitterUserPreferences.getUserLoggedIn(getActivity()) && getSwitchTwitterChecked()) {
            mSwitch.setChecked(true);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        if (TwitterUserPreferences.getUserLoggedIn(getActivity())) {
            new GetTwitterUser().execute();
        } else {
            updateLoggedOutUI();
        }
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        setSwitchTwitterChecked(mSwitch.isChecked());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginToTwitterActivity.REQUEST_CODE_LOGIN_TO_TWITTER) {
            if (resultCode == Activity.RESULT_OK) {
                getAccessToken(data.getStringExtra(LoginToTwitterActivity.EXTRA_CALLBACK_URL_KEY));
            } else {
                //TODO
            }
        }
    }

    @OnClick(R.id.button_logout)
    public void onClickLogout(View view) {
        //FIXME refactor
        TwitterUserPreferences.clearAccessToken(getActivity());

        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthAccessToken(null);
        updateLoggedOutUI();

        mSwitch.setChecked(false);
        Toast.makeText(getActivity(), "Logout", Toast.LENGTH_SHORT).show();

        BusProvider.getInstance().post(new TwitterLoggedOutEvent());
    }

    @OnCheckedChanged(R.id.switch_twitter)
    public void onCheckedChangedSwitch(boolean checked) {
        Toast.makeText(getActivity(), "Checked OnCheckedChanged:" + Boolean.toString(checked),
                Toast.LENGTH_SHORT).show();

        //FIXME
        setSwitchTwitterChecked(checked);

        if (!TwitterUserPreferences.getUserLoggedIn(getActivity())) {
            new TwitterLoginTask().execute();
        }
    }

    private void launchLoginWebView(@NonNull RequestToken requestToken) {

        Intent intent = new Intent(getActivity(), LoginToTwitterActivity.class);
        intent.putExtra(LoginToTwitterActivity.EXTRA_AUTH_URL_KEY,
                requestToken.getAuthenticationURL());
        startActivityForResult(intent, LoginToTwitterActivity.REQUEST_CODE_LOGIN_TO_TWITTER);
    }

    public void getAccessToken(@NonNull final String callbackUrl) {

        Uri uri = Uri.parse(callbackUrl);
        String verifier = uri.getQueryParameter("oauth_verifier");

        GetAccessTokenTask getAccessTokenTask = new GetAccessTokenTask();
        getAccessTokenTask.execute(verifier);
    }

    private class TwitterLoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                RequestToken requestToken = twitter.getOAuthRequestToken();

                launchLoginWebView(requestToken);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private class GetAccessTokenTask extends AsyncTask<String, Void, AccessToken> {

        @Override
        protected AccessToken doInBackground(String... strings) {
            String verifier = strings[0];
            AccessToken accessToken = null;
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                accessToken = twitter.getOAuthAccessToken(verifier);
            } catch (Exception e) {
                e.printStackTrace();
                BusProvider.getInstance().post(new TwitterErrorEvent());
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);
            Toast.makeText(getActivity(), "Login completed", Toast.LENGTH_SHORT).show();
            BusProvider.getInstance().post(new TwitterLoggedInEvent(accessToken));
        }
    }

    private class GetTwitterUser extends AsyncTask<String, Void, User> {

        @Override
        protected User doInBackground(String... strings) {

            User user = null;
            try {
                // Getting user details from twitter
                Twitter twitter = TwitterFactory.getSingleton();
                twitter.setOAuthAccessToken(TwitterUserPreferences.getAccessToken(getActivity()));
                user = twitter.showUser(
                        TwitterUserPreferences.getAccessToken(getActivity()).getUserId());
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return user;
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);
            BusProvider.getInstance().post(new TwitterShowUserEvent(user));
        }
    }

    @Subscribe
    public void onTwitterErrorEvent(TwitterErrorEvent event) {
        updateLoggedOutUI();
    }

    @Subscribe
    public void onTwitterLoggedinEvent(TwitterLoggedInEvent event) {
        // store the access token and access token secret in application preferences
        TwitterUserPreferences.setAccessToken(getActivity(), event.mAccessToken);
        new GetTwitterUser().execute();
    }

    @Subscribe
    public void onTwitterShowUserEvent(TwitterShowUserEvent event) {
        updateLoggedInUI(event.mUser);
    }

    private void updateLoggedInUI(User user) {
        mButtonLogout.setVisibility(View.VISIBLE);
        mUserName.setText(String
                .format(getResources().getString(R.string.twitter_logged_in), user.getName()));
    }

    private void updateLoggedOutUI() {
        mButtonLogout.setVisibility(View.GONE);
        mUserName.setText(getResources().getText(R.string.twitter_no_login));
    }

    private boolean getSwitchTwitterChecked() {
        return getActivity().getSharedPreferences(PREFS_NAME_TAG, Context.MODE_PRIVATE)
                .getBoolean(SHARED_PREF_SWITCH_TWITTER_STATUS, false);
    }

    private void setSwitchTwitterChecked(boolean checked) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME_TAG,
                Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(SHARED_PREF_SWITCH_TWITTER_STATUS, checked).commit();
    }
}
