package io.github.SirWashington.features;

public class ConfigVariables {
    //How many internal water levels there are in a full block of water.
    public static int volumePerBlock = 100;
    //Only use powers of 2 for this setting, controls the rate at which a pair of blocks equalises.
    public static int equalisingRate = 4;
    //Log2 of equalisingRate, used for internal bitshift division.
    public static int equalisingDivider = 2;
    //Minimum water volume difference for flowing between a pair to happen.
    public static final int minimumFlowDifference = 2;
    //Divider, dictates the tickrate of water. 2 = 1 in every 2 game ticks.
    public static int tickRate = 1;


}
