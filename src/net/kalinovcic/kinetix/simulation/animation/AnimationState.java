package net.kalinovcic.kinetix.simulation.animation;

public enum AnimationState
{
	COLLISION_APPROACH,
	COLLISION_VT,
	COLLISION_LEAVING,
	;

	public AnimationState next;
	
	static
	{
		COLLISION_APPROACH.next = COLLISION_VT;
		COLLISION_VT.next = COLLISION_LEAVING;
		COLLISION_LEAVING.next = COLLISION_APPROACH;
	}
}
