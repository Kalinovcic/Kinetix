package net.kalinovcic.kinetix.test;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TestingWindow extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

    public MainWindow mainWindow;
	public TestingThread tester;

    public JLabel status1;
    public JLabel status2;
	public JLabel formula;
	public JProgressBar progressBar;
	
	public TestingWindow(MainWindow mainWindow, TestingThread tester)
	{
        super("Testing Monitor", false, false, false, false);
        
        this.mainWindow = mainWindow;
        this.tester = tester;
        
        Dimension size = new Dimension(300, 120);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(250, 250);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));

        setFocusable(true);

        mainWindow.desktop.add(this);
        getContentPane().setLayout(null);

        status1 = new JLabel("");
        status1.setBounds(10, 11, 278, 14);
        getContentPane().add(status1);
        status2 = new JLabel("");
        status2.setBounds(10, 25, 278, 14);
        getContentPane().add(status2);
        
        progressBar = new JProgressBar();
        progressBar.setBounds(10, 66, 278, 24);
        progressBar.setMinimum(0);
        progressBar.setMaximum(10000);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("");
        getContentPane().add(progressBar);
        
        formula = new JLabel("");
        formula.setHorizontalAlignment(SwingConstants.CENTER);
        formula.setText("");
        formula.setBounds(10, 42, 278, 14);
        getContentPane().add(formula);

        setVisible(true);
	}
	
	public void unitUpdate()
	{
	    formula.setText(tester.unit.reaction.reactant1 + " + " + tester.unit.reaction.reactant2 + " → " + tester.unit.reaction.product1 + " + " + tester.unit.reaction.product2);
        status2.setText(String.format("time=%.2f, temp=%.2f, N(%s)=%d, N(%s)=%d",
                tester.stepTime, tester.unit.reaction.temperature,
                tester.unit.reaction.reactant1, tester.state.atomTypes[tester.theReactant1].initialCount,
                tester.unit.reaction.reactant2, tester.state.atomTypes[tester.theReactant2].initialCount));
	}
	
	public void stepUpdate()
	{
	    int remaining = (int)((tester.testingTotalSteps - tester.testingCurrentStep) * tester.averageStepTime);
        int hours = remaining / 60 / 60;
        int minutes = remaining / 60 % 60;
        int seconds = remaining % 60;
        status1.setText(String.format("unit %d, step %d (%02d:%02d:%02d, %.2f s/step)", tester.unitIndex, tester.unitRepeat, hours, minutes, seconds, tester.averageStepTime));
	}
	
	public void update()
	{
	    if (tester.testingFinished) dispose();
	    else
	    {
    	    progressBar.setValue((int)(tester.testingCurrentTime / tester.testingTotalTime * 10000));
    	    progressBar.setString(String.format("%d / %d (%d%% : %d%%)", tester.testingCurrentStep, tester.testingTotalSteps,
    	                                        (int)(tester.testingCurrentTime / tester.testingTotalTime * 100),
    	                                        (int)(tester.stepCurrentTime / tester.stepTime * 100)));
    	    repaint();
	    }
	}
}
