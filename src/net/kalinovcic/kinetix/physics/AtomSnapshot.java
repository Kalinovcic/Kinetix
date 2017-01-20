package net.kalinovcic.kinetix.physics;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class AtomSnapshot
{
	public AtomType type;
	public double x;
    public double y;
    public double z;
	public double vx;
    public double vy;
    public double vz;
	
	public void set(Atom atom)
	{
		type = atom.type;
		x = atom.position.x;
        y = atom.position.y;
        z = atom.position.z;
		vx = atom.velocity.x;
        vy = atom.velocity.y;
        vz = atom.velocity.z;
	}
	
	public Shape toShape(double deltaTime, int depth)
	{
		double xx = x + vx * deltaTime;
        double yy = y + vy * deltaTime;
        double zz = z + vz * deltaTime;
        
        double distance = zz / depth;
        double radius = type.radius * (1.0 - distance * 0.5);
		return new Ellipse2D.Double(xx - radius, yy - radius, radius * 2, radius * 2);
	}
}
