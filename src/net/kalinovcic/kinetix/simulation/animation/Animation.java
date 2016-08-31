package net.kalinovcic.kinetix.simulation.animation;

import java.awt.Color;
import java.awt.Graphics2D;

import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.math.Vector2;
import net.kalinovcic.kinetix.physics.CollisionData;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.simulation.LookbackUtil;
import static net.kalinovcic.kinetix.simulation.animation.AnimationUtil.*;

public class Animation
{
	public AnimationState animationState;
	public double animationTime;
	public double beginStateTime;
	public double stateTime;
	public double statePause;
	
	public boolean initialized = false;

	public int collisionSnapshot;
	public CollisionData collision;

	public static final double TIME_SLOWDOWN = 0.01;
	
	public double collisionLookback;
	public double beginLookback;
	public double endLookback;
	
	public Animation(SimulationState state, AnimationState animationState)
	{
		this.animationState = animationState;
	}
	
	public void initialize(SimulationState state)
	{
		final double maxApproachTime = 0.01;
		final double maxLeavingTime = 0.01;
		
		collisionLookback = LookbackUtil.getSnapshotTimeForIndex(state, collisionSnapshot);
		beginLookback = Math.min(collisionLookback + maxApproachTime, LookbackUtil.getAvailableSnapshotTime(state));
		endLookback = Math.max(collisionLookback - maxLeavingTime, 0.0);
		
		initState(state);
	}
	
	public void initState(SimulationState state)
	{
		switch (animationState)
		{
		case COLLISION_APPROACH:
			double approachTime = beginLookback - collisionLookback;
			state.lookback = beginLookback;
			beginStateTime = approachTime / TIME_SLOWDOWN;
			statePause = 0.0;
		break;
		
		case COLLISION_LEAVING:
			double leavingTime = collisionLookback - endLookback;
			beginStateTime = leavingTime / TIME_SLOWDOWN;
			statePause = 0.0;
		break;

		default:
			beginStateTime = 1.0;
			statePause = 1.0;
		}
		
		stateTime = beginStateTime;
	}
	
	public void deinitState(SimulationState state)
	{
		switch (animationState)
		{
		case COLLISION_APPROACH:
			state.lookback = collisionLookback;
		break;

		default:
		}
	}
	
	public void nextState(SimulationState state)
	{
		deinitState(state);
		animationState = animationState.next;
		initState(state);
	}
	
	public void progress(SimulationState state, double deltaTime)
	{
		if (!initialized)
		{
			initialize(state);
			initialized = true;
		}
		
		animationTime += deltaTime;
		
		if (stateTime <= 0.0)
		{
			statePause -= deltaTime;
			if (statePause <= 0.0)
				nextState(state);
			return;
		}
		stateTime -= deltaTime;
		
		switch (animationState)
		{
		case COLLISION_APPROACH:
			state.lookback -= deltaTime * TIME_SLOWDOWN;
			if (state.lookback < collisionLookback)
				state.lookback = collisionLookback;
		break;
		
		case COLLISION_LEAVING:
			state.lookback -= deltaTime * TIME_SLOWDOWN;
			if (state.lookback < endLookback)
				state.lookback = endLookback;
		break;
		
		default:
		}
	}
	
	public void preRender(SimulationState state, KinetixWindow window, Graphics2D g2D)
	{
		double focusX = (collision.p1.x + collision.p2.x) * 0.5;
		double focusY = (collision.p1.y + collision.p2.y) * 0.5;
		focusTransform(window, g2D, focusX, focusY);
	}
	
	public void render(SimulationState state, KinetixWindow window, Graphics2D g2D)
	{
		double progress = 1.0;
		if (stateTime > 0.0) progress = 1.0 - stateTime / beginStateTime;

		g2D.setColor(Color.BLACK);
		
		switch (animationState)
		{
		
		case COLLISION_FINAL_VELOCITIES:
		{
			double maxVelocity = Math.max(collision.v1c.length(), collision.v2c.length());
			double maxLength = Math.max(collision.r1, collision.r2) * 1.5;
			double length1 = collision.v1c.length() / maxVelocity * maxLength;
			double length2 = collision.v2c.length() / maxVelocity * maxLength;
			drawLine(g2D, collision.p1, collision.p1.clone().add(collision.v1c.normal().mul(length1)), progress);
			drawLine(g2D, collision.p2, collision.p2.clone().add(collision.v2c.normal().mul(length2)), progress);
		}
		
		progress = 1.0;
		fade(g2D);
		
		case COLLISION_NT:
		{
			drawLine(g2D, collision.p1, collision.p2, getElementProgress(progress, 0.0, 0.5));
			
			Vector2 collisionPoint = collision.p1.clone().add(collision.un.clone().mul(collision.r1));
			Vector2 tLow = collisionPoint.clone().sub(collision.t.clone().mul(0.5));
			Vector2 tHigh = collisionPoint.clone().add(collision.t.clone().mul(0.5));
			drawLine(g2D, tLow, tHigh, getElementProgress(progress, 0.5, 1.0));
		}
		
		progress = 1.0;
		fade(g2D);
		
		case COLLISION_INITIAL_VELOCITIES:
		{
			double maxVelocity = Math.max(collision.v1.length(), collision.v2.length());
			double maxLength = Math.max(collision.r1, collision.r2) * 1.5;
			double length1 = collision.v1.length() / maxVelocity * maxLength;
			double length2 = collision.v2.length() / maxVelocity * maxLength;
			drawLine(g2D, collision.p1, collision.p1.clone().add(collision.v1.normal().mul(length1)), progress);
			drawLine(g2D, collision.p2, collision.p2.clone().add(collision.v2.normal().mul(length2)), progress);
		}
		
		default:
		}
	}
}
