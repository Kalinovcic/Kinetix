package net.kalinovcic.kinetix.physics;

public class SimulationSnapshot
{
	public int atomCount;
	public AtomSnapshot atoms[] = null;
	
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
		
		int index = 0;
		for (Atom atom : state.atoms)
			atoms[index++].set(atom);
		
		valid = true;
		this.deltaTime = deltaTime;
	}
}
