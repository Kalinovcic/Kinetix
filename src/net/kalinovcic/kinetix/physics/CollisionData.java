package net.kalinovcic.kinetix.physics;

import net.kalinovcic.kinetix.math.Vector3;

public class CollisionData
{
	public Vector3 p1, p2;
	public Vector3 v1, v2;
	public double m1, m2;
	public double r1, r2;
	
	public Vector3 n, t1, t2;
	public Vector3 un, ut1, ut2;
	public double v1n, v1t1, v1t2;
	public double v2n, v2t1, v2t2;
	public double v1nc, v1t1c, v1t2c;
	public double v2nc, v2t1c, v2t2c;
	public double maxvnc, minvnc, dvnc;
	public Vector3 v1ncv, v1t1cv, v1t2cv;
	public Vector3 v2ncv, v2t1cv, v2t2cv;
	public Vector3 v1c, v2c;
	
	public CollisionData(Vector3 p1, Vector3 p2,
						 Vector3 v1, Vector3 v2,
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
        un = n.normal();
        if (n.x != 0.0 && n.y != 0.0 && n.z != 0.0)
            t1 = new Vector3(1, 1, -(n.x + n.y) / n.z);
        else if (n.x == 0.0 && n.y != 0.0 && n.z != 0.0)
            t1 = new Vector3(0, -n.z, n.y);
        else if (n.x != 0.0 && n.y == 0.0 && n.z != 0.0)
            t1 = new Vector3(-n.z, 0, n.x);
        else if (n.x != 0.0 && n.y != 0.0 && n.z == 0.0)
            t1 = new Vector3(-n.y, n.x, 0);
        else if (n.x != 0.0 && n.y == 0.0 && n.z == 0.0)
            t1 = new Vector3(0, n.x, 0);
        else if (n.x == 0.0 && n.y != 0.0 && n.z == 0.0)
            t1 = new Vector3(0, 0, n.y);
        else if (n.x == 0.0 && n.y == 0.0 && n.z != 0.0)
            t1 = new Vector3(n.z, 0, 0);
        ut1 = t1.normal();
        t2 = un.cross(ut1);
        ut2 = t2.normal();
		
		v1n = v1.dot(un);
        v1t1 = v1.dot(ut1);
        v1t2 = v1.dot(ut2);
		
		v2n = v2.dot(un);
        v2t1 = v2.dot(ut1);
        v2t2 = v2.dot(ut2);

		v1nc = (v1n*(m1 - m2) + 2*m2*v2n) / (m1 + m2);
        v1t1c = v1t1;
        v1t2c = v1t2;

		v2nc = (v2n*(m2 - m1) + 2*m1*v1n) / (m1 + m2);
        v2t1c = v2t1;
        v2t2c = v2t2;

		maxvnc = Math.max(v1nc, v2nc);
		minvnc = Math.min(v1nc, v2nc);
		dvnc = maxvnc - minvnc;

		v1ncv = un.clone().mul(v1nc);
        v1t1cv = ut1.clone().mul(v1t1c);
        v1t2cv = ut2.clone().mul(v1t2c);

		v2ncv = un.clone().mul(v2nc);
        v2t1cv = ut1.clone().mul(v2t1c);
        v2t2cv = ut2.clone().mul(v2t2c);

		v1c = v1ncv.clone().add(v1t1cv).add(v1t2cv);
		v2c = v2ncv.clone().add(v2t1cv).add(v2t2cv);
	}
}
