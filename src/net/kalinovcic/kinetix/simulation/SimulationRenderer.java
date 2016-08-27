package net.kalinovcic.kinetix.simulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Locale;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.physics.AtomSnapshot;
import net.kalinovcic.kinetix.physics.SimulationSnapshot;
import net.kalinovcic.kinetix.physics.SimulationState;

public class SimulationRenderer
{
    public KinetixWindow window;
    
    public SimulationRenderer(KinetixWindow window)
    {
        this.window = window;
    }
    
	public void render(SimulationState state, double deltaTime)
	{
		window.setTargetSize(state.settings.width, state.settings.height);
		
	    BufferedImage buffer = window.getBuffer(0);
	    Graphics2D g2D = buffer.createGraphics();
	    KinetixUI.setHints(g2D);

		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		g2D.setColor(Color.BLACK);
		g2D.drawString("Lookback: " + String.format(Locale.US, "%.3fs", state.lookback), 0, 20);
		
		double history = state.lookback;
		double availableTime = state.nextSnapshotDelta;
		
		int currentIndex = state.nextSnapshotIndex;
		while (true)
		{
			currentIndex--;
	    	if (currentIndex < 0) currentIndex += state.snapshots.length;
	    	if (!state.snapshots[currentIndex].valid) break;

	    	if (availableTime >= history)
	    	{
	    		availableTime -= history;
	    		break;
	    	}
	    	history -= availableTime;
	    	
	    	availableTime = state.snapshots[currentIndex].deltaTime;
		}
    	
    	SimulationSnapshot currentSnapshot = state.snapshots[currentIndex];
    	if (currentSnapshot.valid)
    	{
    		if (state.focusPoint != null)
    		{
    			double width = 30.0;
    			double height = width * window.getHeight() / window.getWidth();
    			double scale = window.getWidth() / width;
    			g2D.scale(scale, scale);

    			double x = state.focusPoint.x - width * 0.5;
    			double y = state.focusPoint.y - height * 0.5;
    			g2D.translate(-x, -y);
    		}
    		
    		for (int i = 0; i < currentSnapshot.atomCount; i++)
    		{
    			AtomSnapshot atom = currentSnapshot.atoms[i];
    			
				Color color = atom.getColor();
				g2D.setColor(color);
				
				Shape shape = atom.toShape(availableTime);
				g2D.fill(shape);
			}
    	}
		
		g2D.dispose();

        window.swapBuffers();
	}
}
