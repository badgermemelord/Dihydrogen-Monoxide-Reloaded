package io.github.SirWashington.scheduling;

public class TickSpeedHandler {

    static int counter = 0;
    static int n = 8;

    public static boolean shouldTick() {
        boolean returnValue = false;
        if(counter%n == 0) {
            returnValue = true;
            counter = 0;
        }
        counter += 1;
        //System.out.println(returnValue);
        return returnValue;
    }


}
