package net.kalinovcic.kinetix.simulation;

import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;

public class SimulationWindow extends KinetixWindow
{
	private static final long serialVersionUID = 1L;
	
	public SimulationWindow(MainWindow mainWindow)
	{
		super(mainWindow, "Simulation", 10, 10, 600, 600, false, false);
        setVisible(true);
	}
}
