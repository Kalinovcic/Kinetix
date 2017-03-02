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
        
        double temperature = state.settings.temperature;
        
        // distribute velocities

        final double BOLTZMANN = 1.38064852;
        final double DALTON = 1.660539040;
        double maximumVelocity = 0;
        int totalAtomCount = 0;

        for (int i = 0; i < state.atomTypes.length; i++)
        {
            if (state.atomTypes[i] == null) continue;
            AtomType type = state.atomTypes[i];
            if (type.initialCount == 0) continue;
                
            double mass = type.mass;
            
            double velocity = 0;
            double probabilitySum = 0;
            while (probabilitySum < 0.999)
            {
                double velocitySq = velocity * velocity;
                
                double a = Math.pow((mass * DALTON * 0.0001) / (2 * Math.PI * BOLTZMANN * temperature), 1.5);
                double b = Math.exp(-mass * DALTON * 0.0001 * velocitySq / (2 * BOLTZMANN * temperature));
                double probability = 4 * Math.PI * a * velocitySq * b;
                probabilitySum += probability;
                
                velocity += 1;
            }

            maximumVelocity = Math.max(maximumVelocity, velocity);
            totalAtomCount += type.initialCount;
        }
        int velocityColumnCount = totalAtomCount / 10;
        double velocityColumnWidth = maximumVelocity / velocityColumnCount;
        
        double[][] velocityFunction = new double[Reactions.ATOM_TYPE_COUNT][];
        double[][] velocities = new double[Reactions.ATOM_TYPE_COUNT][];
        int[] amountPlaced = new int[Reactions.ATOM_TYPE_COUNT];
        for (int i = 0; i < state.atomTypes.length; i++)
        {
            if (state.atomTypes[i] == null) continue;
            AtomType type = state.atomTypes[i];
            double mass = type.mass;
            
            double sumProbabilities = 0;
            velocityFunction[i] = new double[velocityColumnCount];
        	for (int j = 0; j < velocityFunction[i].length; j++)
        	{
                double velocity = (j + 0.5) * velocityColumnWidth;
                double velocitySq = velocity * velocity;
                
                double a = Math.pow((mass * DALTON * 0.0001) / (2 * Math.PI * BOLTZMANN * temperature), 1.5);
                double b = Math.exp(-mass * DALTON * 0.0001 * velocitySq / (2 * BOLTZMANN * temperature));
                double probability = 4 * Math.PI * a * velocitySq * b;
                
                velocityFunction[i][j] = probability;
                sumProbabilities += probability;
        	}
        	
            velocities[i] = new double[type.initialCount];
            
            int k = 0;
        	for (int j = 0; j < velocityFunction[i].length; j++)
        	{
                double velocity = (j + 0.5) * velocityColumnWidth;
        	    int count = (int) Math.round(velocityFunction[i][j] * type.initialCount / sumProbabilities);
        	    while (count-- != 0)
        	        if (k < velocities[i].length)
        	            velocities[i][k++] = velocity;
        	}
        	
        	while (k < velocities[i].length)
        	{
                int maximumJ = 0;
                for (int j = 0; j < velocityFunction[i].length; j++)
                    if (velocityFunction[i][j] > velocityFunction[i][maximumJ])
                        maximumJ = j;
                velocityFunction[i][maximumJ] -= 1;
                double velocity = (maximumJ + 0.5) / velocityColumnWidth;
                velocities[i][k++] = velocity;
        	}
        	
            for (int j = 0; j < velocities[i].length; j++)
                velocities[i][j] += (random.nextDouble() - 0.5) * velocityColumnWidth;
        	
            for (int jj = 0; jj < velocities[i].length * 256; jj++)
            {
                int index1 = random.nextInt(velocities[i].length);
                int index2 = random.nextInt(velocities[i].length);
                
                double temp = velocities[i][index1];
                velocities[i][index1] = velocities[i][index2];
                velocities[i][index2] = temp;
            }
        }
        
        // distribute positions

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
        	
        	double velocity = velocities[unique][amountPlaced[unique]++];
            
            Vector3 vel = new Vector3(random.nextDouble() - 0.5, random.nextDouble() - 0.5,
                    state.settings.do2D ? 0 : (random.nextDouble() - 0.5)).normalize().mul(velocity);
            state.addAtom(new Atom(type, new Vector3(x, y, z), vel));
        }

        state.paused = true;
        state.informListeners();
    }
}
