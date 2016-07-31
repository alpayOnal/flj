package com.muatik.flj.flj.UI.utilities;

import com.squareup.otto.Bus;

/**
 * Created by muatik on 25.07.2016.
 */
public class BusManager {
    private static Bus bus;

    public static Bus get() {
        if (bus == null)
            bus = new Bus();
        return bus;
    }
}
