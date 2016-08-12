package net.kalinovcic.kinetix.profiler;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import net.kalinovcic.kinetix.KinetixThread;

public class Profiler extends JComponent
{
    private static final long serialVersionUID = 1L;

    public final Profiler profiler = this;
    
    public ProfilerWindow window;
    public ProfilerAction action;
    public KinetixThread thread;
    public String tabTitle;
    
    public int targetWidth;
    public int targetHeight;
    
    private int currentBuffer = 0;
    private BufferedImage[] buffers = new BufferedImage[2];
    
    public Profiler(ProfilerWindow window, ProfilerAction action, String tabTitle, int targetWidth, int targetHeight)
    {
        this.window = window;
        this.action = action;
        this.tabTitle = tabTitle;
        
        setTargetSize(targetWidth, targetHeight);
        
        if (action != null)
        {
        	action.profiler = this;
        	action.initialize();
        }
        
        setFocusable(true);
        setEnabled(true);
        setVisible(true);
    }
    
    public void onClose()
    {
    	if (thread != null)
    		thread.terminate = true;
    }

    public void setTargetSize(int targetWidth, int targetHeight)
    {
    	synchronized (buffers)
    	{
    		if (this.targetWidth != targetWidth || this.targetHeight != targetHeight)
    		{
    	        buffers[0] = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
    	        buffers[1] = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
    		}
    		
	        this.targetWidth = targetWidth;
	        this.targetHeight = targetHeight;
    	}
    }
    
    public BufferedImage getBuffer(int offset)
    {
        synchronized (buffers)
        {
            return buffers[(currentBuffer + offset) % buffers.length];
        }
    }
    
    public void swapBuffers()
    {
        synchronized (buffers)
        {
            currentBuffer = (currentBuffer + 1) % buffers.length;
        }
        window.revalidate();
        window.repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();
        if (width != targetWidth || height != targetHeight)
        {
            int deltaW = targetWidth - width;
            int deltaH = targetHeight - height;
            int windowWidth = window.getWidth();
            int windowHeight = window.getHeight();
            int newWidth = windowWidth + deltaW;
            int newHeight = windowHeight + deltaH;

            Dimension size = new Dimension(newWidth, newHeight);
            window.setSize(size);
            window.setMinimumSize(size);
            window.setMaximumSize(size);
        }
        
        BufferedImage buffer = getBuffer(1);
        g.drawImage(buffer, 0, 0, getWidth(), getHeight(), 0, 0, buffer.getWidth(), buffer.getHeight(), null);
    }
}
