package net.kalinovcic.kinetix;

import net.kalinovcic.kinetix.physics.SimulationState;

public abstract class KinetixThread extends Thread
{
    public int targetUPS;
    
    public KinetixThread(int targetUPS)
    {
        this.targetUPS = targetUPS;
    }
    
    @Override
    public void run()
    {
        initialize();
        synchronized (Kinetix.STATE)
        {
            synchronizedInitialize(Kinetix.STATE);
        }

        long previousNano = System.nanoTime();
        while (true)
        {
            long currentNano = System.nanoTime();
            long deltaNano = currentNano - previousNano;
            double deltaTime = deltaNano / 1000000000.0;
            previousNano = currentNano;

            synchronized (Kinetix.STATE)
            {
                synchronizedUpdate(Kinetix.STATE, deltaTime);
            }

            long endNano = System.nanoTime();
            if (targetUPS != 0)
            {
                long frameNano = endNano - currentNano;
                long targetNano = 1000000000 / targetUPS;
                if (frameNano < targetNano)
                {
                    long remainingNano = targetNano - frameNano;
                    try
                    {
                        Thread.sleep(remainingNano / 1000000, (int) (remainingNano % 1000000));
                    }
                    catch (InterruptedException e) {}
                }
            }
        }
    }
    
    public abstract void initialize();
    public void synchronizedInitialize(SimulationState state) {}
    public abstract void synchronizedUpdate(SimulationState state, double deltaTime);
}
