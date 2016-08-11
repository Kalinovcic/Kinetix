package net.kalinovcic.kinetix.profile.typecount;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;

public class ProfileTypeCountWindow extends KinetixWindow
{
    private static final long serialVersionUID = 1L;
    
    public ProfileTypeCountWindow(MainWindow mainWindow, ProfileTypeCountSettings settings)
    {
        super(mainWindow, "Profile - # atoms / time", 640, 10, 600, 300, false, false);
        setVisible(true);
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "Reset");
        getActionMap().put("Reset", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                settings.iteration = 0;
            }
        });
    }
}
