package net.kalinovcic.kinetix;

import net.kalinovcic.kinetix.physics.PhysicsThread;
import net.kalinovcic.kinetix.profile.typecount.ProfileTypeCountThread;
import net.kalinovcic.kinetix.profile.velocity.ProfileVelocityThread;
import net.kalinovcic.kinetix.simulation.SimulationThread;

public class KinetixStartup
{
	public static void main(String[] args)
	{
		MainWindow mainWindow = new MainWindow();
		new PhysicsThread().start();
		new SimulationThread(mainWindow).start();
		new ProfileTypeCountThread(mainWindow).start();
		new ProfileVelocityThread(mainWindow).start();
	}
}
