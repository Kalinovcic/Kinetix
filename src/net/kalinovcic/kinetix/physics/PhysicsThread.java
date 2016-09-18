package net.kalinovcic.kinetix.physics;

import java.util.Random;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.math.Vector2;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class PhysicsThread extends KinetixThread
{
    public PhysicsThread()
    {
        super(60);
    }
    
    @Override
    public void initialize()
    {
    }
    
    @Override
    public void synchronizedInitialize(SimulationState state)
    {
        state.paused = true;
    }
    
    private void initializeState(SimulationState state)
    {
        Random random = new Random();
        
        state.reset();
        
        final int MAXIMUM_VELOCITY = 5000;

        int reactant1 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant1);
        int reactant2 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant2);
        int[] reactants = new int[]{ reactant1, reactant2 };
        
        double[] sumProbabilities = new double[Reactions.ATOM_TYPE_COUNT];
        double[][] probabilities = new double[MAXIMUM_VELOCITY][Reactions.ATOM_TYPE_COUNT];
        
        double temperature = Kinetix.reaction.temperature;
        for (int velocity = 0; velocity < MAXIMUM_VELOCITY; velocity++)
        {
        	int velocitySq = velocity * velocity;
        	
        	for (int type : reactants)
        	{
        		double mass = (type == reactant1) ? Kinetix.reaction.mass1 : Kinetix.reaction.mass2;

        		final double BOLTZMANN = 1.38064852;
        		final double DALTON = 1.660539040;
        		
        		double a = Math.pow((mass * DALTON * 0.0001) / (2 * Math.PI * BOLTZMANN * Kinetix.reaction.temperature), 1.5);
        		double b = Math.exp(-mass * DALTON * 0.0001 * velocitySq / (2 * BOLTZMANN * temperature));
        		double probability = 4 * Math.PI * a * velocitySq * b;
        		
        		probabilities[velocity][type] = probability;
        		sumProbabilities[type] += probability;
        	}
        }
        
        for (int i = 0; i < state.settings.redCount + state.settings.greenCount; i++)
        {
        	int type = (i < state.settings.redCount) ? reactant1 : reactant2;
        	double radius = (type == reactant1) ? Kinetix.reaction.radius1 : Kinetix.reaction.radius2;
        	double mass = (type == reactant1) ? Kinetix.reaction.mass1 : Kinetix.reaction.mass2;

            double x = random.nextDouble()*(state.settings.width - 2*radius) + radius;
            double y = random.nextDouble()*(state.settings.height - 2*radius) + radius;

            double randomArea = random.nextDouble() * sumProbabilities[type];
            int velocity = 0;
            while (velocity < MAXIMUM_VELOCITY && randomArea > probabilities[velocity][type])
            	randomArea -= probabilities[velocity++][type];
            
            double angle = random.nextDouble() * Math.PI * 2;
            double vx = Math.sin(angle) * velocity;
            double vy = Math.cos(angle) * velocity;

            state.addAtom(new Atom(type, new Vector2(x, y), new Vector2(vx, vy), radius, mass));
        }
        
        if (Kinetix.testing == null)
        	state.paused = true;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
    	if (Kinetix.restart)
    	{
    		Kinetix.restart = false;
    		initializeState(state);
    	}
    	
        if (state.paused) return;

        double timeout = 1.0 / targetUPS;
    	if (deltaTime > timeout)
    		deltaTime = timeout;

        state.update(deltaTime);
        
        if (Kinetix.testing != null)
        {
        	TestingConfiguration.TestingUnit unit = Kinetix.testing.currentUnit;
        	
        	unit.timeRemaining -= deltaTime;
        	if (unit.timeRemaining <= 0.0)
        	{
        		unit.timeRemaining = unit.time;

                int reactant1 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant1);
                int reactant2 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant2);
                int reactions = state.collisionInfo[reactant1][reactant2][1];
        		unit.countReactions[unit.repeat - unit.repeatRemaining] = reactions;
        		unit.repeatRemaining--;
        		if (unit.repeatRemaining <= 0)
        		{
        			unit.repeatRemaining = unit.repeat;
        			
        			Kinetix.testing.currentUnit = unit.next;
        			if (unit.next == null)
        			{
        				Kinetix.testing.writeOut();
        				Kinetix.testing = null;
        			}
        		}
        		
    			Kinetix.restart = true;
        	}
        }
    }
}
