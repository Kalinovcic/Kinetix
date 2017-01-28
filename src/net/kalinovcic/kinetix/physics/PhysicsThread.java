package net.kalinovcic.kinetix.physics;

import java.util.Locale;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.commander.CommanderWindow;

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
    
    private void restart(SimulationState state)
    {
        SimulationInitialization.initialize(state);
        state.doesSnapshots = true;
        state.takeSnapshot();
        state.readyToUse = true;
        Kinetix.restart = false;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
    	if (Kinetix.restart)
    		restart(state);
    	
        if (state.paused) return;

        double timeout = (1.0 / targetUPS) * state.settings.timeFactor;
        // if (timeout > 1.5) timeout = 1.5; // You can't wait for more than 1.5 seconds
        
        deltaTime *= state.settings.timeFactor;
    	if (deltaTime > timeout)
    		deltaTime = timeout;
    	
    	boolean restartAfterThisUpdate = false;
    	if (state.autoRestartCounter > 0)
    	{
    	    if (state.simulationTime + deltaTime >= state.endTime)
    	    {
    	        deltaTime = state.endTime - state.simulationTime;
    	        restartAfterThisUpdate = true;
    	    }
    	}

        state.update(deltaTime);
        
        CommanderWindow.simulationTime = "Time: " + String.format(Locale.US, "%.2f", state.simulationTime) + " s";
        
        if (restartAfterThisUpdate)
        {
            restart(state);
            
            state.autoRestartCounter--;
            if (state.autoRestartCounter > 0)
                state.paused = false;
        }
    }
}
