package net.kalinovcic.kinetix;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.kalinovcic.kinetix.physics.PhysicsThread;
import net.kalinovcic.kinetix.simulation.SimulationThread;

public class KinetixStartup
{
	public static void main(String[] args)
	{
        new PhysicsThread().start();
        
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
                new SimulationThread(mainWindow).start();
            }
        });
	}
}
