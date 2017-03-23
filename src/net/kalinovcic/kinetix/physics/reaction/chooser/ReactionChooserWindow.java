package net.kalinovcic.kinetix.physics.reaction.chooser;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.imgui.ImguiFrame;

public class ReactionChooserWindow extends ImguiFrame
{
    private static final long serialVersionUID = 1L;
    
    public ReactionChooserWindow(MainWindow mainWindow)
    {
        super(mainWindow, "Reaction Chooser", 100, 100, 930, 495, true, new ReactionChooserUI());
        requestFocus();
        toFront();
        repaint();
    }
}
