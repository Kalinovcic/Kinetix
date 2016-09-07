package net.kalinovcic.kinetix.physics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JFileChooser;

public class TestingConfiguration
{
	public TestingUnit firstUnit;
	public TestingUnit currentUnit;
	
	public static class TestingUnit
	{
		public TestingUnit next;
		
		public int countReactions[];

		public int repeat;
		public double time;
		public double scale;
		
		public int repeatRemaining;
		public double timeRemaining;
	}
	
	public void writeOut()
	{
    	JFileChooser chooser = new JFileChooser();
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setMultiSelectionEnabled(false);
    	chooser.setDialogTitle("Save testing results");
    	
    	int result = chooser.showSaveDialog(null);
    	if (result != JFileChooser.APPROVE_OPTION)
    	{
    		return;
    	}

    	try
    	{
    		File f = chooser.getSelectedFile();
    		
			FileWriter fW = new FileWriter(f);
			BufferedWriter bW = new BufferedWriter(fW);
			
	    	TestingUnit unit = firstUnit;
	    	while (unit != null)
	    	{
	    		for (int i = 0; i < unit.countReactions.length; i++)
	    		{
	    			if (i != 0) bW.write('\t');
	    			bW.write(String.format("%.2f", unit.countReactions[i] * unit.scale));
	    		}
	    		bW.write('\n');
	    		
	    		unit = unit.next;
	    	}
	    	
	    	bW.close();
	    	fW.close();
    	}
    	catch (Exception ex)
    	{
    		ex.printStackTrace();
    	}
	}
}
