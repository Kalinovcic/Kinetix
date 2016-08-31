package net.kalinovcic.kinetix.simulation;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

public class SimulationWindow extends KinetixWindow
{
	private static final long serialVersionUID = 1L;
	
	public SimulationWindow(KinetixThread thread, MainWindow mainWindow)
	{
		super(thread, mainWindow, "Simulation", 10, 10, Kinetix.STATE.settings.width, Kinetix.STATE.settings.height, false, false);
        setVisible(true);

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Restart");
        getActionMap().put("Restart", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
            	synchronized (Kinetix.STATE)
            	{
                    Kinetix.restart = true;
				}
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "OpenProfiler");
        getActionMap().put("OpenProfiler", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
            	synchronized (Kinetix.STATE)
            	{
                    SwingUtilities.invokeLater(new Runnable()
                    {
                        public void run()
                        {
                            new ProfilerWindow(mainWindow);
                        }
                    });
				}
            }
        });

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "TogglePause");
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
        });
	}
}
