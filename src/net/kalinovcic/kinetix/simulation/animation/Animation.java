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
			beginStateTime = 2.0;
			statePause = 2.0;
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
        case COLLISION_INITIAL_VELOCITIES: renderInitialVelocities(g2D, progress); break;
		case COLLISION_NT: renderNT(g2D, progress); break;
        case COLLISION_UNUT: renderUNUT(g2D, progress); break;
        case COLLISION_UNUT_DUPSLIDE: renderUNUTDupSlide(g2D, progress); break;
        case COLLISION_EXTEND_UNUT1: renderExtendUNUT(g2D, progress, true); break;
        case COLLISION_EXTEND_UNUT2: renderExtendUNUT(g2D, progress, false); break;
        case COLLISION_FINAL_VELOCITIES: renderFinalVelocities(g2D, progress); break;
		default:
		}
	}
	
	public void renderInitialVelocities(Graphics2D g2D, double progress)
	{
        /*double maxVelocity = Math.max(collision.v1.length(), collision.v2.length());
        double maxLength = Math.max(collision.r1, collision.r2) * 1.5;
        Vector2 resizedV1 = collision.v1.clone().div(maxVelocity / maxLength);
        Vector2 resizedV2 = collision.v2.clone().div(maxVelocity / maxLength);
        
        drawLine(g2D, collision.p1, collision.p1.clone().add(resizedV1), progress);
        drawLine(g2D, collision.p2, collision.p2.clone().add(resizedV2), progress);*/
	}
	
	public void renderNT(Graphics2D g2D, double progress)
	{
        /*drawLine(g2D, collision.p1, collision.p2, getElementProgress(progress, 0.0, 0.5));
        
        Vector2 collisionPoint = collision.p1.clone().add(collision.un.clone().mul(collision.r1));
        Vector2 tLow = collisionPoint.clone().sub(collision.t.clone().mul(0.5));
        Vector2 tHigh = collisionPoint.clone().add(collision.t.clone().mul(0.5));
        drawLine(g2D, tLow, tHigh, getElementProgress(progress, 0.5, 1.0));
        
        fade(g2D);
        renderInitialVelocities(g2D, 1.0);*/
	}
	
	public void renderUNUT(Graphics2D g2D, double progress)
	{
        g2D.setStroke(thickStroke);
        
        /*drawLine(g2D, collision.p1, collision.p1.clone().add(collision.un), getElementProgress(progress, 0.0, 0.5));
        
        Vector2 collisionPoint = collision.p1.clone().add(collision.un.clone().mul(collision.r1));
        Vector2 tLow = collisionPoint.clone().sub(collision.t.clone().mul(0.5));
        Vector2 tHigh = tLow.clone().add(collision.ut);
        drawLine(g2D, tLow, tHigh, getElementProgress(progress, 0.5, 1.0));*/
        
        g2D.setStroke(thinStroke);
        renderNT(g2D, 1.0);
	}
    
    public void renderUNUTDupSlide(Graphics2D g2D, double progress)
    {
        g2D.setStroke(thickStroke);

        /*Vector2 current = collision.p1.clone().add(collision.p2.clone().sub(collision.p1).mul(progress));
        drawLine(g2D, collision.p1, collision.p1.clone().add(collision.un), 1.0);
        drawLine(g2D, current, current.clone().add(collision.un), 1.0);
        
        Vector2 collisionPoint = collision.p1.clone().add(collision.un.clone().mul(collision.r1));
        Vector2 begin = collisionPoint.clone().sub(collision.t.clone().mul(0.5));
        Vector2 offset1 = collision.p1.clone().sub(begin);
        Vector2 offset2 = collision.p2.clone().sub(begin);

        Vector2 current1 = begin.clone().add(offset1.clone().mul(progress));
        Vector2 current2 = begin.clone().add(offset2.clone().mul(progress));

        drawLine(g2D, current1, current1.clone().add(collision.ut), 1.0);
        drawLine(g2D, current2, current2.clone().add(collision.ut), 1.0);*/

        g2D.setStroke(thinStroke);
        fade(g2D);
        renderInitialVelocities(g2D, 1.0);
    }
    
    public void renderExtendUNUT(Graphics2D g2D, double progress, boolean first)
    {
        g2D.setStroke(thickStroke);
        /*if (first)
        {
            drawLine(g2D, collision.p1, collision.p1.clone().add(collision.un), 1.0);        
            drawLine(g2D, collision.p1, collision.p1.clone().add(collision.ut), 1.0);
        }
        else
        {
            drawLine(g2D, collision.p2, collision.p2.clone().add(collision.un), 1.0);
            drawLine(g2D, collision.p2, collision.p2.clone().add(collision.ut), 1.0);
        }
        g2D.setStroke(thinStroke);

        double maxVelocity = Math.max(collision.v1.length(), collision.v2.length());
        double maxLength = Math.max(collision.r1, collision.r2) * 1.5;
        Vector2 resizedV1 = collision.v1.clone().div(maxVelocity / maxLength);
        Vector2 resizedV2 = collision.v2.clone().div(maxVelocity / maxLength); 
        
        if (first)
        {
            drawLine(g2D, collision.p1, collision.p1.clone().add(collision.un.clone().mul(1.2).mul(resizedV1.dot(collision.un))), progress);
            drawLine(g2D, collision.p1, collision.p1.clone().add(collision.ut.clone().mul(1.2).mul(resizedV1.dot(collision.ut))), progress);
        }
        else
        {
            drawLine(g2D, collision.p2, collision.p2.clone().add(collision.un.clone().mul(1.2).mul(resizedV2.dot(collision.un))), progress);
            drawLine(g2D, collision.p2, collision.p2.clone().add(collision.ut.clone().mul(1.2).mul(resizedV2.dot(collision.ut))), progress);
        }

        if (first)
        {
            Vector2 endV1 = collision.p1.clone().add(resizedV1);
            Vector2 endN1 = collision.p1.clone().add(collision.un.clone().mul(resizedV1.dot(collision.un)));
            Vector2 endT1 = collision.p1.clone().add(collision.ut.clone().mul(resizedV1.dot(collision.ut)));
            Vector2 offsetN1 = endN1.clone().sub(endV1);
            Vector2 offsetT1 = endT1.clone().sub(endV1);
            
            drawLine(g2D, endV1, endV1.clone().add(offsetN1.mul(progress)), progress);
            drawLine(g2D, endV1, endV1.clone().add(offsetT1.mul(progress)), progress);
        }
        else
        {
            Vector2 endV2 = collision.p2.clone().add(resizedV2);
            Vector2 endN2 = collision.p2.clone().add(collision.un.clone().mul(resizedV2.dot(collision.un)));
            Vector2 endT2 = collision.p2.clone().add(collision.ut.clone().mul(resizedV2.dot(collision.ut)));
            Vector2 offsetN2 = endN2.clone().sub(endV2);
            Vector2 offsetT2 = endT2.clone().sub(endV2);
            
            drawLine(g2D, endV2, endV2.clone().add(offsetN2.mul(progress)), progress);
            drawLine(g2D, endV2, endV2.clone().add(offsetT2.mul(progress)), progress);
        }

        g2D.setStroke(thickStroke);
        if (first)
        {
            drawLine(g2D, collision.p1, collision.p1.clone().add(resizedV1), 1.0);
        }
        else
        {
            drawLine(g2D, collision.p2, collision.p2.clone().add(resizedV2), 1.0);
        }*/
        g2D.setStroke(thinStroke);
    }
	
	public void renderFinalVelocities(Graphics2D g2D, double progress)
	{
        /*double maxVelocity = Math.max(collision.v1c.length(), collision.v2c.length());
        double maxLength = Math.max(collision.r1, collision.r2) * 1.5;
        Vector2 resizedV1c = collision.v1c.clone().div(maxVelocity / maxLength);
        Vector2 resizedV2c = collision.v2c.clone().div(maxVelocity / maxLength);
        
        drawLine(g2D, collision.p1, collision.p1.clone().add(resizedV1c), progress);
        drawLine(g2D, collision.p2, collision.p2.clone().add(resizedV2c), progress);*/
	}
}
