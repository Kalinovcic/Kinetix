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
        super(mainWindow, "Profiler", 20, mainWindow.getHeight() - 450, 700, 400, true, new ProfilerUI());

        InternalFrameListener exitListener = new InternalFrameAdapter()
        {
        	public void internalFrameClosing(InternalFrameEvent e)
        	{
        		onClose();
        	}
        };
        addInternalFrameListener(exitListener);
    }
    
    public void onClose()
    {
        imgui.onClose();
    }
}