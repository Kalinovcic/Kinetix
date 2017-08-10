package net.kalinovcic.kinetix.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.simulation.LookbackUtil;
import net.kalinovcic.kinetix.simulation.animation.Animation;

public class SimulationState
{
    public Object instanceID = new Object();
    
    public boolean readyToUse;
	public SimulationSettings settings;
	public Reaction[] reactions;
	public AtomType[] atomTypes;
	
	public Set<Atom> atoms = new HashSet<Atom>();
	public int collisionInfo[][][];
	
	public double simulationTime = 0.0;
    public boolean paused = false;
    public int pauseInSnapshots;
    public Animation animation;
    
    public SimulationSeries series = null;
    
    public Atom highlightAtom;
    
    public boolean doesSnapshots;
    public SimulationSnapshot[] snapshots;
    public double nextSnapshotDelta;
    public int nextSnapshotIndex;
    
    public boolean realtime = true;
    public List<SimulationUpdateListener> updateListeners = new ArrayList<SimulationUpdateListener>();
    
    public double lookback = 0.0;
    
    {
    	snapshots = new SimulationSnapshot[8192];
    	for (int i = 0; i < snapshots.length; i++)
    		snapshots[i] = new SimulationSnapshot();
    }
    
    public void reset()
    {
        atoms.clear();
        simulationTime = 0;
        collisionInfo = new int[Reactions.ATOM_TYPE_COUNT][Reactions.ATOM_TYPE_COUNT][2];
        
        for (Reaction r : reactions)
        {
            r._stericRemaining = 1.0 / r.steric;
        }

    	for (int i = 0; i < snapshots.length; i++)
    		snapshots[i].valid = false;
    	
    	highlightAtom = null;
    	
    	nextSnapshotIndex = 0;
    	nextSnapshotDelta = 0;

        instanceID = new Object();
    }
    
    public void takeSnapshot()
    {
    	if (!doesSnapshots)
    		return;
    	
    	if (pauseInSnapshots > 0)
    	{
    		pauseInSnapshots--;
    		if (pauseInSnapshots == 0)
    			paused = true;
    	}
    	
    	int guardIndex = LookbackUtil.next(this, nextSnapshotIndex); 
    	snapshots[nextSnapshotIndex].set(this, nextSnapshotDelta);
    	snapshots[guardIndex].valid = false;
    	nextSnapshotIndex = guardIndex;
    	nextSnapshotDelta = 0;
    }
	
	public void addAtom(Atom atom)
	{
		if (highlightAtom == null)
			highlightAtom = atom;
		atoms.add(atom);
		atom.updateCollisionTime(this);
		atom.updateWallTime(this);
	}
	
	public void removeAtom(Atom atom)
	{
		if (atom == highlightAtom)
			highlightAtom = null;
		atom.toRemove = true;
		updateAtomsThatCollideWith(atom);
		atoms.remove(atom);
	}
	
	public void atomPostUpdate(Atom atom)
	{
		if (atom.toRemove) return;
		
		updateAtomsThatCollideWith(atom);
		atom.updateCollisionTime(this);
		atom.updateWallTime(this);
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
	
	public void passTime(double deltaTime)
	{
		simulationTime += deltaTime;
		nextSnapshotDelta += deltaTime;
		
		for (Atom atom : atoms)
		{
            atom.position.x += atom.velocity.x * deltaTime;
            atom.position.y += atom.velocity.y * deltaTime;
            atom.position.z += atom.velocity.z * deltaTime;
			atom.wallTime -= deltaTime;
			if (atom.collisionTime >= 0.0)
				atom.collisionTime -= deltaTime;
		}
	}
	
	public static final int EVENT_NONE = 0;
	public static final int EVENT_COLLISION = 1;
	public static final int EVENT_WALL = 2;
	
	public class Event
	{
		public Atom atom = null;
		public double time;
		public int type = EVENT_NONE;
	}
	
    private Event nextEvent = new Event();
	public void getNextEvent(double deltaTime)
	{
	    nextEvent.atom = null;
		nextEvent.time = deltaTime;
		nextEvent.type = EVENT_NONE;
		
		for (Atom atom : atoms)
			if (atom.collisionTime >= 0.0 && atom.collisionTime < nextEvent.time)
			{
				nextEvent.atom = atom;
				nextEvent.type = EVENT_COLLISION;
				nextEvent.time = atom.collisionTime;
			}
		
		for (Atom atom : atoms)
			if (atom.wallTime < nextEvent.time)
			{
				nextEvent.atom = atom;
				nextEvent.type = EVENT_WALL;
				nextEvent.time = atom.wallTime;
			}
	}
	
	public void update(double deltaTime)
	{
	    for (Atom atom : atoms)
	        atom.decayTimer += deltaTime;
	    
	    for (Reaction reaction : reactions)
	    {
	        if (reaction.product2 == null) continue;
	        if (reaction.reactant2 != null) continue;
	        
	        int typeA = reaction.reactant1_unique;
            int typeB = reaction.product1_unique;
            int typeC = reaction.product2_unique;
            
            AtomType reactantType = atomTypes[typeA];
            reactantType.canBeActivated = true;
            
            while (true)
            {
                double k = reaction.A_exp * Math.pow(reaction.temperature / 298, reaction.n) * Math.exp(-reaction.Ea / Reaction.IDEAL_GAS / reaction.temperature);
                k /= 1e10;
                k *= reactantType.currentCount / (double) reactantType.initialCount;

                double time = 1.0 / k;

                boolean success = false;
                for (Atom atom : atoms)
                {
                    if (atom.type != reactantType) continue;
                    if (settings.doActive && !atom.isActive) continue;
                    if (atom.decayTimer < time) continue;
                    
                    success = true;

                    atomTypes[typeB].currentCount++;
                    atomTypes[typeC].currentCount++;
            
                    Atom atomB = new Atom(atomTypes[typeB], atom.position, atom.velocity);
                    Atom atomC = new Atom(atomTypes[typeC], atom.position, atom.velocity.mul(-1));

                    removeAtom(atom);
                    reactantType.currentCount--;
                    
                    addAtom(atomB);
                    addAtom(atomC);
                    break;
                }
                
                if (!success) break;
            }
	    }
	    
		double remaining = deltaTime;
		while (remaining > 0)
		{
			if (paused) return;

		    getNextEvent(remaining);
			deltaTime = nextEvent.time;
			remaining -= deltaTime;
			
			passTime(deltaTime);

			if (nextEvent.type == EVENT_COLLISION)
			{
				Atom partner = nextEvent.atom.collisionPartner;				
				Collision.collide(this, nextEvent.atom, partner);

				atomPostUpdate(nextEvent.atom);
				atomPostUpdate(partner);
				
				takeSnapshot();
			}
			else if (nextEvent.type == EVENT_WALL)
			{
				nextEvent.atom.wallBounce();
				atomPostUpdate(nextEvent.atom);
				
				takeSnapshot();
			}
			
			/*
			double sumEnergies = 0;
			for (Atom atom : atoms)
			{
			    double velocity = atom.velocity.length();
                double energy = 0.5 * atom.mass * velocity * velocity;
			    sumEnergies += energy;
			}
			System.out.println(sumEnergies);
			*/
		}
		
		informListeners();
	}
	
	public void informListeners()
	{
	    for (SimulationUpdateListener listener : updateListeners)
            listener.onSimulationUpdate(this);
	}
}
