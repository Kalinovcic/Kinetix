package net.kalinovcic.kinetix.simulation;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.imgui.ImguiFrame;

import static net.kalinovcic.kinetix.simulation.SimulationUI.*;

public class SimulationWindow extends ImguiFrame
{
	private static final long serialVersionUID = 1L;
	
	public SimulationWindow(MainWindow mainWindow)
	{
        super(mainWindow, "Simulation", 450, 20, TOTAL_NULL_WIDTH, TOTAL_NULL_HEIGHT, false, new SimulationUI());

        /*getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "TogglePause");
        getActionMap().put("TogglePause", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
            	synchronized (Kinetix.STATE)
            	{
                    Kinetix.STATE.paused = !Kinetix.STATE.paused;
                    Kinetix.STATE.animation = null;
                    Kinetix.STATE.lookback = 0;
				}
            }
        });*/
	}
}
