package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector2;

public class Collision
{
	public static double getTime(Atom atom1, Atom atom2)
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
	
	public static void collide(SimulationState state, Atom atom1, Atom atom2)
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

		Vector2 v1ncv = un.clone().mul(v1nc);
		Vector2 v1tcv = ut.clone().mul(v1tc);

		Vector2 v2ncv = un.clone().mul(v2nc);
		Vector2 v2tcv = ut.clone().mul(v2tc);

		Vector2 v1c = v1ncv.clone().add(v1tcv);
		Vector2 v2c = v2ncv.clone().add(v2tcv);

		atom1.velocity.set(v1c);
		atom2.velocity.set(v2c);

		if (dvnc > 100.0)
		{
			atom1.attemptMerge(state, atom2);
		}
	}
}
