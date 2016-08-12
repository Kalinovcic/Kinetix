package net.kalinovcic.kinetix.physics;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import net.kalinovcic.kinetix.math.Vector2;

public class Atom
{
	public static final int ATOM_RED = 0;
	public static final int ATOM_GREEN = 1;
	public static final int ATOM_BLACK = 2;
	public static final int ATOM_TYPE_COUNT = 3;
	
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
	
	public void updateCollisionTime(SimulationState state)
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
	
	public void updateWallTime(SimulationState state)
	{
		wallTime = Double.MAX_VALUE;
		
		if (velocity.x != 0.0)
		{
			double time = (velocity.x < 0.0) ?
				((position.x - radius) / -velocity.x) :
				((state.settings.width - position.x - radius) / velocity.x);

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
				((state.settings.height - position.y - radius) / velocity.y);
			
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
		if (type == ATOM_RED) return new Color(242, 5, 33);
		if (type == ATOM_GREEN) return new Color(21, 150, 23);
		if (type == ATOM_BLACK) return new Color(51, 51, 51);
		return Color.GRAY;
	}
	
	public Color getColor()
	{
		return getColor(type);
	}
	
	public Shape toShape()
	{
		return new Ellipse2D.Double(position.x - radius, position.y - radius, radius * 2, radius * 2);
	}
	
	public boolean attemptMerge(SimulationState state, Atom other)
	{
	    if (other.radius > radius)
	    	return other.attemptMerge(state, this);
	    
		if ((type == ATOM_RED && other.type == ATOM_GREEN) ||
			(type == ATOM_GREEN && other.type == ATOM_RED))
		{
			state.removeAtom(this);
			state.removeAtom(other);
			
			int mergedType = ATOM_BLACK;
			Vector2 mergedPosition = position;
			Vector2 mergedVelocity = velocity.clone().mul(mass / (mass + other.mass)).add(other.velocity.clone().mul(other.mass / (mass + other.mass)));
			double mergedRadius = radius + other.radius;
			double mergedMass = mass + other.mass;
			
			Atom merged = new Atom(mergedType, mergedPosition, mergedVelocity, mergedRadius, mergedMass);
			state.addAtom(merged);
			
			return true;
		}
		
		return false;
	}
}
