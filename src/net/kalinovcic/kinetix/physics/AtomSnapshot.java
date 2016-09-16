package net.kalinovcic.kinetix.physics;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class AtomSnapshot
{
	public int type;
	public double x;
	public double y;
	public double vx;
	public double vy;
	public double radius;
	public double mass;
	
	public void set(Atom atom)
	{
		type = atom.type;
		x = atom.position.x;
		y = atom.position.y;
		vx = atom.velocity.x;
		vy = atom.velocity.y;
		radius = atom.radius;
		mass = atom.mass;
	}
	
	public Color getColor(SimulationState state)
	{
		return Atom.getType(state, type).color;
	}
	
	public Shape toShape(double deltaTime)
	{
		double xx = x + vx * deltaTime;
		double yy = y + vy * deltaTime;
		return new Ellipse2D.Double(xx - radius, yy - radius, radius * 2, radius * 2);
	}
}
