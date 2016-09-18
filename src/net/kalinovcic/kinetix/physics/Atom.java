package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector2;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class Atom
{
	public AtomType type;
	
	public Vector2 position;
	public Vector2 velocity;
	public double radius;
	public double mass;
	
	public double collisionTime;
	public Atom collisionPartner;
	
	public double wallTime;
	public boolean wallHorizontal;
	
	public boolean toRemove = false;
	
	public Atom(AtomType type, Vector2 position, Vector2 velocity)
	{
		this.type = type;
		this.position = position;
		this.velocity = velocity;
		this.radius = type.radius;
		this.mass = type.mass;
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
        if (type.reactantInReaction == null)
            return false;
        
        int reactant1 = Reactions.uniqueAtoms.get(type.reactantInReaction.reactant1);
        int reactant2 = Reactions.uniqueAtoms.get(type.reactantInReaction.reactant2);
        if ((type.unique != reactant1 || other.type.unique != reactant2) &&
            (type.unique != reactant2 || other.type.unique != reactant1))
            return false;
        
        double energy = type.reactantInReaction.reducedMass * data.dvnc * data.dvnc / 2000 * Reaction.AVOGADRO;
        if (energy < type.reactantInReaction.activationEnergy)
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

        int product1 = Reactions.uniqueAtoms.get(type.reactantInReaction.product1);
        int product2 = Reactions.uniqueAtoms.get(type.reactantInReaction.product2);

        double newV1 = Math.sqrt(2.0 * (energy * 1000.0 / Reaction.AVOGADRO / 2.0) / (state.atomTypes[product1].mass * Reaction.DALTON));
        double newV2 = Math.sqrt(2.0 * (energy * 1000.0 / Reaction.AVOGADRO / 2.0) / (state.atomTypes[product2].mass * Reaction.DALTON));
        // System.out.println((2.0 * (energy * 1000.0 * Reaction.AVOGADRO / 2.0)) + " " + (state.atomTypes[product2].mass * Reaction.DALTON));
        // System.out.println(type.reactantInReaction.reducedMass + " " + data.dvnc + " " + (energy * 1000.0) + " " + newV1 + " " + newV2 + " " + state.atomTypes[product1].mass + " " + state.atomTypes[product2].mass);

		Atom new1 = new Atom(state.atomTypes[product1], position, velocity.normal().mul(newV1));
		Atom new2 = new Atom(state.atomTypes[product2], other.position, other.velocity.normal().mul(newV2));
		state.addAtom(new1);
		state.addAtom(new2);
		
		return true;
	}
}
