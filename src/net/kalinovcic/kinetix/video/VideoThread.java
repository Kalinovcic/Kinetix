package net.kalinovcic.kinetix.video;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;

import net.kalinovcic.kinetix.KinetixThread;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationState;

@SuppressWarnings("deprecation")
public class VideoThread extends KinetixThread
{
    public MainWindow mainWindow;
    public VideoSettings settings;
    public VideoWindow window;
    public VideoRenderer renderer;
    
    public VideoThread(MainWindow mainWindow)
    {
        super(10);
        this.mainWindow = mainWindow;
    }
    
	@Override
    public void initialize()
    {
    	JFileChooser chooser = new JFileChooser();
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setMultiSelectionEnabled(false);
    	chooser.setFileFilter(new FileFilter()
    	{
			public String getDescription() { return null; }
			public boolean accept(File f) { return f.isDirectory() || f.getName().toLowerCase().endsWith(".mp4"); }
		});
    	chooser.setDialogTitle("Open video");
    	
    	int result = chooser.showOpenDialog(mainWindow);
    	if (result != JFileChooser.APPROVE_OPTION)
    	{
    		terminate = true;
    		return;
    	}
    	
        settings = new VideoSettings();
    	settings.frameIndex = 0;
    	
    	try
    	{
	    	settings.videoFile = chooser.getSelectedFile();
	    	settings.videoChannel = NIOUtils.readableChannel(settings.videoFile);
	    	settings.videoFrameGrabber = FrameGrab.createFrameGrab(settings.videoChannel);
	    	
	    	settings.videoFrameGrabber.seekToFramePrecise(0);
    	}
    	catch (IOException | JCodecException ex)
    	{
    		JOptionPane.showMessageDialog(mainWindow, ex.getMessage(), "Error loading video", JOptionPane.ERROR_MESSAGE);
    		terminate = true;
    		return;
    	}
    	
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    window = new VideoWindow(mainWindow, settings);
                }
            });
        }
        catch(InvocationTargetException | InterruptedException e) {}
        renderer = new VideoRenderer(window, settings);
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        renderer.render(state, deltaTime);
    }
    
    @Override
    public void cleanup()
    {
    	if (settings.videoChannel != null)
    		NIOUtils.closeQuietly(settings.videoChannel);
    }
}
