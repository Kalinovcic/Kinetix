package net.kalinovcic.kinetix.video;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;

import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.physics.SimulationState;

@SuppressWarnings("deprecation")
public class VideoRenderer
{
    public KinetixWindow window;
    public VideoSettings settings;
    
    public VideoRenderer(KinetixWindow window, VideoSettings settings)
    {
        this.window = window;
        this.settings = settings;
    }
    
    public BufferedImage crop(BufferedImage frame)
    {
    	int cropWidth = settings.cropLeft + settings.cropRight;
    	int cropHeight = settings.cropTop + settings.cropBottom;
    	BufferedImage cropped = new BufferedImage(frame.getWidth() - cropWidth, frame.getHeight() - cropHeight, BufferedImage.TYPE_INT_RGB);
    	
    	Graphics g = cropped.getGraphics();
    	g.drawImage(frame, 0, 0, cropped.getWidth(), cropped.getHeight(), settings.cropLeft, settings.cropTop, cropped.getWidth(), cropped.getHeight(), null);
    	g.dispose();
    	
    	return cropped;
    }
    
    public void removeBackground(BufferedImage frame)
    {
    	int[] pixels = ((DataBufferInt) frame.getRaster().getDataBuffer()).getData();

    	for (int y = 0; y < frame.getHeight(); y++)
    		for (int x = 0; x < frame.getWidth(); x++)
    		{
    			int rgb = pixels[y * frame.getWidth() + x];
    			int r = (rgb >> 16) & 0xFF;
    			int g = (rgb >> 8) & 0xFF;
    			int b = (rgb >> 0) & 0xFF;

    			int dr = settings.backgroundColor.getRed() - r;
    			int dg = settings.backgroundColor.getGreen() - g;
    			int db = settings.backgroundColor.getBlue() - b;
    			int distance = dr*dr + dg*dg + db*db;
    			
    			if (distance < 64*64)
    				r = g = b = 0;

    			rgb = (r << 16) | (g << 8) | (b);
    			pixels[y * frame.getWidth() + x] = rgb;
    		}
    }

	public void render(SimulationState state, double deltaTime)
    {
		if (settings.paused) return;

        try
        {
	    	Picture picture = settings.videoFrameGrabber.getNativeFrame();
	    	BufferedImage frame = null;
	    	if (picture != null)
	    	{
		    	frame = AWTUtil.toBufferedImage(picture);
		    	
		    	frame = crop(frame);
		    	removeBackground(frame);

		    	settings.frameIndex++;
	    	}
	    	
	        BufferedImage buffer = window.getBuffer(0);
	        Graphics2D g2D = buffer.createGraphics();
	        KinetixUI.setHints(g2D);

	    	g2D.drawImage(frame, 0, 0, window.targetWidth, (int) (frame.getHeight() / (double) frame.getWidth() * window.targetWidth), null);
	    	
	    	int yOffset = 0;
	    	g2D.drawString("Frame " + settings.frameIndex, 4, yOffset += g2D.getFontMetrics().getHeight());
	    	
	        window.swapBuffers();
	        
	        g2D.dispose();
        }
        catch (IOException ex)
        {
        	ex.printStackTrace();
        }
    }
}
