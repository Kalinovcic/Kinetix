package net.kalinovcic.kinetix.profiler.atomsovertime;

import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

public class AtomsOverTime extends Profiler
{
    private static final long serialVersionUID = 1L;

    public static final int ITERATION_COUNT = 1500;
    public int iteration = 0;
    
    public boolean paused = false;
    public int maximumAtomCount = 0;
    public int[][] countOverTime = null;
    public boolean[] exists = null;
    
    public AtomsOverTime(ProfilerWindow window)
    {
        super(window, new AtomsOverTimeAction(), "Atoms over time", 1500, 300);
        
        thread = new AtomsOverTimeThread(this);
        thread.start();
    }
}
