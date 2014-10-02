package com.lapastillaroja.twittersample.event;

import com.squareup.otto.Bus;

/**
 * otto bus provider class holding BUS object as singleton
 *
 * Created by Antonio Abad on 2014/08/26.
 */
public final class BusProvider {

    private static final Bus BUS = new Bus();

    private BusProvider() {
        // No instances.
    }

    public static Bus getInstance() {
        return BUS;
    }
}