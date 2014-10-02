package com.lapastillaroja.twittersample.event;

import android.support.annotation.NonNull;

import twitter4j.User;

/**
 * Created by Antonio Abad on 2014/10/02.
 */
public class TwitterShowUserEvent {

    @NonNull
    public User mUser;

    public TwitterShowUserEvent(@NonNull User user) {
        mUser = user;
    }
}
