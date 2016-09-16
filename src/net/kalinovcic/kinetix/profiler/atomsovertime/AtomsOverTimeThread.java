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
        }

        if (profiler.iteration < AtomsOverTime.ITERATION_COUNT)
        {
            for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++) profiler.countOverTime[profiler.iteration][i] = 0;
            for (Atom atom : state.atoms) profiler.countOverTime[profiler.iteration][atom.type]++;
            for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++) profiler.maximumAtomCount = Math.max(profiler.maximumAtomCount, profiler.countOverTime[profiler.iteration][i]);
            profiler.iteration++;
        }
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
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
