package net.kalinovcic.kinetix.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationInitialization;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.test.TestingConfiguration.TestingUnit;

public class TestingThread extends Thread
{
    public MainWindow mainWindow;
	public SimulationState state;
	public TestingConfiguration configuration;
	public TestingWindow window;
	
	public File file;
	public FileWriter fileWriter;
	public BufferedWriter bufferedWriter;
	
	public int theReactant1;
	public int theReactant2;

	public List<Double> times;
	public List<Double> averages;
	
	public double testingCurrentTime = 0.0;
	public double testingTotalTime = 0.0;
    public int testingCurrentStep = 0;
    public int testingTotalSteps = 0;
	public boolean testingFinished = false;
	
	public TestingUnit unit;
    public int unitIndex = 0;
    public int unitRepeat = 0;
    public double stepCurrentTime = 0.0;
    public double stepTime = 0;
    
    public double averageStepTime;
	
	public TestingThread(MainWindow mainWindow, TestingConfiguration configuration)
	{
	    this.mainWindow = mainWindow;
		this.configuration = configuration;
	}
	
	private void unitHeader() throws IOException
	{
	    bufferedWriter.write("UNIT\n");
        bufferedWriter.write(String.format("time=%.2f repeat=%d\n", unit.time, unit.repeat));
        bufferedWriter.write(String.format("temp=%.2f N1=%d N2=%d\n", unit.reaction.temperature, unit.atomTypes[theReactant1].initialCount, unit.atomTypes[theReactant2].initialCount));
        bufferedWriter.write(String.format("%s + %s -> %s + %s\n", Reactions.simpleName(unit.reaction.reactant1), Reactions.simpleName(unit.reaction.reactant2),
                                                                   Reactions.simpleName(unit.reaction.product1), Reactions.simpleName(unit.reaction.product2)));
        bufferedWriter.write("\n");
	}
	
	private void single(double time) throws IOException
	{
	    long stepTimeStart = System.nanoTime();
	    
    	SimulationInitialization.initialize(state);
		state.doesSnapshots = false;
		state.readyToUse = true;
		state.paused = false;

        bufferedWriter.write("STEP ");
        
		int index = 0;
		double current = 0;
		double update = 1.0;
		while (current < time)
		{
			double stepTime = 1 / 20.0;
			
			double delta = Math.min(time - current, stepTime);
			current += delta;
			testingCurrentTime += delta;
			stepCurrentTime += delta;
			update -= delta;
			if (update < 0)
			{
			    while (update < 0)
			        update += 1.0;
			    window.update();
			}

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
		
		long stepTimeEnd = System.nanoTime();
		double stepTime = (stepTimeEnd - stepTimeStart) / 1000000000.0;
		averageStepTime = (averageStepTime * (testingCurrentStep - 1) + stepTime) / testingCurrentStep;
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
    
	    	{
	    	    TestingUnit unit = configuration.head;
	    	    while (unit != null)
	    	    {
	    	        testingTotalTime += unit.repeat * unit.time;
	    	        testingTotalSteps += unit.repeat;
	                unit = unit.next;
	    	    }
	    	}
	    	
	        window = new TestingWindow(mainWindow, this);
	    	
	    	unitIndex = 0;
	    	testingCurrentStep = 0;
	    	testingCurrentTime = 0.0;
	    	
	    	unit = configuration.head;
	    	while (unit != null)
	    	{
	    	    state = new SimulationState();
                state.paused = false;
                state.settings = unit.settings;
                state.reactions = new Reaction[] { unit.reaction };
                state.atomTypes = unit.atomTypes;
                theReactant1 = Reactions.uniqueAtoms.get(unit.reaction.reactant1);
                theReactant2 = Reactions.uniqueAtoms.get(unit.reaction.reactant2);
                window.unitUpdate();
	    	    
	    		times = new ArrayList<Double>();
	    		averages = new ArrayList<Double>();
	    		
	    		unitHeader();

	    		unitRepeat = 0;
	    		stepTime = unit.time;
                unitIndex++;
	    		for (int i = 0; i < unit.repeat; i++)
	    		{
                    testingCurrentStep++;
                    unitRepeat++;
                    stepCurrentTime = 0.0;
                    window.stepUpdate();
                    
	    			single(unit.time);
	    			bufferedWriter.flush();
	    	        window.update();
	    		}

	    		bufferedWriter.write("\n");
	    		for (Double value : times)
	    			bufferedWriter.write(String.format("%.2f\t", value.doubleValue()));
	    		bufferedWriter.write("\n");
	    		for (Double value : averages)
	    			bufferedWriter.write(String.format("%.2f\t", value.doubleValue() / (double) unit.repeat));
                bufferedWriter.write("\n\n");
	    		
	    		unit = unit.next;
	    	}
	    	
	    	testingFinished = true;
	    	window.update();

			bufferedWriter.close();
			fileWriter.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
    	
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
