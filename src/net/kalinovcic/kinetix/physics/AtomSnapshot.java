package net.kalinovcic.kinetix.physics;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class AtomSnapshot
{
	public AtomType type;
	public double x;
	public double y;
	public double vx;
	public double vy;
	
	public void set(Atom atom)
	{
		type = atom.type;
		x = atom.position.x;
		y = atom.position.y;
		vx = atom.velocity.x;
		vy = atom.velocity.y;
	}
	
	public Shape toShape(double deltaTime)
	{
		double xx = x + vx * deltaTime;
		double yy = y + vy * deltaTime;
		return new Ellipse2D.Double(xx - type.radius, yy - type.radius, type.radius * 2, type.radius * 2);
	}
}
