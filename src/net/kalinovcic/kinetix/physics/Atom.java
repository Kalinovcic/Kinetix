package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector3;
import net.kalinovcic.kinetix.physics.reaction.Reaction;

public class Atom
{
	public AtomType type;
	
	public Vector3 position;
	public Vector3 velocity;
	public double radius;
	public double mass;
	
	public double collisionTime;
	public Atom collisionPartner;
	
	public double wallTime;
	public int wall;
	
	public boolean toRemove = false;
	
	public Atom(AtomType type, Vector3 position, Vector3 velocity)
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
				wall = 0;
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
                wall = 1;
            }
        }
        if (velocity.z != 0.0)
        {
            double time = (velocity.z < 0.0) ?
                ((position.z - radius) / -velocity.z) :
                ((state.settings.depth - position.z - radius) / velocity.z);
            
            if (time < wallTime)
            {
                wallTime = time;
                wall = 2;
            }
        }
	}
	
	public void wallBounce()
	{
	    switch (wall)
	    {
        case 0: velocity.x *= -1.0; break;
        case 1: velocity.y *= -1.0; break;
        case 2: velocity.z *= -1.0; break;
	    }
	}
    
    public static AtomType getType(SimulationState state, int type)
    {
        return state.atomTypes[type];
    }

	public boolean onCollide(SimulationState state, Atom other, CollisionData data)
	{
        if (type.reactantInReaction == null)
            return false;
        
        int typeA = type.reactantInReaction.reactant1_unique;
        int typeB = type.reactantInReaction.reactant2_unique;
        int typeC = type.reactantInReaction.product1_unique;
        int typeD = type.reactantInReaction.product2_unique;
        
        if ((type.unique != typeA || other.type.unique != typeB) &&
            (type.unique != typeB || other.type.unique != typeA))
            return false;

        double energy = type.reactantInReaction.reducedMass * data.dvnc * data.dvnc / 2000 * Reaction.AVOGADRO;
        double activationEnergy = state.settings.activationEnergy;
        if (activationEnergy < 0.0)
            activationEnergy = type.reactantInReaction.activationEnergy;
        if (energy < activationEnergy)
            return false;
        
        if (state.settings.doSteric)
        {
            type.reactantInReaction._stericRemaining -= 1.0;
            if (type.reactantInReaction._stericRemaining <= 0)
            {
                type.reactantInReaction._stericRemaining += 1.0 / type.reactantInReaction.steric;
            }
            else
            {
                return false;
            }
        }

        double massC = state.atomTypes[typeC].mass * Reaction.DALTON;
        double massD = state.atomTypes[typeD].mass * Reaction.DALTON;
        
        double kineticA = 0.5 * (mass * Reaction.DALTON) * velocity.length() * velocity.length();
        double kineticB = 0.5 * (other.mass * Reaction.DALTON) * other.velocity.length() * other.velocity.length();
        double kinetic1 = kineticA + kineticB;
        
        double velocityC, velocityD;
        if (state.settings.doV)
        {
            velocityC = Math.sqrt(2 * kinetic1 / (massC + massD));
            velocityD = Math.sqrt(2 * kinetic1 / (massC + massD));
        }
        else
        {
            double kineticC = 0.5 * kinetic1;
            double kineticD = 0.5 * kinetic1;
            velocityC = Math.sqrt(2 * kineticC / massC);
            velocityD = Math.sqrt(2 * kineticD / massD);
        }

		state.removeAtom(this);
		state.removeAtom(other);

		Atom atomC = new Atom(state.atomTypes[typeC], position, velocity.normal().mul(velocityC));
		Atom atomD = new Atom(state.atomTypes[typeD], other.position, other.velocity.normal().mul(velocityD));
		
		state.addAtom(atomC);
		state.addAtom(atomD);
		
		return true;
	}
}
