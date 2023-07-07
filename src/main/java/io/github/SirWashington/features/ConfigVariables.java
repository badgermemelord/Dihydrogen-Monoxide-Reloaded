package io.github.SirWashington.features;

public class ConfigVariables {
    //How many internal water levels there are in a full block of water.
    public static final int volumePerBlock = 1000;
    //Only use powers of 2 for this setting, controls the rate at which a pair of blocks equalises.
    public static final int equalisingRate = 4;
    //Log2 of equalisingRate, used for internal bit shift division.
    public static final int equalisingDivider = 2;
    //Minimum water volume difference for flowing between a pair to happen.
    public static final int minimumFlowDifference = 2;
    //Threshold below which a water block will be considered a puddle.
    public static final int puddleThreshold = 20;
    //Divider, dictates the tick rate of water. 2 = 1 in every 2 game ticks.
    public static int tickRate = 2;


}
