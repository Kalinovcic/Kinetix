package net.kalinovcic.kinetix.commander;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationSettings;
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
					SimulationSettings settings = new SimulationSettings();
					Reaction reaction = new Reaction();
					
					try
					{
						settings.width = INTEGER_FORMAT.parse(simulationWidth.getText()).intValue();
						settings.height = INTEGER_FORMAT.parse(simulationHeight.getText()).intValue();
						if (settings.width < 1) throw new Exception();
						if (settings.height < 1) throw new Exception();
						
						settings.redCount = INTEGER_FORMAT.parse(simulationRedCount.getText()).intValue();
						settings.greenCount = INTEGER_FORMAT.parse(simulationGreenCount.getText()).intValue();
						if (settings.redCount < 0) throw new Exception();
						if (settings.greenCount < 0) throw new Exception();
						
						reaction.mass1 = NUMBER_FORMAT.parse(simulationRedMass.getText()).doubleValue();
						reaction.mass2 = NUMBER_FORMAT.parse(simulationGreenMass.getText()).doubleValue();
						if (reaction.mass1 <= 0) throw new Exception();
						if (reaction.mass2 <= 0) throw new Exception();
						
						reaction.radius1 = NUMBER_FORMAT.parse(simulationRedRadius.getText()).doubleValue();
						reaction.radius2 = NUMBER_FORMAT.parse(simulationGreenRadius.getText()).doubleValue();
						if (reaction.radius1 <= 0) throw new Exception();
						if (reaction.radius2 <= 0) throw new Exception();

						reaction.activationEnergy = NUMBER_FORMAT.parse(simulationActivationEnergy.getText()).doubleValue();
						reaction.ratio = reaction.activationEnergy / (Reaction.IDEAL_GAS / 1000);
						
						reaction.temperature = NUMBER_FORMAT.parse(simulationTemperature.getText()).doubleValue();
						if (reaction.temperature < 0) throw new Exception();
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
					}
					
					reaction.recalculate();
				
					Kinetix.STATE.settings = settings;
					Kinetix.reaction = reaction;
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
        
        JPanel recordingPanel = new JPanel();
        tabbedPane.addTab("Recording", null, recordingPanel, null);
    }
}