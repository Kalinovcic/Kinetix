package net.kalinovcic.kinetix.commander;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.TestingConfiguration;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.chooser.ReactionChooserWindow;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;

public class CommanderWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

	private static final DecimalFormatSymbols OTHER_SYMBOLS = new DecimalFormatSymbols(Locale.US);
	static
	{
		OTHER_SYMBOLS.setDecimalSeparator('.');
		OTHER_SYMBOLS.setGroupingSeparator(',');
	}
	
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat();
	static
	{
		NUMBER_FORMAT.setDecimalFormatSymbols(OTHER_SYMBOLS);
		NUMBER_FORMAT.setGroupingUsed(false);
	}
	
    public static final NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance();
    
    public static JFormattedTextField simulationRedCount;
    public static JFormattedTextField simulationRedRadius;
    public static JFormattedTextField simulationRedMass;
    public static JFormattedTextField simulationGreenCount;
    public static JFormattedTextField simulationGreenRadius;
    public static JFormattedTextField simulationGreenMass;
    public static JFormattedTextField simulationTemperature;
    public static JFormattedTextField simulationActivationEnergy;
    public static JFormattedTextField simulationWidth;
    public static JFormattedTextField simulationHeight;
    
    public static final int MAX_TESTS = 8;
    public static int numTests = 6;
    public static JButton testDefaultButton;
    public static JButton testAddButton;
    public static JButton[] testRemoves;
    public static JTextField[] testTimes;
    public static JTextField[] testRepeats;
    public static JTextField[] testScales;
    
    public CommanderWindow(MainWindow mainWindow)
    {
        super("Commander", false, false, false, false);
        
        Dimension size = new Dimension(400, 300);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(620, 10);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));
        
        setVisible(true);
        
        mainWindow.desktop.add(this);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel simulationPanel = new JPanel();
        tabbedPane.addTab("Simulation", null, simulationPanel, null);
        simulationPanel.setLayout(null);
        
        JButton openReaction = new JButton("Open reaction");
        openReaction.setBounds(211, 161, 172, 23);
        simulationPanel.add(openReaction);
        
        JButton openProfiler = new JButton("Open profiler");
        openProfiler.setBounds(211, 190, 172, 23);
        simulationPanel.add(openProfiler);
        
        JButton simulationStart = new JButton("Start");
        simulationStart.setBounds(211, 219, 172, 23);
        simulationPanel.add(simulationStart);
        
        JLabel lblRedProperties = new JLabel("Red properties...");
        lblRedProperties.setBounds(10, 11, 143, 14);
        simulationPanel.add(lblRedProperties);
        
        JLabel lblRedCount = new JLabel("Count:");
        lblRedCount.setBounds(20, 36, 66, 14);
        simulationPanel.add(lblRedCount);
        
        JLabel lblRedRadius = new JLabel("Radius [Å]:");
        lblRedRadius.setBounds(20, 61, 66, 14);
        simulationPanel.add(lblRedRadius);
        
        JLabel lblRedMass = new JLabel("Mass [g/mol]:");
        lblRedMass.setBounds(20, 86, 66, 14);
        simulationPanel.add(lblRedMass);
        
        simulationRedCount = new JFormattedTextField(INTEGER_FORMAT);
        simulationRedCount.setText("100");
        simulationRedCount.setBounds(96, 35, 60, 17);
        simulationPanel.add(simulationRedCount);
        
        simulationRedRadius = new JFormattedTextField(NUMBER_FORMAT);
        simulationRedRadius.setText("2");
        simulationRedRadius.setBounds(96, 60, 60, 17);
        simulationPanel.add(simulationRedRadius);
        
        simulationRedMass = new JFormattedTextField(NUMBER_FORMAT);
        simulationRedMass.setText("30");
        simulationRedMass.setBounds(96, 85, 60, 17);
        simulationPanel.add(simulationRedMass);
        
        JLabel lblGreenProperties = new JLabel("Green properties...");
        lblGreenProperties.setBounds(201, 11, 143, 14);
        simulationPanel.add(lblGreenProperties);
        
        JLabel lblGreenCount = new JLabel("Count:");
        lblGreenCount.setBounds(211, 36, 66, 14);
        simulationPanel.add(lblGreenCount);
        
        JLabel lblGreenRadius = new JLabel("Radius [Å]:");
        lblGreenRadius.setBounds(211, 61, 66, 14);
        simulationPanel.add(lblGreenRadius);
        
        JLabel lblGreenMass = new JLabel("Mass [g⁄mol]:");
        lblGreenMass.setBounds(211, 86, 66, 14);
        simulationPanel.add(lblGreenMass);

        simulationGreenCount = new JFormattedTextField(INTEGER_FORMAT);
        simulationGreenCount.setText("100");
        simulationGreenCount.setBounds(287, 35, 60, 17);
        simulationPanel.add(simulationGreenCount);
        
        simulationGreenRadius = new JFormattedTextField(NUMBER_FORMAT);
        simulationGreenRadius.setText("2.5");
        simulationGreenRadius.setBounds(287, 60, 60, 17);
        simulationPanel.add(simulationGreenRadius);
        
        simulationGreenMass = new JFormattedTextField(NUMBER_FORMAT);
        simulationGreenMass.setText("48");
        simulationGreenMass.setBounds(287, 85, 60, 17);
        simulationPanel.add(simulationGreenMass);
        
        JLabel lblEnvironmentProperties = new JLabel("Environment properties...");
        lblEnvironmentProperties.setBounds(10, 117, 174, 14);
        simulationPanel.add(lblEnvironmentProperties);
        
        JLabel lblTemperature = new JLabel("Temp [K]:");
        lblTemperature.setBounds(20, 141, 76, 14);
        simulationPanel.add(lblTemperature);
        
        JLabel lblActivationEnergy = new JLabel("Eₐ [kJ⁄mol]:");
        lblActivationEnergy.setBounds(20, 166, 76, 14);
        simulationPanel.add(lblActivationEnergy);
        
        JLabel lblWidth = new JLabel("Width [px]:");
        lblWidth.setBounds(20, 191, 76, 14);
        simulationPanel.add(lblWidth);
        
        JLabel lblHeight = new JLabel("Height [px]:");
        lblHeight.setBounds(20, 216, 76, 14);
        simulationPanel.add(lblHeight);
        
        simulationTemperature = new JFormattedTextField(NUMBER_FORMAT);
        simulationTemperature.setText("1000");
        simulationTemperature.setBounds(98, 140, 60, 17);
        simulationPanel.add(simulationTemperature);
        
        simulationActivationEnergy = new JFormattedTextField(NUMBER_FORMAT);
        simulationActivationEnergy.setText("200");
        simulationActivationEnergy.setBounds(98, 165, 60, 17);
        simulationPanel.add(simulationActivationEnergy);
        
        simulationWidth = new JFormattedTextField(INTEGER_FORMAT);
        simulationWidth.setText("600");
        simulationWidth.setBounds(98, 190, 60, 17);
        simulationPanel.add(simulationWidth);
        
        simulationHeight = new JFormattedTextField(INTEGER_FORMAT);
        simulationHeight.setText("600");
        simulationHeight.setBounds(98, 215, 60, 17);
        simulationPanel.add(simulationHeight);

        simulationStart.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				synchronized (Kinetix.STATE)
				{
					configureSimuationAndReaction(mainWindow);
					
					Kinetix.STATE.settings = newSettings;
					Kinetix.reaction = newReaction;
					Kinetix.testing = null;
					Kinetix.restart = true;
				}
			}
		});
        
        openProfiler.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
                new ProfilerWindow(mainWindow);
			}
		});
        
        openReaction.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				new ReactionChooserWindow(mainWindow);
			}
		});
        
        JPanel testPanel = new JPanel();
        tabbedPane.addTab("Testing", null, testPanel, null);
        testPanel.setLayout(null);
        
        JLabel lblTime = new JLabel("Time");
        lblTime.setBounds(100, 11, 46, 14);
        testPanel.add(lblTime);
        
        JLabel lblRepeat = new JLabel("Repeat");
        lblRepeat.setBounds(190, 11, 46, 14);
        testPanel.add(lblRepeat);
        
        JLabel lblScale = new JLabel("Scale");
        lblScale.setBounds(280, 11, 46, 14);
        testPanel.add(lblScale);
        
        testDefaultButton = new JButton("Defaults");
        testDefaultButton.setBounds(10, 11, 80, 20);
        testPanel.add(testDefaultButton);
        
        testDefaultButton.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				setTestDefaults();
				updateTestUIState();
			}
		});
        
        testAddButton = new JButton("Add");
        testAddButton.setBounds(10, 36, 80, 20);
        testPanel.add(testAddButton);
        
        testAddButton.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				testTimes[numTests].setText("1");
				testRepeats[numTests].setText("1");
				testScales[numTests].setText("1");
				
				numTests++;
				
				updateTestUIState();
			}
		});
        
        testRemoves = new JButton[MAX_TESTS];
        testTimes = new JTextField[MAX_TESTS];
        testRepeats = new JTextField[MAX_TESTS];
        testScales = new JTextField[MAX_TESTS];
        
        for (int i = 0; i < MAX_TESTS; i++)
        {
        	testRemoves[i] = new JButton("Remove");
        	testRemoves[i].setBounds(10, 36 + i*23, 80, 20);
	        testPanel.add(testRemoves[i]);
	        
	        final int I = i;
	        testRemoves[i].addActionListener(new ActionListener()
	        {
				public void actionPerformed(ActionEvent e)
				{
					numTests--;
					
					for (int i = I; i < numTests; i++)
					{
						testTimes[i].setText(testTimes[i + 1].getText());
						testRepeats[i].setText(testRepeats[i + 1].getText());
						testScales[i].setText(testScales[i + 1].getText());
					}
					
					updateTestUIState();
				}
			});
        	
        	testTimes[i] = new JFormattedTextField(NUMBER_FORMAT);
	        testTimes[i].setColumns(10);
	        testTimes[i].setBounds(100, 36 + i*23, 80, 20);
	        testPanel.add(testTimes[i]);

	        testRepeats[i] = new JFormattedTextField(INTEGER_FORMAT);
	        testRepeats[i].setColumns(10);
	        testRepeats[i].setBounds(190, 36 + i*23, 80, 20);
	        testPanel.add(testRepeats[i]);

	        testScales[i] = new JFormattedTextField(NUMBER_FORMAT);
	        testScales[i].setColumns(10);
	        testScales[i].setBounds(280, 36 + i*23, 80, 20);
	        testPanel.add(testScales[i]);
        }
        
        JButton testStart = new JButton("Start testing");
        testStart.setBounds(10, 230, 380, 20);
        testPanel.add(testStart);

        testStart.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				synchronized (Kinetix.STATE)
				{
					configureSimuationAndReaction(mainWindow);
					
					TestingConfiguration configuration = new TestingConfiguration();
					TestingConfiguration.TestingUnit previousUnit = null;
					for (int i = 0; i < numTests; i++)
					{
						TestingConfiguration.TestingUnit unit = new TestingConfiguration.TestingUnit();
						if (i == 0)
							configuration.firstUnit = configuration.currentUnit = unit;
						else
							previousUnit.next = unit;
						previousUnit = unit;

						try
						{
							unit.time = unit.timeRemaining = NUMBER_FORMAT.parse(testTimes[i].getText()).doubleValue();
							unit.repeat = unit.repeatRemaining = INTEGER_FORMAT.parse(testRepeats[i].getText()).intValue();
							unit.scale = NUMBER_FORMAT.parse(testScales[i].getText()).doubleValue();
							
							unit.countReactions = new int[unit.repeat];
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

					Kinetix.STATE.paused = false;
					Kinetix.STATE.settings = newSettings;
					Kinetix.reaction = newReaction;
					Kinetix.testing = configuration;
					Kinetix.restart = true;
				}
			}
		});
        
        setTestDefaults();
        updateTestUIState();
    }
    
    private void setTestDefaults()
    {
        numTests = 6;
        
        for (int i = 0; i < MAX_TESTS; i++)
        {
	        switch (i)
	        {
	        case 0: testTimes[i].setText("20");  testRepeats[i].setText("5");    testScales[i].setText("0.05"); break;
	        case 1: testTimes[i].setText("10");  testRepeats[i].setText("10");   testScales[i].setText("0.01"); break;
	        case 2: testTimes[i].setText("1");   testRepeats[i].setText("100");  testScales[i].setText("1");    break;
	        case 3: testTimes[i].setText("0.5"); testRepeats[i].setText("200");  testScales[i].setText("2");    break;
	        case 4: testTimes[i].setText("0.2"); testRepeats[i].setText("500");  testScales[i].setText("5");    break;
	        case 5: testTimes[i].setText("0.1"); testRepeats[i].setText("1000"); testScales[i].setText("10");   break;
	        }
        }
    }
    
    private void updateTestUIState()
    {
    	if (numTests < MAX_TESTS)
    	{
    		testAddButton.setVisible(true);
    		testAddButton.setBounds(10, 36 + numTests*23, 80, 20);
    	}
    	else
    	{
    		testAddButton.setVisible(false);
    	}
    	
        for (int i = 0; i < MAX_TESTS; i++)
        {
        	testRemoves[i].setEnabled(numTests > 1);
	        if (i >= numTests)
	        {
	        	testRemoves[i].setVisible(false);
	        	testTimes[i].setVisible(false);
	        	testRepeats[i].setVisible(false);
	        	testScales[i].setVisible(false);
	        }
	        else
	        {
	        	testRemoves[i].setVisible(true);
	        	testTimes[i].setVisible(true);
	        	testRepeats[i].setVisible(true);
	        	testScales[i].setVisible(true);
	        }
        }
    }

    private SimulationSettings newSettings;
    private Reaction newReaction;
    
    private void configureSimuationAndReaction(MainWindow mainWindow)
    {
    	newSettings = new SimulationSettings();
    	newReaction = new Reaction();
		
		try
		{
			newSettings.width = INTEGER_FORMAT.parse(simulationWidth.getText()).intValue();
			newSettings.height = INTEGER_FORMAT.parse(simulationHeight.getText()).intValue();
			if (newSettings.width < 1) throw new Exception();
			if (newSettings.height < 1) throw new Exception();
			
			newSettings.redCount = INTEGER_FORMAT.parse(simulationRedCount.getText()).intValue();
			newSettings.greenCount = INTEGER_FORMAT.parse(simulationGreenCount.getText()).intValue();
			if (newSettings.redCount < 0) throw new Exception();
			if (newSettings.greenCount < 0) throw new Exception();
			
			newReaction.mass1 = NUMBER_FORMAT.parse(simulationRedMass.getText()).doubleValue();
			newReaction.mass2 = NUMBER_FORMAT.parse(simulationGreenMass.getText()).doubleValue();
			if (newReaction.mass1 <= 0) throw new Exception();
			if (newReaction.mass2 <= 0) throw new Exception();
			
			newReaction.radius1 = NUMBER_FORMAT.parse(simulationRedRadius.getText()).doubleValue();
			newReaction.radius2 = NUMBER_FORMAT.parse(simulationGreenRadius.getText()).doubleValue();
			if (newReaction.radius1 <= 0) throw new Exception();
			if (newReaction.radius2 <= 0) throw new Exception();

			newReaction.activationEnergy = NUMBER_FORMAT.parse(simulationActivationEnergy.getText()).doubleValue();
			newReaction.ratio = newReaction.activationEnergy / (Reaction.IDEAL_GAS / 1000);
			
			newReaction.temperature = NUMBER_FORMAT.parse(simulationTemperature.getText()).doubleValue();
			if (newReaction.temperature < 0) throw new Exception();
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		newReaction.recalculate();
    }
}