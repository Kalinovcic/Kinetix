package net.kalinovcic.kinetix;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.kalinovcic.kinetix.commander.CommanderWindow;
import net.kalinovcic.kinetix.physics.PhysicsThread;
import net.kalinovcic.kinetix.profile.typecount.ProfileTypeCountThread;
import net.kalinovcic.kinetix.profile.velocity.ProfileVelocityThread;
import net.kalinovcic.kinetix.simulation.SimulationThread;

public class KinetixStartup
{
	public static void main(String[] args)
	{
	    try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}
	    
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MainWindow mainWindow = new MainWindow();
                new CommanderWindow(mainWindow);
                new PhysicsThread().start();
                new SimulationThread(mainWindow).start();
                new ProfileTypeCountThread(mainWindow).start();
                new ProfileVelocityThread(mainWindow).start();
            }
        });
	}
}
