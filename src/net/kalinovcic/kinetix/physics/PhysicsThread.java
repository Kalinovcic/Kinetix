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

        double[] sumProbabilities = new double[Reactions.ATOM_TYPE_COUNT];
        double[][] probabilities = new double[MAXIMUM_VELOCITY][Reactions.ATOM_TYPE_COUNT];
        
        double temperature = state.settings.temperature;
        for (int velocity = 0; velocity < MAXIMUM_VELOCITY; velocity++)
        {
        	int velocitySq = velocity * velocity;
        	
        	for (AtomType type : state.atomTypes)
        	{
        	    if (type == null) continue;
        		double mass = type.mass;

        		final double BOLTZMANN = 1.38064852;
        		final double DALTON = 1.660539040;
        		
        		double a = Math.pow((mass * DALTON * 0.0001) / (2 * Math.PI * BOLTZMANN * temperature), 1.5);
        		double b = Math.exp(-mass * DALTON * 0.0001 * velocitySq / (2 * BOLTZMANN * temperature));
        		double probability = 4 * Math.PI * a * velocitySq * b;
        		
        		probabilities[velocity][type.unique] = probability;
        		sumProbabilities[type.unique] += probability;
        	}
        }

        for (AtomType type : state.atomTypes)
        {
            if (type == null) continue;
            
            int count = type.initialCount;
            for (int i = 0; i < count; i++)
            {
                double x = random.nextDouble() * (state.settings.width - 2 * type.radius) + type.radius;
                double y = random.nextDouble() * (state.settings.height - 2 * type.radius) + type.radius;
    
                double randomArea = random.nextDouble() * sumProbabilities[type.unique];
                int velocity = 0;
                while (velocity < MAXIMUM_VELOCITY && randomArea > probabilities[velocity][type.unique])
                	randomArea -= probabilities[velocity++][type.unique];
                
                double angle = random.nextDouble() * Math.PI * 2;
                double vx = Math.sin(angle) * velocity;
                double vy = Math.cos(angle) * velocity;
    
                state.addAtom(new Atom(type, new Vector2(x, y), new Vector2(vx, vy)));
            }
        }
        
        if (Kinetix.testing == null)
        	state.paused = true;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
    	if (Kinetix.restart)
    	{
    		initializeState(state);
    		state.readyToUse = true;
            Kinetix.restart = false;
    	}
    	
        if (state.paused) return;

        double timeout = 1.0 / targetUPS;
    	if (deltaTime > timeout)
    		deltaTime = timeout;

        state.update(deltaTime);
        
        /*
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
        */
    }
}
