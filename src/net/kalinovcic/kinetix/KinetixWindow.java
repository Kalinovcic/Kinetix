package net.kalinovcic.kinetix;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class KinetixWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

    public KinetixThread thread;
    public MainWindow mainWindow;
    public int targetWidth;
    public int targetHeight;
    
    private int currentBuffer = 0;
    private BufferedImage[] buffers = new BufferedImage[2];
    
    public KinetixWindow(KinetixThread thread, MainWindow mainWindow, String title, int x, int y, int targetWidth, int targetHeight, boolean resizable, boolean closable)
    {
        super(title, resizable, closable, resizable, false);
        
        this.thread = thread;
        this.mainWindow = mainWindow;
        setTargetSize(targetWidth, targetHeight);
        
        Dimension size = new Dimension(targetWidth, targetHeight);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(x, y);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));

        setContentPane(new CanvasComponent(this));
        setFocusable(true);
        
        InternalFrameListener exitListener = new InternalFrameAdapter()
        {
        	public void internalFrameClosing(InternalFrameEvent e)
        	{
        		onClose();
        	}
        };
        addInternalFrameListener(exitListener);

        mainWindow.desktop.add(this);
    }
    
    public void onClose()
    {
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
        revalidate();
        repaint();
    }
    
    public static class CanvasComponent extends JComponent
    {
        private static final long serialVersionUID = 1L;
        
        public KinetixWindow window;
        
        public CanvasComponent(KinetixWindow window)
        {
            this.window = window;
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            int width = getWidth();
            int height = getHeight();
            if (width != window.targetWidth || height != window.targetHeight)
            {
                int deltaW = window.targetWidth - width;
                int deltaH = window.targetHeight - height;
                int windowWidth = window.getWidth();
                int windowHeight = window.getHeight();
                int newWidth = windowWidth + deltaW;
                int newHeight = windowHeight + deltaH;

                Dimension size = new Dimension(newWidth, newHeight);
                window.setSize(size);
                window.setMinimumSize(size);
                window.setMaximumSize(size);
            }
            
            BufferedImage buffer = window.getBuffer(1);
            g.drawImage(buffer, 0, 0, getWidth(), getHeight(), 0, 0, buffer.getWidth(), buffer.getHeight(), null);
        }
    }
}
