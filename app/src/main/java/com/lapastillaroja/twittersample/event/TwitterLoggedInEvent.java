package com.lapastillaroja.twittersample.event;

import android.support.annotation.NonNull;

import twitter4j.auth.AccessToken;

/**
 * Created by Antonio Abad on 2014/10/02.
 */
public class TwitterLoggedInEvent {

    @NonNull
    public final AccessToken mAccessToken;

    public TwitterLoggedInEvent(@NonNull AccessToken accessToken) {
        mAccessToken = accessToken;
    }
}
