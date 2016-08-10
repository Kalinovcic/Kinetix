package net.kalinovcic.kinetix.display;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferStrategy;

import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.State;

public class AtomRenderer
{
	public AtomWindow window;
	public State state;
	
	public AtomCountGraph atomCountGraph;
	
	public AtomRenderer(AtomWindow window, State state)
	{
		this.window = window;
		this.state = state;
		
		atomCountGraph = new AtomCountGraph(this);
	}
	
	public void render(Graphics g, double deltaTime)
	{
		Insets insets = window.getInsets();
		int top = insets.top;
		int left = insets.left;
		int right = window.getWidth() - insets.right;
		int bottom = window.getHeight() - insets.bottom;
		
		BufferStrategy bs = window.mainWindow.getBufferStrategy();
		if (bs == null)
		{
			window.mainWindow.createBufferStrategy(4);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		Graphics2D g2D = (Graphics2D) g;
		
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2D.setColor(Color.WHITE);
		g2D.fillRect(0, 0, window.getWidth(), window.getHeight());
		
		for (Atom atom : state.atoms)
		{
			g2D.setColor(atom.getColor());
			
			Shape shape = atom.toShape(left, right, top, bottom);
			g2D.fill(shape);
		}
		
		atomCountGraph.update(deltaTime);
		atomCountGraph.render(g2D);
		
		renderVelocities(g2D);
		
		g2D.dispose();
		bs.show();
	}
	
	public void renderVelocities(Graphics2D g2D)
	{
		Insets insets = window.getInsets();
		int left = insets.left;
		int bottom = window.getHeight() - insets.bottom;
		
		int[] amount = new int[200];
		for (int i = 0; i < amount.length; i++)
			amount[i] = 0;
		
		double columnDivision = 32.0;
		
		for (Atom atom : state.atoms)
		{
			double vel = atom.velocity.length();
			int vi = (int) (vel / columnDivision);
			if (vi < amount.length)
				amount[vi]++;
		}

		g2D.setFont(g2D.getFont().deriveFont(10.0f));
		for (int i = 0; i < amount.length; i++)
		{
			if (amount[i] == 0) continue;
			
			int width = 32;
			int height = amount[i] * 1;

			g2D.setColor(Color.ORANGE);
			g2D.fillRect(left + i * width, bottom - height, width, height);
			
			g2D.setColor(Color.BLACK);
			g2D.drawString((int) (i * columnDivision) + "", left + i * width, bottom - height);
		}
	}
}
