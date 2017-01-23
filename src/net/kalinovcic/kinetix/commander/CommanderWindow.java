package net.kalinovcic.kinetix.commander;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.physics.reaction.chooser.ReactionChooserWindow;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;
import net.kalinovcic.kinetix.test.TestAll;
import net.kalinovcic.kinetix.test.TestingConfiguration;
import net.kalinovcic.kinetix.test.TestingThread;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JCheckBox;
import java.awt.Font;

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
    static
    {
        INTEGER_FORMAT.setGroupingUsed(false);
    }

    public static MainWindow mainWindow;
    
    public static JPanel reactionPanel;
    public static JButton simulationStartButton;
    public static JButton testStartButton;
    public static JCheckBox simulationSteric;
    public static JCheckBox simulation2D;
    public static JCheckBox simulationV;
    public static JFormattedTextField simulationActivationEnergy;
    public static JFormattedTextField simulationReactionStartTime;
    public static JFormattedTextField simulationTimeFactor;
    public static JFormattedTextField simulationTemperature;
    public static JFormattedTextField simulationWidth;
    public static JFormattedTextField simulationHeight;
    public static JFormattedTextField simulationDepth;
    
    public static JLabel simluationTime;
    
    public static final int MAX_TESTS = 12;
    public static int numTests = 6;
    public static JButton testDefaultButton;
    public static JButton testAddButton;
    public static JButton[] testRemoves;
    public static JTextField[] testTimes;
    public static JTextField[] testRepeats;
    public static JTextField[] testScales;
    
    public static Reaction[] selectedReactions;
    public static Map<String, JTextField> atomTypeCount = new HashMap<String, JTextField>();
    public static Map<String, JTextField> atomTypeMass = new HashMap<String, JTextField>();
    public static Map<String, JTextField> atomTypeRadius = new HashMap<String, JTextField>();
    public static Map<String, JButton> atomTypeColor = new HashMap<String, JButton>();
    
    public CommanderWindow(MainWindow mainWindow)
    {
        super("Commander", false, false, false, false);
        CommanderWindow.mainWindow = mainWindow;
        
        Dimension size = new Dimension(400, 450);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(20, 20);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));
        
        setVisible(true);
        
        mainWindow.desktop.add(this);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel simulationPanel = new JPanel();
        tabbedPane.addTab("Simulation", null, simulationPanel, null);
        simulationPanel.setLayout(null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 373, 239);
        simulationPanel.add(scrollPane);
        
        reactionPanel = new JPanel();
        reactionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        reactionPanel.setLayout(new BoxLayout(reactionPanel, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(reactionPanel);

        simulationSteric = new JCheckBox("Steric");
        simulationSteric.setBounds(6, 250, 52, 23);
        simulationPanel.add(simulationSteric);

        simulation2D = new JCheckBox("2D");
        simulation2D.setBounds(60, 250, 38, 23);
        simulationPanel.add(simulation2D);

        simulationV = new JCheckBox("V");
        simulationV.setBounds(100, 250, 38, 23);
        simulationPanel.add(simulationV);
        
        JLabel lblActivationEnergy = new JLabel("Eₐ:");
        lblActivationEnergy.setBounds(140, 256, 18, 17);
        simulationPanel.add(lblActivationEnergy);
        
        simulationActivationEnergy = new JFormattedTextField(NUMBER_FORMAT);
        simulationActivationEnergy.setText("-1");
        simulationActivationEnergy.setBounds(160, 255, 60, 17);
        simulationPanel.add(simulationActivationEnergy);
        
        JLabel lblReactionStartTime = new JLabel("x:");
        lblReactionStartTime.setBounds(230, 256, 18, 17);
        simulationPanel.add(lblReactionStartTime);
        
        simulationReactionStartTime = new JFormattedTextField(NUMBER_FORMAT);
        simulationReactionStartTime.setText("0");
        simulationReactionStartTime.setBounds(250, 255, 60, 17);
        simulationPanel.add(simulationReactionStartTime);
        
        JLabel lblTimeFactor = new JLabel("Time factor:");
        lblTimeFactor.setBounds(10, 276, 76, 14);
        simulationPanel.add(lblTimeFactor);
        
        JLabel lblTemperature = new JLabel("Temp [K]:");
        lblTemperature.setBounds(10, 301, 76, 14);
        simulationPanel.add(lblTemperature);
        
        JLabel lblWidth = new JLabel("Width [px]:");
        lblWidth.setBounds(10, 326, 76, 14);
        simulationPanel.add(lblWidth);
        
        JLabel lblHeight = new JLabel("Height [px]:");
        lblHeight.setBounds(10, 351, 76, 14);
        simulationPanel.add(lblHeight);
        
        JLabel lblDepth = new JLabel("Depth [px]:");
        lblDepth.setBounds(10, 376, 76, 14);
        simulationPanel.add(lblDepth);
        
        simulationTimeFactor = new JFormattedTextField(NUMBER_FORMAT);
        simulationTimeFactor.setText("1");
        simulationTimeFactor.setBounds(88, 275, 60, 17);
        simulationPanel.add(simulationTimeFactor);
        
        simulationTemperature = new JFormattedTextField(NUMBER_FORMAT);
        simulationTemperature.setText("1000");
        simulationTemperature.setBounds(88, 300, 60, 17);
        simulationPanel.add(simulationTemperature);
        
        simulationWidth = new JFormattedTextField(INTEGER_FORMAT);
        simulationWidth.setText("600");
        simulationWidth.setBounds(88, 325, 60, 17);
        simulationPanel.add(simulationWidth);
        
        simulationHeight = new JFormattedTextField(INTEGER_FORMAT);
        simulationHeight.setText("600");
        simulationHeight.setBounds(88, 350, 60, 17);
        simulationPanel.add(simulationHeight);
        
        simulationDepth = new JFormattedTextField(INTEGER_FORMAT);
        simulationDepth.setText("600");
        simulationDepth.setBounds(88, 375, 60, 17);
        simulationPanel.add(simulationDepth);
        
        JButton openReaction = new JButton("Open reaction");
        openReaction.setBounds(211, 311, 172, 23);
        simulationPanel.add(openReaction);
        
        JButton openProfiler = new JButton("Open profiler");
        openProfiler.setBounds(211, 340, 172, 23);
        simulationPanel.add(openProfiler);
        
        simulationStartButton = new JButton("Start");
        simulationStartButton.setBounds(211, 369, 172, 23);
        simulationStartButton.setEnabled(false);
        simulationPanel.add(simulationStartButton);
        
        simluationTime = new JLabel("Time: 0 [s]");
        simluationTime.setFont(new Font("Tahoma", Font.BOLD, 18));
        simluationTime.setBounds(211, 277, 172, 23);
        simulationPanel.add(simluationTime);

        simulationStartButton.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				synchronized (Kinetix.STATE)
				{
					configureSimuationAndReaction(mainWindow);

                    Kinetix.STATE.settings = newSettings;
                    Kinetix.STATE.reactions = newReactions;
                    Kinetix.STATE.atomTypes = newAtomTypes;
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
        
        testStartButton = new JButton("Start testing");
        testStartButton.setBounds(10, 330, 380, 20);
        testStartButton.setEnabled(false);
        testPanel.add(testStartButton);
        
        JButton testAllButton = new JButton("Test all");
        testAllButton.setBounds(10, 356, 380, 20);
        testAllButton.setEnabled(true);
        testPanel.add(testAllButton);

        testStartButton.addActionListener(new ActionListener()
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
							configuration.head = unit;
						else
							previousUnit.next = unit;
						previousUnit = unit;

						try
						{
							unit.time = NUMBER_FORMAT.parse(testTimes[i].getText()).doubleValue();
							unit.repeat = INTEGER_FORMAT.parse(testRepeats[i].getText()).intValue();
							unit.scale = NUMBER_FORMAT.parse(testScales[i].getText()).doubleValue();
						}
						catch (Exception ex)
						{
						    JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
						    return;
						}
						
                        if (newReactions.length > 1)
                        {
                            JOptionPane.showMessageDialog(mainWindow, "Testing only permits one reaction", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        unit.settings = newSettings;
                        unit.reaction = newReactions[0];
                        unit.atomTypes = newAtomTypes;
					}
					
					new TestingThread(mainWindow, configuration).start();
				}
			}
		});
        
        testAllButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                TestAll.testAll(mainWindow);
            }
        });
        
        setTestDefaults();
        updateTestUIState();
        
        selectReactions(null);
    }
    
    public static void selectReactions(Reaction[] newReactions)
    {
        if (newReactions == null || newReactions.length == 0)
        {
            selectedReactions = null;

            atomTypeCount.clear();
            atomTypeMass.clear();
            atomTypeRadius.clear();
            atomTypeColor.clear();
            
            reactionPanel.removeAll();
            reactionPanel.add(new JLabel("(no reactions selected)"));
            
            simulationStartButton.setEnabled(false);
            testStartButton.setEnabled(false);
        }
        else
        {
            selectedReactions = newReactions;
            
            atomTypeCount.clear();
            atomTypeMass.clear();
            atomTypeRadius.clear();
            atomTypeColor.clear();
            
            reactionPanel.removeAll();

            HashSet<String> participants = new HashSet<String>();
            for (Reaction reaction : newReactions)
            {
                String formula = reaction.reactant1 + " + " + reaction.reactant2 + " → " + reaction.product1 + " + " + reaction.product2;
                JLabel label = new JLabel(formula);
                label.setAlignmentX(Component.LEFT_ALIGNMENT);
                reactionPanel.add(label);

                participants.add(reaction.reactant1);
                participants.add(reaction.reactant2);
                participants.add(reaction.product1);
                participants.add(reaction.product2);
            }
            
            reactionPanel.add(Box.createVerticalStrut(8));
            //reactionPanel.add(new JSeparator());
            
            JPanel participantHeaderPanel = new JPanel();
            participantHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            participantHeaderPanel.setMinimumSize(new Dimension(324, 20));
            participantHeaderPanel.setMaximumSize(new Dimension(324, 20));
            participantHeaderPanel.setLayout(new BoxLayout(participantHeaderPanel, BoxLayout.X_AXIS));
            participantHeaderPanel.add(Box.createHorizontalStrut(84));
            JLabel countLabel = new JLabel("n [1]");
            countLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            countLabel.setMinimumSize(new Dimension(80, 14));
            countLabel.setMaximumSize(new Dimension(80, 14));
            participantHeaderPanel.add(countLabel);
            participantHeaderPanel.add(Box.createHorizontalStrut(2));
            JLabel massLabel = new JLabel("M [g⁄mol]");
            massLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            massLabel.setMinimumSize(new Dimension(80, 14));
            massLabel.setMaximumSize(new Dimension(80, 14));
            participantHeaderPanel.add(massLabel);
            participantHeaderPanel.add(Box.createHorizontalStrut(2));
            JLabel radiusLabel = new JLabel("r [Å]");
            radiusLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
            radiusLabel.setMinimumSize(new Dimension(80, 14));
            radiusLabel.setMaximumSize(new Dimension(80, 14));
            participantHeaderPanel.add(radiusLabel);
            reactionPanel.add(participantHeaderPanel);
            reactionPanel.add(Box.createVerticalStrut(2));
            
            int participantIndex = 0;
            for (String participant : participants)
            {
                JPanel participantPanel = new JPanel();
                participantPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                participantPanel.setMinimumSize(new Dimension(346, 20));
                participantPanel.setMaximumSize(new Dimension(346, 20));
                participantPanel.setLayout(new BoxLayout(participantPanel, BoxLayout.X_AXIS));
                
                JLabel label = new JLabel(participant);
                label.setAlignmentY(Component.CENTER_ALIGNMENT);
                label.setMinimumSize(new Dimension(80, 14));
                label.setMaximumSize(new Dimension(80, 14));
                participantPanel.add(label);

                JTextField count = new JFormattedTextField(INTEGER_FORMAT);
                count.setText("0");
                count.setAlignmentY(Component.CENTER_ALIGNMENT);
                count.setMinimumSize(new Dimension(80, 20));
                count.setMaximumSize(new Dimension(80, 20));
                participantPanel.add(count);
                participantPanel.add(Box.createHorizontalStrut(2));
                atomTypeCount.put(participant, count);

                JTextField mass = new JFormattedTextField(NUMBER_FORMAT);
                mass.setText(NUMBER_FORMAT.format(Reactions.findMass(participant)));
                mass.setAlignmentY(Component.CENTER_ALIGNMENT);
                mass.setMinimumSize(new Dimension(80, 20));
                mass.setMaximumSize(new Dimension(80, 20));
                participantPanel.add(mass);
                participantPanel.add(Box.createHorizontalStrut(2));
                atomTypeMass.put(participant, mass);

                JTextField radius = new JFormattedTextField(NUMBER_FORMAT);
                radius.setText(NUMBER_FORMAT.format(Reactions.findRadius(participant)));
                radius.setAlignmentY(Component.CENTER_ALIGNMENT);
                radius.setMinimumSize(new Dimension(80, 20));
                radius.setMaximumSize(new Dimension(80, 20));
                participantPanel.add(radius);
                participantPanel.add(Box.createHorizontalStrut(2));
                atomTypeRadius.put(participant, radius);
                
                float colorHue = (participantIndex / (float) participants.size()) * 0.8f;
                Color color = new Color(Color.HSBtoRGB(colorHue, 1.0f, 1.0f));
                
                JButton colorChoice = new ColoredButton();
                colorChoice.setMinimumSize(new Dimension(20, 20));
                colorChoice.setMaximumSize(new Dimension(20, 20));
                colorChoice.setBackground(color);
                participantPanel.add(colorChoice);
                atomTypeColor.put(participant, colorChoice);
                
                colorChoice.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Color newColor = JColorChooser.showDialog(mainWindow, "Choose color", colorChoice.getBackground());
                        if (newColor != null)
                        {
                            newColor = new Color(newColor.getRed(), newColor.getGreen(), newColor.getBlue());
                            colorChoice.setBackground(newColor);
                        }
                    }
                });
                
                reactionPanel.add(participantPanel);
                reactionPanel.add(Box.createVerticalStrut(2));

                participantIndex++;
            }
            
            simulationStartButton.setEnabled(true);
            testStartButton.setEnabled(true);
        }
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
    private Reaction[] newReactions;
    private AtomType[] newAtomTypes;
    
    private void configureSimuationAndReaction(MainWindow mainWindow)
    {
    	newSettings = new SimulationSettings();
    	newReactions = new Reaction[selectedReactions.length];
    	newAtomTypes = new AtomType[Reactions.ATOM_TYPE_COUNT];
    	
    	// Simulation settings
        
        try
        {
            newSettings.doSteric = simulationSteric.isSelected();
            newSettings.do2D = simulation2D.isSelected();
            newSettings.doV = simulationV.isSelected();
            newSettings.activationEnergy = NUMBER_FORMAT.parse(simulationActivationEnergy.getText()).doubleValue();
            newSettings.reactionStartTime = NUMBER_FORMAT.parse(simulationReactionStartTime.getText()).doubleValue();
            newSettings.timeFactor = NUMBER_FORMAT.parse(simulationTimeFactor.getText()).doubleValue();
            newSettings.temperature = NUMBER_FORMAT.parse(simulationTemperature.getText()).doubleValue();
            newSettings.width = INTEGER_FORMAT.parse(simulationWidth.getText()).intValue();
            newSettings.height = INTEGER_FORMAT.parse(simulationHeight.getText()).intValue();
            newSettings.depth = INTEGER_FORMAT.parse(simulationDepth.getText()).intValue();
            if (newSettings.reactionStartTime < 0.0) throw new Exception();
            if (newSettings.temperature < 0.0) throw new Exception();
            if (newSettings.timeFactor <= 0.0) throw new Exception();
            if (newSettings.width < 1) throw new Exception();
            if (newSettings.height < 1) throw new Exception();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Atom types
    	
    	Set<String> atomTypes = atomTypeCount.keySet();
    	for (String type : atomTypes)
    	{
    	    int unique = Reactions.uniqueAtoms.get(type);

            JTextField countField = atomTypeCount.get(type);
            JTextField massField = atomTypeMass.get(type);
            JTextField radiusField = atomTypeRadius.get(type);
            JButton colorButton = atomTypeColor.get(type);
            
            int count = 0;
            double mass = 0.0;
            double radius = 0.0;
            Color color = colorButton.getBackground();

            try
            {
                count = INTEGER_FORMAT.parse(countField.getText()).intValue();
                mass = NUMBER_FORMAT.parse(massField.getText()).doubleValue();
                radius = NUMBER_FORMAT.parse(radiusField.getText()).doubleValue();

                if (mass < 0.0) throw new Exception();
                if (radius < 0.0) throw new Exception();
                if (count < 0) throw new Exception();
            }
            catch (Exception ex)
            {
                JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            
    	    newAtomTypes[unique] = new AtomType();

            newAtomTypes[unique].name = type;
            newAtomTypes[unique].unique = unique;
    	    
            newAtomTypes[unique].initialCount = count;
            
            newAtomTypes[unique].mass = mass;
            newAtomTypes[unique].radius = radius;
    	    newAtomTypes[unique].color = color;
    	}
    	
    	// Reactions
		
        for (int index = 0; index < newReactions.length; index++)
        {
            int unique1 = Reactions.uniqueAtoms.get(selectedReactions[index].reactant1);
            int unique2 = Reactions.uniqueAtoms.get(selectedReactions[index].reactant2);
            
            newReactions[index] = selectedReactions[index].clone();
            newReactions[index].mass1 = newAtomTypes[unique1].mass;
            newReactions[index].mass2 = newAtomTypes[unique2].mass;
            newReactions[index].radius1 = newAtomTypes[unique1].radius;
            newReactions[index].radius2 = newAtomTypes[unique2].radius;
            newReactions[index].radius2 = newAtomTypes[unique2].radius;
            newReactions[index].temperature = newSettings.temperature;
            newReactions[index].recalculate();
            
            newAtomTypes[unique1].reactantInReaction = newReactions[index];
            newAtomTypes[unique2].reactantInReaction = newReactions[index];
        }
    }
}