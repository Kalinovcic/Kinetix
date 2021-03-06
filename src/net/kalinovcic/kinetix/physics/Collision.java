package net.kalinovcic.kinetix.physics;

public class Collision
{
	public static double getTime(Atom atom1, Atom atom2)
	{
		double x1 = atom1.position.x;
        double y1 = atom1.position.y;
        double z1 = atom1.position.z;
		double x2 = atom2.position.x;
        double y2 = atom2.position.y;
        double z2 = atom2.position.z;
		double u1 = atom1.velocity.x;
        double v1 = atom1.velocity.y;
        double w1 = atom1.velocity.z;
		double u2 = atom2.velocity.x;
        double v2 = atom2.velocity.y;
        double w2 = atom2.velocity.z;
		double r1 = atom1.radius;
		double r2 = atom2.radius;
		
		double a = (u1 - u2)*(u1 - u2) + (v1 - v2)*(v1 - v2) + (w1 - w2)*(w1 - w2);
		double b = (u1 - u2)*(2*x1 - 2*x2) + (v1 - v2)*(2*y1 - 2*y2) + (w1 - w2)*(2*z1 - 2*z2);
		double c = (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2) + (z1 - z2)*(z1 - z2) - (r1 + r2)*(r1 + r2);

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
		CollisionData data = new CollisionData(atom1.position, atom2.position, atom1.velocity, atom2.velocity,
											   atom1.mass, atom2.mass, atom1.radius, atom2.radius);
		
		boolean madeAnimation = false;
		if (state.simulationTime > 0.5 && state.pauseInSnapshots == 0)
		{
			/*
			state.pauseInSnapshots = 8;
			state.animation = new Animation(state, AnimationState.COLLISION_APPROACH);
			state.animation.collisionSnapshot = state.nextSnapshotIndex;
			state.animation.collision = data;
			madeAnimation = true;
			*/
		}

		atom1.velocity.set(data.v1c);
		atom2.velocity.set(data.v2c);

		boolean merged = atom1.onCollide(state, atom2, data);
		state.collisionInfo[atom1.type.unique][atom2.type.unique][merged ? 1 : 0]++;
		state.collisionInfo[atom2.type.unique][atom1.type.unique][merged ? 1 : 0]++;
		
		if (merged && madeAnimation)
		{
			state.pauseInSnapshots = 0;
			state.animation = null;
		}
	}
}
