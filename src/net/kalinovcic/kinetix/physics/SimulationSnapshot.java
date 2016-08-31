package net.kalinovcic.kinetix.physics;

public class SimulationSnapshot
{
	public int atomCount;
	public AtomSnapshot atoms[] = null;
	
	public AtomSnapshot highlightAtom;
	
	public boolean valid;
	public double deltaTime;
	
	public void set(SimulationState state, double deltaTime)
	{
		atomCount = state.atoms.size();
		if (atoms == null || atoms.length < atomCount)
		{
			atoms = new AtomSnapshot[atomCount];
			for (int i = 0; i < atomCount; i++)
				atoms[i] = new AtomSnapshot();
		}
		
		boolean hasHighlight = false;
		
		int index = 0;
		for (Atom atom : state.atoms)
		{
			atoms[index++].set(atom);
			if (atom == state.highlightAtom)
			{
				if (highlightAtom == null)
					highlightAtom = new AtomSnapshot();
				highlightAtom.set(atom);
				hasHighlight = true;
			}
		}
		
		if (!hasHighlight)
			highlightAtom = null;
		
		valid = true;
		this.deltaTime = deltaTime;
	}
}
