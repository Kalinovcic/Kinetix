package net.kalinovcic.kinetix.profiler.collisions;

import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

public class Collisions extends Profiler
{
    private static final long serialVersionUID = 1L;

    public Collisions(ProfilerWindow window)
    {
        super(window, new CollisionsAction(), "Collisions", 600, 300);
        setVisible(true);

        thread = new CollisionsThread(this);
        thread.start();
    }
}
