package net.kalinovcic.kinetix;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.video.VideoThread;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	public KinetixDesktop desktop;
	
	public MainWindow()
	{
		final MainWindow mainWindow = this;
		
		Dimension size = new Dimension(1400, 800);
		setTitle("Kinetix");
		setSize(size);
		setPreferredSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		setVisible(true);
		
		desktop = new KinetixDesktop();
		setContentPane(desktop);

		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "Restart");
        getRootPane().getActionMap().put("Restart", new AbstractAction()
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

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "OpenVideo");
        getRootPane().getActionMap().put("OpenVideo", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
            	new VideoThread(mainWindow).start();
            }
        });
	}
}
