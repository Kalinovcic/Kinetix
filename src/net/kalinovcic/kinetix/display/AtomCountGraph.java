package net.kalinovcic.kinetix.display;

import java.awt.Graphics2D;
import java.awt.Insets;

import net.kalinovcic.kinetix.physics.Atom;

public class AtomCountGraph
{
	public Renderer renderer;
	
	public static final double UPDATE_INTERVAL = 0.02;
	public static double countdown;

	public static final int ITERATION_COUNT = 300;
	public int iteration = 0;
	
	public int maximumAtomCount;
	public int[][] countOverTime;
	
	public AtomCountGraph(Renderer renderer)
	{
		this.renderer = renderer;

		maximumAtomCount = renderer.state.atoms.size();
		countOverTime = new int[ITERATION_COUNT][Atom.ATOM_TYPE_COUNT];
	}
	
	public void update(double deltaTime)
	{
		if (iteration >= ITERATION_COUNT) return;

		countdown -= deltaTime;
		while (countdown < 0.0)
		{
			countdown += UPDATE_INTERVAL;
		
			maximumAtomCount = Math.max(maximumAtomCount, renderer.state.atoms.size());
			for (int i = 0; i < Atom.ATOM_TYPE_COUNT; i++) countOverTime[iteration][i] = 0;
			for (Atom atom : renderer.state.atoms) countOverTime[iteration][atom.type]++;
			
			iteration++;
			if (iteration >= ITERATION_COUNT) return;
		}
	}
	
	public void render(Graphics2D g2D)
	{
		Insets insets = renderer.window.getInsets();
		int top = insets.top;
		int left = insets.left;
		int right = renderer.window.getWidth() - insets.right;
		int bottom = renderer.window.getHeight() - insets.bottom;
		int width = right - left;
		int height = bottom - top;
		
		for (int i = 1; i < iteration; i++)
		{
			double x1 = width * ((i - 1) / (double) (countOverTime.length - 1));
			double x2 = width * ((i - 0) / (double) (countOverTime.length - 1));
			for (int j = 0; j < Atom.ATOM_TYPE_COUNT; j++)
			{
				double y1 = height * (1.0 - countOverTime[i - 1][j] / (double) maximumAtomCount) * 0.8 + height * 0.2;
				double y2 = height * (1.0 - countOverTime[i - 0][j] / (double) maximumAtomCount) * 0.8 + height * 0.2;

				g2D.setColor(Atom.getColor(j));
				g2D.drawLine((int) x1 + left, (int) y1 + top, (int) x2 + left, (int) y2 + top);
			}
		}
	}
}
