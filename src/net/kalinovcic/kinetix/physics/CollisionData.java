package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector2;

public class CollisionData
{
	public Vector2 p1, p2;
	public Vector2 v1, v2;
	public double m1, m2;
	public double r1, r2;
	
	public Vector2 n, t;
	public Vector2 un, ut;
	public double v1n, v1t;
	public double v2n, v2t;
	public double v1nc, v1tc;
	public double v2nc, v2tc;
	public double maxvnc, minvnc, dvnc;
	public Vector2 v1ncv, v1tcv;
	public Vector2 v2ncv, v2tcv;
	public Vector2 v1c, v2c;
	
	public CollisionData(Vector2 p1, Vector2 p2,
						 Vector2 v1, Vector2 v2,
						 double m1, double m2,
						 double r1, double r2)
	{
		this.p1 = p1.clone();
		this.p2 = p2.clone();
		this.v1 = v1.clone();
		this.v2 = v2.clone();
		this.m1 = m1;
		this.m2 = m2;
		this.r1 = r1;
		this.r2 = r2;
		
		n = p2.clone().sub(p1);
		t = new Vector2(-n.y, n.x);
		un = n.normal();
		ut = t.normal();
		
		v1n = v1.dot(un);
		v1t = v1.dot(ut);
		
		v2n = v2.dot(un);
		v2t = v2.dot(ut);

		v1nc = (v1n*(m1 - m2) + 2*m2*v2n) / (m1 + m2);
		v1tc = v1t;

		v2nc = (v2n*(m2 - m1) + 2*m1*v1n) / (m1 + m2);
		v2tc = v2t;

		maxvnc = Math.max(v1nc, v2nc);
		minvnc = Math.min(v1nc, v2nc);
		dvnc = maxvnc - minvnc;

		v1ncv = un.clone().mul(v1nc);
		v1tcv = ut.clone().mul(v1tc);

		v2ncv = un.clone().mul(v2nc);
		v2tcv = ut.clone().mul(v2tc);

		v1c = v1ncv.clone().add(v1tcv);
		v2c = v2ncv.clone().add(v2tcv);
	}
}
