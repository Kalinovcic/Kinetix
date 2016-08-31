package net.kalinovcic.kinetix;

import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.reaction.Reaction;

public class Kinetix
{
    public static final SimulationState STATE = new SimulationState();
    public static boolean restart = true;
    
    public static Reaction oneAndOnly = new Reaction();
    static
    {
    	oneAndOnly.reactant1 = "S0";
    	oneAndOnly.reactant2 = "O2";
		oneAndOnly.product1 = "S02";
		oneAndOnly.product2 = "O";
		oneAndOnly.mass1 = 48.016;
		oneAndOnly.mass2 = 32;
		oneAndOnly.radius1 = 2.375;
		oneAndOnly.radius2 = 2.125;
		oneAndOnly.temperatureRange_low = 440;
		oneAndOnly.temperatureRange_high = 2100;
		oneAndOnly.preExponentialFactor_experimental = 4.5e+8;
		oneAndOnly.b = 0;
		oneAndOnly.ratio = 3250;
		
		oneAndOnly.temperature = 1000;
		oneAndOnly.concentration1 = 0.02;
		oneAndOnly.concentration2 = 0.02;
		
		oneAndOnly.recalculate();
    }
}
