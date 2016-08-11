package net.kalinovcic.kinetix.simulation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.physics.Atom;
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
	    Graphics2D g2D = window.canvas.createGraphics();
	    KinetixUI.setHints(g2D);

		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		for (Atom atom : state.atoms)
		{
			g2D.setColor(atom.getColor());
			
			Shape shape = atom.toShape(window.targetWidth, window.targetHeight);
			g2D.fill(shape);
		}
		
		g2D.dispose();

        window.revalidate();
        window.repaint();
	}
}
