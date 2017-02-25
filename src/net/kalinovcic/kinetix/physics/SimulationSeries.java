package net.kalinovcic.kinetix.physics;

public class SimulationSeries
{
    public Object id;
    public SimulationSeries next;
    
    public double temperature;

    public int repeatRemaining;
    public double endTime = 0;
    public int halfLife = 0;
    public int halfLifeUnique = 0;
}
