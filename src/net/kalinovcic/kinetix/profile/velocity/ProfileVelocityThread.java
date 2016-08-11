package net.kalinovcic.kinetix.profile.velocity;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationState;

public class ProfileVelocityThread extends KinetixThread
{
    public MainWindow mainWindow;
    public ProfileVelocitySettings settings;
    public ProfileVelocityWindow window;
    public ProfileVelocityRenderer renderer;
    
    public ProfileVelocityThread(MainWindow mainWindow)
    {
        super(60);
        this.mainWindow = mainWindow;
    }
    
    @Override
    public void initialize()
    {
        settings = new ProfileVelocitySettings();
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    window = new ProfileVelocityWindow(mainWindow, settings);
                }
            });
        }
        catch(InvocationTargetException | InterruptedException e) {}
        renderer = new ProfileVelocityRenderer(window, settings);
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        renderer.render(state, deltaTime);
    }
}
