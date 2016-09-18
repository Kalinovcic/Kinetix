package net.kalinovcic.kinetix.profiler.atomsovertime;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class AtomsOverTimeThread extends KinetixThread
{
    public AtomsOverTime profiler;
    public AtomsOverTimeRenderer renderer;
    
    public AtomsOverTimeThread(AtomsOverTime profiler)
    {
        super(20);
        this.profiler = profiler;
    }
    
    @Override
    public void initialize()
    {
    	renderer = new AtomsOverTimeRenderer(profiler);
    }
    
    public void updateProfile(SimulationState state, double deltaTime)
    {
        if (profiler.countOverTime == null)
        {
        	profiler.maximumAtomCount = 0;
        	profiler.countOverTime = new int[AtomsOverTime.ITERATION_COUNT][Reactions.ATOM_TYPE_COUNT];
        	profiler.exists = new boolean[Reactions.ATOM_TYPE_COUNT];
        }

        if (profiler.iteration < AtomsOverTime.ITERATION_COUNT)
        {
            for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
                profiler.countOverTime[profiler.iteration][type] = 0;
            
            for (Atom atom : state.atoms)
                profiler.countOverTime[profiler.iteration][atom.type.unique]++;

            for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
            {
                profiler.maximumAtomCount = Math.max(profiler.maximumAtomCount, profiler.countOverTime[profiler.iteration][type]);
                if (profiler.countOverTime[profiler.iteration][type] > 0)
                    profiler.exists[type] = true;
            }
            
            profiler.iteration++;
        }
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        if (!state.readyToUse) return;
    	synchronized (profiler)
    	{
            if (!state.paused) profiler.paused = false;
            if (profiler.paused) return;
            
	    	updateProfile(state, deltaTime);
	    	
	    	if (!profiler.isShowing()) return;
	        renderer.render(state, deltaTime);
	        
	        if (state.paused) profiler.paused = true;
    	}
    }
}
