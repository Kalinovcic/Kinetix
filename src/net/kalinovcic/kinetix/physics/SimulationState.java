package net.kalinovcic.kinetix.physics;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.simulation.LookbackUtil;
import net.kalinovcic.kinetix.simulation.animation.Animation;

public class SimulationState
{
	public SimulationSettings settings = new SimulationSettings();
	
	public AtomType[] atomTypes;
	public Set<Atom> atoms = new HashSet<Atom>();
	public int collisionInfo[][][];
	
	public double simulationTime = 0.0;
    public boolean paused = false;
    public int pauseInSnapshots;
    public Animation animation;
    
    public Atom highlightAtom;
    
    public SimulationSnapshot[] snapshots;
    public double nextSnapshotDelta;
    public int nextSnapshotIndex;
    
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

        atomTypes = new AtomType[Reactions.ATOM_TYPE_COUNT];
        atomTypes[0] = new AtomType(new Color(242,   5,  33), Kinetix.reaction);
        atomTypes[1] = new AtomType(new Color( 21, 150,  23), Kinetix.reaction);
        atomTypes[2] = new AtomType(new Color(  0, 130, 173), null);
        atomTypes[3] = new AtomType(new Color( 51,  51,  51), null);
        for (int i = 4; i < Reactions.ATOM_TYPE_COUNT; i++)
            atomTypes[i] = new AtomType(Color.DARK_GRAY, null);

    	for (int i = 0; i < snapshots.length; i++)
    		snapshots[i].valid = false;
    	
    	highlightAtom = null;
    	
    	nextSnapshotIndex = 0;
    	nextSnapshotDelta = 0;
    	takeSnapshot();
    }
    
    public void takeSnapshot()
    {
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
			atom.position.add(atom.velocity.clone().mul(deltaTime));
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
	
	public Event getNextEvent(double deltaTime)
	{
		Event nextEvent = new Event();
		nextEvent.time = deltaTime;
		
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
		
		return nextEvent;
	}
	
	public void update(double deltaTime, double timeout)
	{
	    long beginTime = System.nanoTime();
	    
		double remaining = deltaTime;
		while (remaining > 0)
		{
			if (paused) return;
			
		    long currentTime = System.nanoTime();
		    long runningTime = currentTime - beginTime;
		    double runningTimeS = runningTime / 1000000000.0;
		    if (runningTimeS > timeout)
		        break;
		    
		    Event nextEvent = getNextEvent(remaining);
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
		}
	}
}
