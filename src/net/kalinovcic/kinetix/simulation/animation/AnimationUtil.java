package net.kalinovcic.kinetix.simulation.animation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.math.Vector2;

public class AnimationUtil
{
	public static void focusTransform(KinetixWindow window, Graphics2D g2D, double focusX, double focusY)
	{
		double width = 15.0;
		double height = width * window.getHeight() / window.getWidth();
		double scale = window.getWidth() / width;
		g2D.scale(scale, scale);

		double x = focusX - width * 0.5;
		double y = focusY - height * 0.5;
		g2D.translate(-x, -y);
		
		g2D.setStroke(new BasicStroke((float) (1.0 / scale)));
		g2D.setFont(g2D.getFont().deriveFont((float) (12.0 / scale)));
	}
	
	public static void fade(Graphics2D g2D)
	{
		Color currentColor = g2D.getColor();
		int red = currentColor.getRed();
		int green = currentColor.getGreen();
		int blue = currentColor.getBlue();
		int alpha = currentColor.getAlpha();
		Color newColor = new Color(red, green, blue, alpha / 3);
		g2D.setColor(newColor);
	}
	
	public static double getElementProgress(double progress, double begin, double end)
	{
		if (progress <= begin) return 0.0;
		if (progress >= end) return 1.0;
		return (progress - begin) / (end - begin);
	}
	
	public static void drawLine(Graphics2D g2D, double ax, double ay, double bx, double by, double progress)
	{
		if (progress <= 0.0) return;
		
		double bxp = ax + (bx - ax) * progress;
		double byp = ay + (by - ay) * progress;
		g2D.draw(new Line2D.Double(ax, ay, bxp, byp));
	}
	
	public static void drawLine(Graphics2D g2D, Vector2 a, Vector2 b, double progress)
	{
		drawLine(g2D, a.x, a.y, b.x, b.y, progress);
	}
}
