package net.kalinovcic.kinetix.physics;

import java.util.Random;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.math.Vector2;

public class PhysicsThread extends KinetixThread
{
    public static final int MINIMUM_UPS = 60;
    
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

        state.atoms.clear();
        state.simulationTime = 0;
        state.collisionInfo = new int[Atom.ATOM_TYPE_COUNT][Atom.ATOM_TYPE_COUNT][2];
        
        final int MAXIMUM_VELOCITY = 5000;
        
        double[] sumProbabilities = new double[2];
        double[][] probabilities = new double[MAXIMUM_VELOCITY][2];
        
        double temperature = state.settings.temperature;
        for (int velocity = 0; velocity < MAXIMUM_VELOCITY; velocity++)
        {
        	int velocitySq = velocity * velocity;
        	
        	for (int type = 0; type < 2; type++)
        	{
        		double mass = (type == Atom.ATOM_RED) ? state.settings.redMass : state.settings.greenMass;
        		
        		final double BOLTZMANN = 1.38064852;
        		final double DALTON = 1.6605;
        		
        		double a = Math.pow((mass * DALTON * 0.0001) / (2 * Math.PI * BOLTZMANN * state.settings.temperature), 1.5);
        		double b = Math.exp(-mass * DALTON * 0.0001 * velocitySq / (2 * BOLTZMANN * temperature));
        		double probability = 4 * Math.PI * a * velocitySq * b;
        		
        		probabilities[velocity][type] = probability;
        		sumProbabilities[type] += probability;
        	}
        }
        
        for (int i = 0; i < state.settings.redCount + state.settings.greenCount; i++)
        {
        	int type = (i < state.settings.redCount) ? Atom.ATOM_RED : Atom.ATOM_GREEN;
        	double radius = (type == Atom.ATOM_RED) ? state.settings.redRadius : state.settings.greenRadius;
        	double mass = (type == Atom.ATOM_RED) ? state.settings.redMass : state.settings.greenMass;

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
        
		state.paused = true;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
    	if (Kinetix.settings != state.settings)
    	{
    		state.settings = Kinetix.settings;
    		initializeState(state);
    	}
    	
        if (state.paused) return;
        
        final double TIMEOUT = 1.0 / targetUPS;
        state.update(deltaTime, TIMEOUT);
    }
}
