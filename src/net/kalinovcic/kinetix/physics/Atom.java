package net.kalinovcic.kinetix.physics;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import net.kalinovcic.kinetix.math.Vector2;

public class Atom
{
	public static int ATOM_RED = 0;
	public static int ATOM_GREEN = 1;
	public static int ATOM_BLACK = 2;
	public static int ATOM_TYPE_COUNT = 3;
	
	public int type;
	
	public Vector2 position;
	public Vector2 velocity;
	public double radius;
	public double mass;
	
	public double collisionTime;
	public Atom collisionPartner;
	
	public double wallTime;
	public boolean wallHorizontal;
	
	public boolean toRemove = false;
	
	public Atom(int type, Vector2 position, Vector2 velocity, double radius, double mass)
	{
		this.type = type;
		this.position = position;
		this.velocity = velocity;
		this.radius = radius;
		this.mass = mass;
	}
	
	public void updateCollisionTime(State state)
	{
		collisionTime = -1.0;
		
		for (Atom other : state.atoms)
		{
			if (other == this || other.toRemove) continue;
			
			double time = Collision.getTime(this, other);
			if (time >= 0 && (collisionTime < 0 || time < collisionTime))
			{
				collisionTime = time;
				collisionPartner = other;
			}
		}
	}
	
	public void updateWallTime()
	{
		wallTime = Double.MAX_VALUE;
		
		if (velocity.x != 0.0)
		{
			double time = (velocity.x < 0.0) ?
				((position.x - radius) / -velocity.x) :
				((State.SIMULATION_WIDTH - position.x - radius) / velocity.x);

			if (time < wallTime)
			{
				wallTime = time;
				wallHorizontal = false;
			}
		}
		if (velocity.y != 0.0)
		{
			double time = (velocity.y < 0.0) ?
				((position.y - radius) / -velocity.y) :
				((State.SIMULATION_HEIGHT - position.y - radius) / velocity.y);
			
			if (time < wallTime)
			{
				wallTime = time;
				wallHorizontal = true;
			}
		}
	}
	
	public void wallBounce()
	{
		if (wallHorizontal)
			velocity.y *= -1;
		else
			velocity.x *= -1;
	}
	
	public static Color getColor(int type)
	{
		if (type == ATOM_RED) return Color.RED;
		if (type == ATOM_GREEN) return Color.GREEN;
		if (type == ATOM_BLACK) return Color.BLACK;
		return Color.GRAY;
	}
	
	public Color getColor()
	{
		return getColor(type);
	}
	
	public Shape toShape(int left, int right, int top, int bottom)
	{
		int width = right - left;
		int height = bottom - top;
		double mw = width / State.SIMULATION_WIDTH;
		double mh = height / State.SIMULATION_HEIGHT;
		double sx = position.x * mw + left;
		double sy = position.y * mh + top;
		double sw = radius * mw;
		double sh = radius * mh;
		return new Ellipse2D.Double(sx - sw, sy - sh, sw * 2, sh * 2);
	}
	
	public void attemptMerge(State state, Atom other)
	{
		if ((type == ATOM_RED && other.type == ATOM_GREEN) ||
			(type == ATOM_GREEN && other.type == ATOM_RED))
		{
			state.removeAtom(this);
			state.removeAtom(other);
			
			int mergedType = ATOM_BLACK;
			Vector2 mergedPosition = position;
			Vector2 mergedVelocity = velocity.clone().mul(mass / (mass + other.mass)).add(other.velocity.clone().mul(other.mass / (mass + other.mass)));
			double mergedRadius = radius;
			double mergedMass = mass + other.mass;
			
			Atom merged = new Atom(mergedType, mergedPosition, mergedVelocity, mergedRadius, mergedMass);
			state.addAtom(merged);
		}
	}
}
