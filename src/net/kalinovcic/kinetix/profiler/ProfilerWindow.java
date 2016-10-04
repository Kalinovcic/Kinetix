package net.kalinovcic.kinetix.profiler;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.profiler.atomsovertime.AtomsOverTime;
import net.kalinovcic.kinetix.profiler.atomsovervelocity.AtomsOverVelocity;
import net.kalinovcic.kinetix.profiler.collisions.Collisions;

public class ProfilerWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

    public MainWindow mainWindow;
    public JTabbedPane tabbedPane;
    public ArrayList<Profiler> profilers = new ArrayList<Profiler>();
    public HashMap<Integer, ArrayList<ProfilerAction>> actions = new HashMap<Integer, ArrayList<ProfilerAction>>();
    
    public ProfilerWindow(MainWindow mainWindow)
    {
        super("Profiler", false, true, false, false);
        
        this.mainWindow = mainWindow;
        
        Dimension size = new Dimension(600, 300);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(20, mainWindow.getHeight() - 400);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));

        setFocusable(true);
        
        InternalFrameListener exitListener = new InternalFrameAdapter()
        {
        	public void internalFrameClosing(InternalFrameEvent e)
        	{
        		onClose();
        	}
        };
        addInternalFrameListener(exitListener);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        addProfiler(new AtomsOverTime(this));
        addProfiler(new AtomsOverVelocity(this));
        addProfiler(new Collisions(this));

        mainWindow.desktop.add(this);
        setVisible(true);
    }
    
    public void addProfiler(Profiler profiler)
    {
    	synchronized (profilers)
    	{
    		tabbedPane.addTab(profiler.tabTitle, profiler);
    		profilers.add(profiler);
    	}
    }
    
    public void onClose()
    {
    	synchronized (profilers)
    	{
    		for (Profiler profiler : profilers)
    			profiler.onClose();
    	}
    }
}