package net.kalinovcic.kinetix.test;

import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.reaction.Reaction;

public class TestingConfiguration
{
	public TestingUnit head;
	
	public static class TestingUnit
	{
		public TestingUnit next;

		public int repeat;
		public double time;
		public double scale;
		
		public SimulationSettings settings;
	    public Reaction reaction;
	    public AtomType[] atomTypes;
	}
}
