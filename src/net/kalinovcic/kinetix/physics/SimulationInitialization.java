package net.kalinovcic.kinetix.physics;

import java.util.Random;

import net.kalinovcic.kinetix.math.Vector3;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class SimulationInitialization
{
	public static void initialize(SimulationState state)
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

        int totalCount = 0;
        for (AtomType type : state.atomTypes)
            if (type != null)
                totalCount += type.initialCount;
        
        double cubeSize;
        if (state.settings.do2D)
        {
        	int area = state.settings.width * state.settings.height;
            cubeSize = Math.sqrt((double) area / (double) totalCount);
        }
        else
        {
        	int volume = state.settings.width * state.settings.height * state.settings.depth;
            cubeSize = Math.cbrt((double) volume / (double) totalCount);
        }
        
        int countX, countY, countZ, cubeCount, error;
        while (true)
        {
            countX = (int)(state.settings.width  / cubeSize);
            countY = (int)(state.settings.height / cubeSize);
            if (state.settings.do2D)
            	countZ = 1;
            else
            	countZ = (int)(state.settings.depth  / cubeSize);
            cubeCount = countX * countY * countZ;
            error = totalCount - cubeCount;
            if (error <= 0) break;
            cubeSize *= 0.999;
        }

        int[] distribution = new int[cubeCount];
        int currentIndex = 0;
        for (AtomType type : state.atomTypes)
            if (type != null)
            	for (int i = 0; i < type.initialCount; i++)
            	{
	            	distribution[currentIndex] = type.unique;
	            	currentIndex++;
            	}
        for (; currentIndex < distribution.length; currentIndex++)
        	distribution[currentIndex] = -1;
        
        for (int i = 0; i < distribution.length * 256; i++)
        {
        	int index1 = random.nextInt(distribution.length);
        	int index2 = random.nextInt(distribution.length);
        	
        	int temp = distribution[index1];
        	distribution[index1] = distribution[index2];
        	distribution[index2] = temp;
        }
        
        for (int i = 0; i < distribution.length; i++)
        {
        	int ix = i % countX;
        	int iy = (i / countX) % countY;
        	int iz = (i / countX / countY) % countZ;
        	
        	int unique = distribution[i];
        	if (unique == -1) continue;
        	
        	AtomType type = state.atomTypes[unique];

        	double x = (ix + 0.5) * cubeSize + (state.settings.width  - countX * cubeSize) / 2;
        	double y = (iy + 0.5) * cubeSize + (state.settings.height - countY * cubeSize) / 2;
        	double z = (iz + 0.5) * cubeSize + (state.settings.depth  - countZ * cubeSize) / 2;
        	if (state.settings.do2D) z = 0;
        	
        	double randomArea = random.nextDouble() * sumProbabilities[type.unique];
            int velocity = 0;
            while (velocity < MAXIMUM_VELOCITY && randomArea > probabilities[velocity][type.unique])
                randomArea -= probabilities[velocity++][type.unique];
            
            Vector3 vel = new Vector3(random.nextDouble() - 0.5, random.nextDouble() - 0.5,
                    state.settings.do2D ? 0 : (random.nextDouble() - 0.5)).normalize().mul(velocity);
            state.addAtom(new Atom(type, new Vector3(x, y, z), vel));
        }
        
        state.paused = true;
    }
}
