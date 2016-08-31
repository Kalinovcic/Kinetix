package net.kalinovcic.kinetix.simulation;

import net.kalinovcic.kinetix.physics.SimulationState;

public class LookbackUtil
{
	public static int previous(SimulationState state, int index)
	{
		index--;
    	if (index < 0)
    		index += state.snapshots.length;
    	return index;
	}

	public static int next(SimulationState state, int index)
	{
		index++;
    	if (index >= state.snapshots.length)
    		index -= state.snapshots.length;
    	return index;
	}
	
	public static int getOldestSnapshotIndex(SimulationState state)
	{
		int currentIndex = state.nextSnapshotIndex;
		
		while (true)
		{
			int previousIndex = currentIndex;
			currentIndex = previous(state, currentIndex);
	    	
	    	if (!state.snapshots[currentIndex].valid)
	    	{
	    		if (!state.snapshots[previousIndex].valid)
	    			return state.nextSnapshotIndex;
	    		return previousIndex;
	    	}
		}
	}
	
	public static double getSnapshotTimeForIndex(SimulationState state, int index)
	{
		double availableTime = state.nextSnapshotDelta;
		int currentIndex = state.nextSnapshotIndex;
		while (true)
		{
			currentIndex = previous(state, currentIndex);
	    	
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
			currentIndex = previous(state, currentIndex);
	    	
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
			currentIndex = previous(state, currentIndex);
	    	
	    	if (!state.snapshots[currentIndex].valid)
	    		return 0.0;
	    	
	    	if (currentDelta >= lookback)
	    		return currentDelta - lookback;
	    	
	    	lookback -= currentDelta;
	    	currentDelta = state.snapshots[currentIndex].deltaTime;
		}
	}
}
