package net.kalinovcic.kinetix.physics;

import java.util.Random;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.math.Vector2;

public class PhysicsThread extends KinetixThread
{
    public static final int MINIMUM_UPS = 60;
    
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
        Random random = new Random();
        for (int i = 0; i < 200; i++)
        {
            int type = ((i % 2) == 0) ? Atom.ATOM_RED : Atom.ATOM_GREEN;
            double radius = 2 * ((type == Atom.ATOM_GREEN) ? 2 : 1);
            double mass = 20.0;

            double x = random.nextDouble()*(SimulationState.SIMULATION_WIDTH - 2*radius) + radius;
            double y = random.nextDouble()*(SimulationState.SIMULATION_HEIGHT - 2*radius) + radius;

            double velocity = random.nextDouble() * 300.0;
            double angle = random.nextDouble() * Math.PI * 2;
            double vx = Math.sin(angle) * velocity;
            double vy = Math.cos(angle) * velocity;
            
            state.addAtom(new Atom(type, new Vector2(x, y), new Vector2(vx, vy), radius, mass));
        }
        
        state.paused = true;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        if (state.paused) return;
        
        final double TIMEOUT = 1.0 / targetUPS;
        state.update(deltaTime, TIMEOUT);
    }
}
