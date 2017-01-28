package net.kalinovcic.kinetix.profiler.atomsovervelocity;

public class AtomsOverVelocityThread
{
    /*
    public AtomsOverVelocity profiler;
    public AtomsOverVelocityRenderer renderer;
    
    public AtomsOverVelocityThread(AtomsOverVelocity profiler)
    {
        super(60);
        this.profiler = profiler;
    }
    
    @Override
    public void initialize()
    {
        renderer = new AtomsOverVelocityRenderer(profiler);
    }
    
    public void updateProfile(SimulationState state, double deltaTime)
    {
        if (profiler.columns == null || profiler.columns.length != profiler.frameInterval || profiler.columns[0].length != profiler.columnCount)
        {
        	int[][][] newColumns = new int[profiler.frameInterval][profiler.columnCount][Reactions.ATOM_TYPE_COUNT];
        	
        	profiler.availableFrames = 0;
        	if (profiler.columns != null)
        	{
            	profiler.availableFrames = Math.min(profiler.columns.length, profiler.frameInterval);
        		for (int frame = 0; frame < profiler.availableFrames; frame++)
        			for (int column = 0; column < Math.min(profiler.columns[0].length, profiler.columnCount); column++)
        				for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
        					newColumns[frame][column][type] = profiler.columns[frame][column][type];
        	}
        	
        	profiler.columns = newColumns;
        }
        
        if (profiler.averages == null || profiler.averages.length != profiler.columnCount)
        	profiler.averages = new double[profiler.columnCount][Reactions.ATOM_TYPE_COUNT];
    	
    	profiler.currentFrame %= profiler.frameInterval;
        for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
	        for (int column = 0; column < profiler.columnCount; column++)
	        	profiler.columns[profiler.currentFrame][column][type] = 0;
        
        for (Atom atom : state.atoms)
        {
            double velocity = atom.velocity.length();
            int index = (int) (velocity / profiler.velocityInterval);
            if (index < profiler.columnCount)
            	profiler.columns[profiler.currentFrame][index][atom.type.unique]++;
        }
        profiler.availableFrames = Math.min(profiler.availableFrames + 1, profiler.frameInterval);
        
        for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
        {
            profiler.maximumAverage[type] = 0;
	        for (int column = 0; column < profiler.columnCount; column++)
	        {
	        	profiler.averages[column][type] = 0;
	        	for (int frame = 0; frame < profiler.availableFrames; frame++)
	        	{
	        		int frameIndex = (profiler.currentFrame + frame) % profiler.frameInterval;
	        		profiler.averages[column][type] += profiler.columns[frameIndex][column][type];
	        	}
	        	profiler.averages[column][type] /= profiler.availableFrames;
	            profiler.maximumAverage[type] = Math.max(profiler.maximumAverage[type], profiler.averages[column][type]);
	        }
        }
        
    	profiler.currentFrame = (profiler.currentFrame + 1) % profiler.frameInterval;
    }
    
    @Override
    public void synchronizedUpdate(SimulationState state, double deltaTime)
    {
        if (!state.readyToUse) return;
    	synchronized (profiler)
    	{
            if (!state.paused) profiler.paused = false;
            if (profiler.paused) return;
            
    		updateProfile(state, deltaTime);
    		
	    	if (!profiler.isShowing()) return;
    		renderer.render(state, deltaTime);
        	
            if (state.paused) profiler.paused = true;
    	}
    }
    */
}
