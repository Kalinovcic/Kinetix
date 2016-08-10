package net.kalinovcic.kinetix.physics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class State
{
	public static double SIMULATION_WIDTH = 600.0;
	public static double SIMULATION_HEIGHT = 600.0;
	
	public ArrayList<Atom> atoms = new ArrayList<Atom>();
	public SortedSet<Collision> collisions = new TreeSet<Collision>();
	
	public void addAtom(Atom atom)
	{
		atoms.add(atom);
		predictCollisions(atom);
		atom.updateWallTime();
	}
	
	public double getCollisionTime(Atom atom1, Atom atom2)
	{
		double x1 = atom1.position.x;
		double y1 = atom1.position.y;
		double x2 = atom2.position.x;
		double y2 = atom2.position.y;
		double u1 = atom1.velocity.x;
		double v1 = atom1.velocity.y;
		double u2 = atom2.velocity.x;
		double v2 = atom2.velocity.y;
		double r1 = atom1.radius;
		double r2 = atom2.radius;
		
		double a = (u1 - u2)*(u1 - u2) + (v1 - v2)*(v1 - v2);
		double b = (u1 - u2)*(2*x1 - 2*x2) + (v1 - v2)*(2*y1 - 2*y2);
		double c = (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) - (r1 + r2)*(r1 + r2);

		double d = b*b - 4*a*c;
		if (d < 0.0) return -1.0;
		
		if (a == 0.0) return -1.0;
		double t1 = (-b + Math.sqrt(d)) / (2*a);
		double t2 = (-b - Math.sqrt(d)) / (2*a);

		if (t1 < t2) return t1;
		return t2;
	}
	
	public void predictCollisions(Atom atom)
	{
		Iterator<Collision> iterator = collisions.iterator();
		while (iterator.hasNext())
		{
			Collision collision = iterator.next();
			if (collision.atom1 == atom || collision.atom2 == atom)
				iterator.remove();
		}
		
		double firstTime = -1.0;
		Atom firstOther = null;
		
		for (Atom other : atoms)
		{
			if (other == atom) continue;
			
			double time = getCollisionTime(atom, other);
			if (time > 0.0 && (firstTime < 0 || time < firstTime))
			{
				firstTime = time;
				firstOther = other;
			}
		}
		
		if (firstOther != null)
		{
			Collision collision = new Collision(atom, firstOther, firstTime);
			collisions.add(collision);
		}
	}
	
	public void moveAtoms(double deltaTime)
	{
		for (Atom atom : atoms)
		{
			atom.position.add(atom.velocity.clone().mul(deltaTime));
			atom.wallTime -= deltaTime;
		}
	}
	
	public void decreaseCollisionTimes(double deltaTime)
	{
		for (Collision collision : collisions)
			collision.time -= deltaTime;
	}
	
	private static final int EVENT_NONE = 0;
	private static final int EVENT_COLLISION = 1;
	private static final int EVENT_WALL = 2;
	
	public void update(double deltaTime)
	{
		double remaining = deltaTime;
		while (remaining > 0)
		{
			int nextEvent = EVENT_NONE;
			
			Collision eventCollision = null;
			Atom eventWall = null;
			
			deltaTime = remaining;
			if (collisions.size() > 0)
			{
				eventCollision = collisions.first();
				if (eventCollision.time < deltaTime)
				{
					nextEvent = EVENT_COLLISION;
					collisions.remove(eventCollision);
					
					deltaTime = eventCollision.time;
				}
			}
			for (Atom atom : atoms)
			{
				if (atom.wallTime < deltaTime)
				{
					eventWall = atom;
					nextEvent = EVENT_WALL;
					deltaTime = atom.wallTime;
				}
			}
			remaining -= deltaTime;
			
			moveAtoms(deltaTime);
			decreaseCollisionTimes(deltaTime);
			
			if (nextEvent == EVENT_COLLISION)
			{
				if (eventCollision.perform())
				{
					atoms.remove(eventCollision.atom2);
				}
				else
				{
					eventCollision.atom2.updateWallTime();
					predictCollisions(eventCollision.atom2);
				}
				
				eventCollision.atom1.updateWallTime();
				predictCollisions(eventCollision.atom1);
			}
			else if (nextEvent == EVENT_WALL)
			{
				eventWall.wallBounce();
				eventWall.updateWallTime();
				predictCollisions(eventWall);
			}
		}
	}
}
