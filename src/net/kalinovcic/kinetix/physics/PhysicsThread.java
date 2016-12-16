package net.kalinovcic.kinetix.physics;

import java.util.Locale;
import java.util.Random;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.commander.CommanderWindow;
import net.kalinovcic.kinetix.math.Distribution;
import net.kalinovcic.kinetix.math.Distribution.Packing;
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
        
        final int MAXIMUM_VELOCITY = 10000;

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

        int equalAmountsTemp = -1;
        boolean equalAmounts = true;
        int[] amountPlaced = new int[Reactions.ATOM_TYPE_COUNT];
        int totalCount = 0;
        for (AtomType type : state.atomTypes)
            if (type != null)
            {
                totalCount += type.initialCount;
                amountPlaced[type.unique] = 0;
                if (type.initialCount > 0)
                {
                    if (equalAmountsTemp == -1)
                        equalAmountsTemp = type.initialCount; 
                    else if (equalAmountsTemp != type.initialCount)
                        equalAmounts = false;
                }
            }
        equalAmountsTemp = 0;

        Packing packing = Distribution.findOptimalPacking(state.settings.width, state.settings.height, totalCount);
        int gridX = 0;
        int gridY = 0;
        
        for (int i = 0; i < totalCount; i++)
        {
            AtomType type;
            int typeIndex;
            if (equalAmounts)
            {
                do
                {
                    equalAmountsTemp++;
                    if (equalAmountsTemp >= state.atomTypes.length)
                        equalAmountsTemp = 0;
                    typeIndex = equalAmountsTemp;
                    type = state.atomTypes[typeIndex];
                }
                while (type == null || type.initialCount == 0);
            }
            else
            {
                do
                {
                    typeIndex = random.nextInt(state.atomTypes.length);
                    type = state.atomTypes[typeIndex];
                }
                while (type == null || amountPlaced[type.unique] >= type.initialCount);
                amountPlaced[type.unique]++;
            }
            
            double x = packing.startX + packing.width * 0.5 * (gridY % 2) + packing.width * gridX;
            double y = packing.startY + packing.height * gridY;
            
            gridX++;
            if ((gridY % 2) == 0 ? (gridX >= packing.countX) : (gridX >= packing.countX - 1))
            {
                gridX = 0;
                gridY++;
            }
            
            // double x = random.nextDouble() * (state.settings.width - 2 * type.radius) + type.radius;
            // double y = random.nextDouble() * (state.settings.height - 2 * type.radius) + type.radius;

            double randomArea = random.nextDouble() * sumProbabilities[type.unique];
            int velocity = 0;
            while (velocity < MAXIMUM_VELOCITY && randomArea > probabilities[velocity][type.unique])
                randomArea -= probabilities[velocity++][type.unique];
            
            double angle = random.nextDouble() * Math.PI * 2;
            double vx = Math.sin(angle) * velocity;
            double vy = Math.cos(angle) * velocity;

            state.addAtom(new Atom(type, new Vector2(x, y), new Vector2(vx, vy)));
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
    		state.takeSnapshot();
    		state.readyToUse = true;
            Kinetix.restart = false;
    	}
    	
        if (state.paused) return;

        double timeout = (1.0 / targetUPS) * state.settings.timeFactor;
        // if (timeout > 1.5) timeout = 1.5; // You can't wait for more than 1.5 seconds
        
        deltaTime *= state.settings.timeFactor;
    	if (deltaTime > timeout)
    		deltaTime = timeout;

        state.update(deltaTime);
        CommanderWindow.simluationTime.setText("Time: " + String.format(Locale.US, "%.2f", state.simulationTime) + " s");
        
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
