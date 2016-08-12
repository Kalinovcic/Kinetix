package net.kalinovcic.kinetix.video;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;

public class VideoWindow extends KinetixWindow
{
    private static final long serialVersionUID = 1L;
    
    public VideoWindow(KinetixThread thread, MainWindow mainWindow, VideoSettings settings)
    {
        super(thread, mainWindow, "Video", 400, 50, 600, 600, false, true);
        setVisible(true);

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "TogglePause");
        getActionMap().put("TogglePause", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
            	settings.paused = !settings.paused;
            }
        });
    }
}
