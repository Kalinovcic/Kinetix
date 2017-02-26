package net.kalinovcic.kinetix.profiler;

public abstract class Profiler
{
    public String name;
    
    public Profiler(String name)
    {
        this.name = name;
    }

    public abstract void update(ProfilerUI ui);
    public abstract void onClose(ProfilerUI ui);
}
