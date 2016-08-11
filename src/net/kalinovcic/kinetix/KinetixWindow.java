package net.kalinovcic.kinetix;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

public class KinetixWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

    public MainWindow mainWindow;
    public int targetWidth;
    public int targetHeight;
    
    public BufferedImage canvas;
    
    public KinetixWindow(MainWindow mainWindow, String title, int x, int y, int targetWidth, int targetHeight, boolean resizable, boolean closable)
    {
        super(title, resizable, closable, resizable, false);
        
        this.mainWindow = mainWindow;
        setTargetSize(targetWidth, targetHeight);
        
        Dimension size = new Dimension(targetWidth, targetHeight);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(x, y);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));

        canvas = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        setContentPane(new CanvasComponent(this));
        setFocusable(true);

        mainWindow.desktop.add(this);
    }

    public void setTargetSize(int targetWidth, int targetHeight)
    {
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
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
            
            g.drawImage(window.canvas, 0, 0, getWidth(), getHeight(), 0, 0, window.canvas.getWidth(), window.canvas.getHeight(), null);
        }
    }
}
