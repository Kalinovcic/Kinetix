package net.kalinovcic.kinetix.simulation.animation;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import net.kalinovcic.kinetix.KinetixWindow;

public class AnimationUtil
{
	public static void focusTransform(KinetixWindow window, Graphics2D g2D, double focusX, double focusY)
	{
		double width = 30.0;
		double height = width * window.getHeight() / window.getWidth();
		double scale = window.getWidth() / width;
		g2D.scale(scale, scale);

		double x = focusX - width * 0.5;
		double y = focusY - height * 0.5;
		g2D.translate(-x, -y);
		
		g2D.setStroke(new BasicStroke((float) (1.0 / scale)));
	}
	
	public static double getElementProgress(double progress, double begin, double end)
	{
		if (progress <= begin) return 0.0;
		if (progress >= end) return 1.0;
		return (progress - begin) / (end - begin);
	}
	
	public static void drawLine(Graphics2D g2D, double ax, double ay, double bx, double by, double progress)
	{
		double bxp = ax + (bx - ax) * progress;
		double byp = ay + (by - ay) * progress;
		
		g2D.draw(new Line2D.Double(ax, ay, bxp, byp));
	}
}
