package net.kalinovcic.kinetix.profile.typecount;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationState;

public class ProfileTypeCountThread extends KinetixThread
{
    public MainWindow mainWindow;
    public ProfileTypeCountSettings settings;
    public ProfileTypeCountWindow window;
    public ProfileTypeCountRenderer renderer;
    
    public ProfileTypeCountThread(MainWindow mainWindow)
    {
        super(20);
        this.mainWindow = mainWindow;
    }
    
    @Override
    public void initialize()
    {
        settings = new ProfileTypeCountSettings();
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    window = new ProfileTypeCountWindow(mainWindow, settings);
                }
            });
        }
        catch(InvocationTargetException | InterruptedException e) {}
        renderer = new ProfileTypeCountRenderer(window, settings);
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        renderer.render(state, deltaTime);
    }
}
