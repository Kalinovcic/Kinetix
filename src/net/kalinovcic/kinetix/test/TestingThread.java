package net.kalinovcic.kinetix.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.kalinovcic.kinetix.physics.SimulationInitialization;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.test.TestingConfiguration.TestingUnit;

public class TestingThread extends Thread
{
	public SimulationState state;
	public TestingConfiguration configuration;
	
	public File file;
	public FileWriter fileWriter;
	public BufferedWriter bufferedWriter;
	
	public Reaction theReaction;
	public int theReactant1;
	public int theReactant2;

	public List<Double> times;
	public List<Double> averages;
	
	public TestingThread(SimulationState state, TestingConfiguration configuration)
	{
		this.state = state;
		this.configuration = configuration;
	}
	
	private void single(double time) throws IOException
	{
    	SimulationInitialization.initialize(state);
		state.doesSnapshots = false;
		state.readyToUse = true;
		state.paused = false;
		
		int index = 0;
		double current = 0;
		while (current < time)
		{
			double stepTime = 1 / 5.0;
			if (time < 2)
				stepTime = 1 / 20.0;
			
			double delta = Math.min(time - current, stepTime);
			current += delta;

	        state.update(delta);
	        
	        int count = state.collisionInfo[theReactant1][theReactant2][1];
			bufferedWriter.write(String.format("%d\t", count));
			
			if (averages.size() <= index)
			{
				averages.add((double) count);
				times.add(current);
			}
			else
				averages.set(index, averages.get(index) + (double) count);
			index++;
		}
		
		bufferedWriter.write("\n");
	}
	
    @Override
    public void run()
    {
    	JFileChooser chooser = new JFileChooser();
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setMultiSelectionEnabled(false);
    	chooser.setDialogTitle("Save testing results");
    	
    	int result = chooser.showSaveDialog(null);
    	if (result != JFileChooser.APPROVE_OPTION)
    		return;

    	try
    	{
    		file = chooser.getSelectedFile();
			fileWriter = new FileWriter(file);
			bufferedWriter = new BufferedWriter(fileWriter);
    	
	    	for (Reaction reaction : state.reactions)
	    		if (reaction != null)
	    		{
	    			theReaction = reaction;
	    			theReactant1 = Reactions.uniqueAtoms.get(theReaction.reactant1);
	    			theReactant2 = Reactions.uniqueAtoms.get(theReaction.reactant2);
	    			break;
	    		}
	    	
	    	TestingUnit unit = configuration.head;
	    	while (unit != null)
	    	{
	    		times = new ArrayList<Double>();
	    		averages = new ArrayList<Double>();
	    		
	    		bufferedWriter.write("UNIT: time " + String.format("%.2f", unit.time) + " repeat " + unit.repeat + "\n\n");
	    		
	    		for (int i = 0; i < unit.repeat; i++)
	    		{
	    			single(unit.time);
	    			bufferedWriter.flush();
	    		}

	    		bufferedWriter.write("\n");
	    		for (Double value : times)
	    			bufferedWriter.write(String.format("%.2f\t", value.doubleValue()));
	    		bufferedWriter.write("\n");
	    		for (Double value : averages)
	    			bufferedWriter.write(String.format("%.2f\t", value.doubleValue() / (double) unit.repeat));
	    		bufferedWriter.write("\n");
	    		
	    		unit = unit.next;
	    	}

			bufferedWriter.close();
			fileWriter.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	System.out.println("Done");
    	
    	/*
    	unit.timeRemaining -= deltaTime;
    	if (unit.timeRemaining <= 0.0)
    	{
    		unit.timeRemaining = unit.time;

            int reactant1 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant1);
            int reactant2 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant2);
            int reactions = state.collisionInfo[reactant1][reactant2][1];
    		unit.countReactions[unit.repeat - unit.repeatRemaining] = reactions;
    		unit.repeatRemaining--;
    		if (unit.repeatRemaining <= 0)
    		{
    			unit.repeatRemaining = unit.repeat;
    			
    			Kinetix.testing.currentUnit = unit.next;
    			if (unit.next == null)
    			{
    				Kinetix.testing.writeOut();
    				Kinetix.testing = null;
    			}
    		}
    		
			Kinetix.restart = true;
    	}
    	*/
    }
}
