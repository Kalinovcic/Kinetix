package net.kalinovcic.kinetix.profiler.collisions;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.physics.SimulationState;

public class CollisionsThread extends KinetixThread
{
    public Collisions profiler;
    public CollisionsRenderer renderer;
    
    public CollisionsThread(Collisions profiler)
    {
        super(60);
        this.profiler = profiler;
    }
    
    @Override
    public void initialize()
    {
        renderer = new CollisionsRenderer(profiler);
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
    	synchronized (profiler)
    	{
	    	if (!profiler.isShowing()) return;
    		renderer.render(state, deltaTime);
    	}
    }
}
