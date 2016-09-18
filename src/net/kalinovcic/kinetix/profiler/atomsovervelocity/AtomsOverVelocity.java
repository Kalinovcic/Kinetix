package net.kalinovcic.kinetix.profiler.atomsovervelocity;

import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

public class AtomsOverVelocity extends Profiler
{
    private static final long serialVersionUID = 1L;

    public static final int DISPLAY_SINGLE = 0;
    public static final int DISPLAY_STACK = 1;
    public static final int DISPLAY_SEPARATED = 2;
    public static final int DISPLAY_COUNT = 3;
    
    public int displayOption = 0;

    public int[][][] columns = null;
    public double[][] averages = null;
    public double[] maximumAverage = new double[Reactions.ATOM_TYPE_COUNT];

    public int frameInterval = 1;
    public int availableFrames;
    public double maximumVelocity = 4000.0;
    public int columnCount = 50;
    public double velocityInterval = maximumVelocity / columnCount;

    public boolean paused = false;
    public int currentFrame = 0;
    
    public AtomsOverVelocity(ProfilerWindow window)
    {
        super(window, new AtomsOverVelocityAction(), "Atoms over velocity", 600, 300);
        setVisible(true);

        thread = new AtomsOverVelocityThread(this);
        thread.start();
    }
}
