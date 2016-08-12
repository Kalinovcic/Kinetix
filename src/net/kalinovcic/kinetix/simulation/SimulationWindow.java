package net.kalinovcic.kinetix.simulation;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;

public class SimulationWindow extends KinetixWindow
{
	private static final long serialVersionUID = 1L;
	
	public SimulationWindow(KinetixThread thread, MainWindow mainWindow)
	{
		super(thread, mainWindow, "Simulation", 420, 10, Kinetix.STATE.settings.width, Kinetix.STATE.settings.height, false, false);
        setVisible(true);

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "TogglePause");
        getActionMap().put("TogglePause", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                Kinetix.STATE.paused = !Kinetix.STATE.paused;
            }
        });
	}
}
