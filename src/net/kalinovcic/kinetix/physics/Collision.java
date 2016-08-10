package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector2;

public class Collision implements Comparable<Collision>
{
	public Atom atom1;
	public Atom atom2;
	public double time;
	
	public Collision(Atom atom1, Atom atom2, double time)
	{
		this.atom1 = atom1;
		this.atom2 = atom2;
		this.time = time;
	}

	@Override
	public int compareTo(Collision o)
	{
		if (time < o.time) return -1;
		if (time > o.time) return 1;
		return 0;
	}
	
	public boolean perform()
	{
		Vector2 n = atom2.position.clone().sub(atom1.position);
		Vector2 un = n.normal();
		Vector2 ut = new Vector2(-un.y, un.x);
		
		double v1n = atom1.velocity.dot(un);
		double v1t = atom1.velocity.dot(ut);
		
		double v2n = atom2.velocity.dot(un);
		double v2t = atom2.velocity.dot(ut);

		double v1nc = (v1n*(atom1.mass - atom2.mass) + 2*atom2.mass*v2n) / (atom1.mass + atom2.mass);
		double v1tc = v1t;

		double v2nc = (v2n*(atom2.mass - atom1.mass) + 2*atom1.mass*v1n) / (atom1.mass + atom2.mass);
		double v2tc = v2t;

		double maxvnc = Math.max(v1nc, v2nc);
		double minvnc = Math.min(v1nc, v2nc);
		double dvnc = maxvnc - minvnc;
		
		boolean merged = false;
		if (dvnc > 100.0)
		{
			merged = atom1.attemptMerge(atom2);
		}

		Vector2 v1ncv = un.clone().mul(v1nc);
		Vector2 v1tcv = ut.clone().mul(v1tc);

		Vector2 v2ncv = un.clone().mul(v2nc);
		Vector2 v2tcv = ut.clone().mul(v2tc);

		Vector2 v1c = v1ncv.clone().add(v1tcv);
		Vector2 v2c = v2ncv.clone().add(v2tcv);

		atom1.velocity.set(v1c);
		atom2.velocity.set(v2c);
		
		return merged;
	}
}
