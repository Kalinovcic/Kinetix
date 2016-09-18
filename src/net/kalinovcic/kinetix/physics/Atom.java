package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector2;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class Atom
{
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
    
    public static AtomType getType(SimulationState state, int type)
    {
        return state.atomTypes[type];
    }

	public boolean onCollide(SimulationState state, Atom other, CollisionData data)
	{
        AtomType atomType = getType(state, type);
        if (atomType.reactantInReaction == null)
            return false;
        
        int reactant1 = Reactions.uniqueAtoms.get(atomType.reactantInReaction.reactant1);
        int reactant2 = Reactions.uniqueAtoms.get(atomType.reactantInReaction.reactant2);
        if ((type != reactant1 || other.type != reactant2) && (type != reactant2 || other.type != reactant1))
            return false;
        
        double drs = atomType.reactantInReaction.reducedMass * data.dvnc * data.dvnc / 2000 * Reaction.AVOGADRO;
        if (drs < atomType.reactantInReaction.activationEnergy)
            return false;

		state.removeAtom(this);
		state.removeAtom(other);
		
		/*
		int mergedType = ATOM_BLACK;
		Vector2 mergedPosition = position;
		Vector2 mergedVelocity = velocity.clone().mul(mass / (mass + other.mass)).add(other.velocity.clone().mul(other.mass / (mass + other.mass)));
		double mergedRadius = radius + other.radius;
		double mergedMass = mass + other.mass;
		
		Atom merged = new Atom(mergedType, mergedPosition, mergedVelocity, mergedRadius, mergedMass);
		state.addAtom(merged);
		*/

        int product1 = Reactions.uniqueAtoms.get(atomType.reactantInReaction.product1);
        int product2 = Reactions.uniqueAtoms.get(atomType.reactantInReaction.product2);
		Atom new1 = new Atom(product1, position, velocity, radius, mass);
		Atom new2 = new Atom(product2, other.position, other.velocity, other.radius, other.mass);
		state.addAtom(new1);
		state.addAtom(new2);
		
		return true;
	}
}
