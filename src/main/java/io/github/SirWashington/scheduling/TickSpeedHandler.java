package io.github.SirWashington.scheduling;

import io.github.SirWashington.features.ConfigVariables;

public class TickSpeedHandler {
    static int counter = 0;
    static int n = ConfigVariables.tickRate;
    public static boolean shouldTick() {
        boolean returnValue = false;
        if(counter%n == 0) {
            returnValue = true;
            counter = 0;
        }
        counter += 1;
        return returnValue;
    }


}
