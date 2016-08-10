package net.kalinovcic.kinetix.physics;

import java.util.HashSet;
import java.util.Set;

public class State
{
	public static double SIMULATION_WIDTH = 600.0;
	public static double SIMULATION_HEIGHT = 600.0;
	
	public Set<Atom> atoms = new HashSet<Atom>();
	
	public void addAtom(Atom atom)
	{
		atoms.add(atom);
		atom.updateCollisionTime(this);
		atom.updateWallTime();
	}
	
	public void removeAtom(Atom atom)
	{
		atom.toRemove = true;
		updateAtomsThatCollideWith(atom);
		atoms.remove(atom);
	}
	
	public void atomPostUpdate(Atom atom)
	{
		if (atom.toRemove) return;
		
		updateAtomsThatCollideWith(atom);
		atom.updateCollisionTime(this);
		atom.updateWallTime();
	}
	
	public void updateAtomsThatCollideWith(Atom atom)
	{
		for (Atom other : atoms)
		{
			if (atom == other || other.toRemove) continue;
			if (other.collisionPartner == atom)
				other.updateCollisionTime(this);
		}
	}
	
	public void moveAtoms(double deltaTime)
	{
		for (Atom atom : atoms)
		{
			atom.position.add(atom.velocity.clone().mul(deltaTime));
			atom.wallTime -= deltaTime;
			if (atom.collisionTime >= 0.0)
				atom.collisionTime -= deltaTime;
		}
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
			
			Atom eventCollision = null;
			Atom eventWall = null;
			
			deltaTime = remaining;
			for (Atom atom : atoms)
				if (atom.collisionTime >= 0.0 && atom.collisionTime < deltaTime)
				{
					eventCollision = atom;
					nextEvent = EVENT_COLLISION;
					deltaTime = atom.collisionTime;
				}
			for (Atom atom : atoms)
				if (atom.wallTime < deltaTime)
				{
					eventWall = atom;
					nextEvent = EVENT_WALL;
					deltaTime = atom.wallTime;
				}
			remaining -= deltaTime;
			
			moveAtoms(deltaTime);
			
			if (nextEvent == EVENT_COLLISION)
			{
				Atom partner = eventCollision.collisionPartner;				
				Collision.collide(this, eventCollision, partner);

				atomPostUpdate(eventCollision);
				atomPostUpdate(partner);
			}
			else if (nextEvent == EVENT_WALL)
			{
				eventWall.wallBounce();
				atomPostUpdate(eventWall);
			}
		}
	}
}
