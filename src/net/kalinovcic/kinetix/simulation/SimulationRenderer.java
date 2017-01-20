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

		if (state.paused && state.animation != null)
		{
			state.animation.progress(state, deltaTime);
			
			g2D.setColor(Color.BLACK);
			g2D.drawString("Lookback: " + String.format(Locale.US, "%.3fs", state.lookback), 0, 20);
			
			state.animation.preRender(state, window, g2D);
		}

		/*
		double availableTime = state.nextSnapshotDelta;
		int currentIndex = state.nextSnapshotIndex;
		while (true)
		{
			currentIndex = LookbackUtil.previous(state, currentIndex);
	    	if (!state.snapshots[currentIndex].valid) break;
	    	
	    	AtomSnapshot highlight = state.snapshots[currentIndex].highlightAtom;
	    	if (highlight != null)
	    	{
		    	double beginX = highlight.x;
		    	double beginY = highlight.y;
		    	double endX = beginX + highlight.vx * availableTime;
		    	double endY = beginY + highlight.vy * availableTime;
		    	g2D.setColor(highlight.getColor());
		    	g2D.draw(new Line2D.Double(beginX, beginY, endX, endY));
	    	}
	    	
	    	availableTime = state.snapshots[currentIndex].deltaTime;
		}
		*/
		
		int snapshotIndex = LookbackUtil.getSnapshotIndexForLookback(state, state.lookback);
		double snapshotTime = LookbackUtil.getSnapshotTimeForLookback(state, state.lookback);
		
    	SimulationSnapshot snapshot = state.snapshots[snapshotIndex];
    	if (snapshot.valid)
    	{
    		for (int i = 0; i < snapshot.atomCount; i++)
    		{
    			AtomSnapshot atom = snapshot.atoms[i];

                float color_multiply = (float)(1.0 - (atom.z / state.settings.depth) * 0.8);
				Color color = atom.type.color;
				color = new Color(color.getRed() / 255.0f * color_multiply,
				                  color.getGreen() / 255.0f * color_multiply,
				                  color.getBlue() / 255.0f * color_multiply);
				g2D.setColor(color);
				
				Shape shape = atom.toShape(snapshotTime, state.settings.depth);
				g2D.fill(shape);
			}
    	}
    	
    	if (state.paused && state.animation != null)
    		state.animation.render(state, window, g2D);
		
		g2D.dispose();

        window.swapBuffers();
	}
}
