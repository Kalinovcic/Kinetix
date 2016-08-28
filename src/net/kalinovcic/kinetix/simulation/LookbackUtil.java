package net.kalinovcic.kinetix.simulation;

import net.kalinovcic.kinetix.physics.SimulationState;

public class LookbackUtil
{
	public static double getSnapshotTimeForIndex(SimulationState state, int index)
	{
		double availableTime = state.nextSnapshotDelta;
		int currentIndex = state.nextSnapshotIndex;
		while (true)
		{
			currentIndex--;
	    	if (currentIndex < 0)
	    		currentIndex += state.snapshots.length;
	    	
	    	if (currentIndex == index)
	    		return availableTime;
	    	if (!state.snapshots[currentIndex].valid)
	    		return availableTime;
	    	
	    	availableTime += state.snapshots[currentIndex].deltaTime;
		}
	}
	
	public static double getAvailableSnapshotTime(SimulationState state)
	{
		return getSnapshotTimeForIndex(state, state.nextSnapshotIndex);
	}
	
	public static int getSnapshotIndexForLookback(SimulationState state, double lookback)
	{
		int currentIndex = state.nextSnapshotIndex;
		double currentDelta = state.nextSnapshotDelta;
		
		while (true)
		{
			int previousIndex = currentIndex;
			currentIndex--;
	    	if (currentIndex < 0)
	    		currentIndex += state.snapshots.length;
	    	
	    	if (!state.snapshots[currentIndex].valid)
	    	{
	    		if (!state.snapshots[previousIndex].valid)
	    			return state.nextSnapshotIndex;
	    		return previousIndex;
	    	}
	    	
	    	if (currentDelta >= lookback)
	    		return currentIndex;
	    	
	    	lookback -= currentDelta;
	    	currentDelta = state.snapshots[currentIndex].deltaTime;
		}
	}
	
	public static double getSnapshotTimeForLookback(SimulationState state, double lookback)
	{
		int currentIndex = state.nextSnapshotIndex;
		double currentDelta = state.nextSnapshotDelta;
		
		while (true)
		{
			currentIndex--;
	    	if (currentIndex < 0)
	    		currentIndex += state.snapshots.length;
	    	
	    	if (!state.snapshots[currentIndex].valid)
	    		return 0.0;
	    	
	    	if (currentDelta >= lookback)
	    		return currentDelta - lookback;
	    	
	    	lookback -= currentDelta;
	    	currentDelta = state.snapshots[currentIndex].deltaTime;
		}
	}
}
