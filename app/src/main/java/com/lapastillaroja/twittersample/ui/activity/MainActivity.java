package com.lapastillaroja.twittersample.ui.activity;

import com.lapastillaroja.twittersample.R;
import com.lapastillaroja.twittersample.event.BusProvider;
import com.lapastillaroja.twittersample.event.TwitterLoggedInEvent;
import com.lapastillaroja.twittersample.event.TwitterLoggedOutEvent;
import com.lapastillaroja.twittersample.resources.TwitterUserPreferences;
import com.squareup.otto.Subscribe;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class MainActivity extends Activity {

    @InjectView(R.id.button_post)
    Button mButtonPost;
    @InjectView(R.id.twitter_post_text)
    EditText mEditTextPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);

        if (TwitterUserPreferences.getUserLoggedIn(this)) {
            updateLoggedInUI();
        } else {
            updateLoggedOutUI();
        }
    }

    @Override
    protected void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
    }

    @OnClick(R.id.button_post)
    public void onOnClickPost(View view) {
        new TwitterPostTask().execute(mEditTextPost.getText().toString());
    }

    @OnClick(R.id.button_next_activity)
    public void onOnClickNextActivity(View view) {
        SecondaryActivity.startActivity(this);
    }

    @Subscribe
    public void onTwitterLoggedInEvent(TwitterLoggedInEvent event) {
        updateLoggedInUI();
    }

    @Subscribe
    public void onTwitterLoggedOutEvent(TwitterLoggedOutEvent event) {
        updateLoggedOutUI();
    }

    private void updateLoggedInUI() {
        mButtonPost.setEnabled(true);
    }

    private void updateLoggedOutUI() {
        mButtonPost.setEnabled(false);
    }

    private class TwitterPostTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                Twitter twitter = TwitterFactory.getSingleton();
                twitter.setOAuthAccessToken(
                        TwitterUserPreferences.getAccessToken(MainActivity.this));

                twitter.updateStatus(strings[0]);
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //FIXME
            Toast.makeText(MainActivity.this, "Status updated", Toast.LENGTH_SHORT).show();
            mEditTextPost.setText("");
        }
    }
}
