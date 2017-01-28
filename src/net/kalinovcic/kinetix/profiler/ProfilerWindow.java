package net.kalinovcic.kinetix.profiler;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.imgui.ImguiFrame;

public class ProfilerWindow extends ImguiFrame
{
    private static final long serialVersionUID = 1L;
    
    public ProfilerWindow(MainWindow mainWindow)
    {
        super(mainWindow, "Profiler", 20, mainWindow.getHeight() - 400, 600, 300, true, new ProfilerUI());

        InternalFrameListener exitListener = new InternalFrameAdapter()
        {
        	public void internalFrameClosing(InternalFrameEvent e)
        	{
        		// onClose();
        	}
        };
        addInternalFrameListener(exitListener);
    }
}