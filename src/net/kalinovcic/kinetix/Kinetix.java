package net.kalinovcic.kinetix;

import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.TestingConfiguration;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class Kinetix
{
    public static final SimulationState STATE = new SimulationState();
    public static TestingConfiguration testing = null;
    public static boolean restart = true;
    
    public static Reaction reaction;
    static
    {
    	reaction = Reactions.reactions.get(0);
		
		reaction.temperature = 1000;
		reaction.concentration1 = 0.02;
		reaction.concentration2 = 0.02;
		
		reaction.recalculate();
    }
}
