package net.kalinovcic.kinetix.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.Atom;
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

    public List<Double> times;
	@SuppressWarnings("unchecked")
    public List<Double>[] averages = new List[Reactions.ATOM_TYPE_COUNT];
	
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
        bufferedWriter.write(String.format("temp=%.2f ", unit.settings.temperature));
        for (int unique = 0; unique < Reactions.ATOM_TYPE_COUNT; unique++)
            if (unit.atomTypes[unique] != null)
                bufferedWriter.write(String.format("N(%s)=%d ", Reactions.simpleName(unit.atomTypes[unique].name), unit.atomTypes[unique].initialCount));
        bufferedWriter.write("\n");
        for (Reaction reaction : unit.reactions)
        {
            bufferedWriter.write(String.format("%s + %s -> %s + %s\n", Reactions.simpleName(reaction.reactant1), Reactions.simpleName(reaction.reactant2),
                                                                       Reactions.simpleName(reaction.product1), Reactions.simpleName(reaction.product2)));
        }
        bufferedWriter.write("\n");
	}
	
	private void single(double time) throws IOException
	{
	    long stepTimeStart = System.nanoTime();
	    
    	SimulationInitialization.initialize(state);
		state.doesSnapshots = false;
		state.readyToUse = true;
		state.paused = false;
        
        int[] currentCount = new int[Reactions.ATOM_TYPE_COUNT];
        @SuppressWarnings("unchecked")
        List<Integer>[] atomCount = new List[Reactions.ATOM_TYPE_COUNT];
        for (int i = 0; i < atomCount.length; i++)
            atomCount[i] = new ArrayList<Integer>();
        
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
	        
	        Arrays.fill(currentCount, 0);
	        for (Atom atom : state.atoms)
	            currentCount[atom.type.unique]++;
	        
	        for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++)
	        {
	            int count = currentCount[i];
	            atomCount[i].add(count);
	            if (times.size() < index)
	                times.add(current);
    			if (averages[i].size() <= index)
    				averages[i].add((double) count);
    			else
    				averages[i].set(index, averages[i].get(index) + (double) count);
	        }
			index++;
		}

        bufferedWriter.write("STEP\n");
        for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++)
            if (state.atomTypes[i] != null)
            {
                bufferedWriter.write(state.atomTypes[i].name + ":\t");
                for (int count : atomCount[i])
                    bufferedWriter.write(String.format("%d\t", count));
                bufferedWriter.write("\n");
            }
		
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
                state.reactions = unit.reactions;
                state.atomTypes = unit.atomTypes;
                window.unitUpdate();
	    	    
	    		times = new ArrayList<Double>();
	    		for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++)
	    		    averages[i] = new ArrayList<Double>();
	    		
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
                bufferedWriter.write("T");
                for (Double value : times)
                    bufferedWriter.write(String.format("\t%.2f", value.doubleValue()));
                bufferedWriter.write("\n");
	            for (int i = 0; i < Reactions.ATOM_TYPE_COUNT; i++)
	                if (state.atomTypes[i] != null)
	                {
	                    bufferedWriter.write(Reactions.simpleName(state.atomTypes[i].name));
	                    for (Double value : averages[i])
	                        bufferedWriter.write(String.format("\t%.2f", value.doubleValue() / (double) unit.repeat));
	                    bufferedWriter.write("\n");
	                }
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
