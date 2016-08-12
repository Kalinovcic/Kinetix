package net.kalinovcic.kinetix.simulation;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationState;

public class SimulationThread extends KinetixThread
{
    public MainWindow mainWindow;
    public SimulationWindow window;
    public SimulationRenderer renderer;
    
    public SimulationThread(MainWindow mainWindow)
    {
        super(60);
        this.mainWindow = mainWindow;
    }
    
    @Override
    public void initialize()
    {
    	KinetixThread thisThread = this;
    	
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    window = new SimulationWindow(thisThread, mainWindow);
                }
            });
        }
        catch(InvocationTargetException | InterruptedException e) {}
        renderer = new SimulationRenderer(window);
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        renderer.render(state, deltaTime);
    }
}
