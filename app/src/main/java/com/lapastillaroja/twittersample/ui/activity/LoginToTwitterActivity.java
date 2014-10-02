package com.lapastillaroja.twittersample.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lapastillaroja.twittersample.R;
import com.lapastillaroja.twittersample.resources.TwitterUserPreferences;
import com.lapastillaroja.twittersample.sns.TwitterConfig;

import butterknife.InjectView;

public class LoginToTwitterActivity extends Activity {

    public static final String EXTRA_AUTH_URL_KEY = ".extra.authUrlKey";
    public static final String EXTRA_CALLBACK_URL_KEY = ".extra.callbackUrlKey";

    public static final int REQUEST_CODE_LOGIN_TO_TWITTER = 1;

    @InjectView(R.id.webViewLoginToTwitter)
    WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_to_twitter);

        Intent intent = getIntent();
        String mUrl = intent.getStringExtra(EXTRA_AUTH_URL_KEY);

        mWebView = (WebView) findViewById(R.id.webViewLoginToTwitter);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new LoginToTwitterWebViewClient());

        mWebView.loadUrl(mUrl);
    }

    private class LoginToTwitterWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(TwitterConfig.OAUTH_CALLBACK_URL)) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CALLBACK_URL_KEY, url);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
            return false;
        }
    }
}
