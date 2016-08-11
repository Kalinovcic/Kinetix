package net.kalinovcic.kinetix.profile.velocity;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.MainWindow;

public class ProfileVelocityWindow extends KinetixWindow
{
    private static final long serialVersionUID = 1L;
    
    public ProfileVelocityWindow(MainWindow mainWindow, ProfileVelocitySettings settings)
    {
        super(mainWindow, "Profile - # atoms / velocity", 640, 350, 600, 300, false, false);
        setVisible(true);
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "DecreaseColumnCount");
        getActionMap().put("DecreaseColumnCount", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                if (settings.columnCount > 1)
                    settings.columnCount--;
                settings.velocityInterval = settings.maximumVelocity / settings.columnCount;
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "IncreaseColumnCount");
        getActionMap().put("IncreaseColumnCount", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                settings.columnCount++;
                settings.velocityInterval = settings.maximumVelocity / settings.columnCount;
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "DecreaseMaximumVelocity");
        getActionMap().put("DecreaseMaximumVelocity", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                if (settings.maximumVelocity > 10)
                    settings.maximumVelocity -= 10;
                settings.velocityInterval = settings.maximumVelocity / settings.columnCount;
            }
        });
        
        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "IncreaseMaximumVelocity");
        getActionMap().put("IncreaseMaximumVelocity", new AbstractAction()
        {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e)
            {
                settings.maximumVelocity += 10;
                settings.velocityInterval = settings.maximumVelocity / settings.columnCount;
            }
        });
    }
}
