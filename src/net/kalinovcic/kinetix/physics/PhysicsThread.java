package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.Kinetix;

public class PhysicsThread extends Thread
{
    public static final int MINIMUM_UPS = 60;
    
    @Override
    public void run()
    {
        long previousNano = System.nanoTime();
        while (true)
        {
            long frameBeginNano = System.nanoTime();
            
            long currentNano = System.nanoTime();
            long deltaNano = currentNano - previousNano;
            previousNano = currentNano;
            
            synchronized (Kinetix.STATE)
            {
                final double MAXIMUM_DELTA = 0.2;
                double deltaTime = deltaNano / 1000000000.0;
                if (deltaTime > MAXIMUM_DELTA)
                    deltaTime = MAXIMUM_DELTA;
                
                Kinetix.STATE.update(deltaTime);
            }
            
            long frameEndNano = System.nanoTime();
            long deltaFrameNano = frameEndNano - frameBeginNano;
            long targetFrameNano = 1000000000 / MINIMUM_UPS;
            if (deltaFrameNano < targetFrameNano)
            {
                long remainingFrameNano = targetFrameNano - deltaFrameNano;
                try
                {
                    Thread.sleep(remainingFrameNano / 1000000, (int) (remainingFrameNano % 1000000));
                }
                catch (InterruptedException e) {}
            }
        }
    }
}
