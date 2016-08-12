package net.kalinovcic.kinetix.commander;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

public class CommanderWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();
    public static final NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance();
    
    public JFormattedTextField simulationRedCount;
    public JFormattedTextField simulationRedRadius;
    public JFormattedTextField simulationRedMass;
    public JFormattedTextField simulationGreenCount;
    public JFormattedTextField simulationGreenRadius;
    public JFormattedTextField simulationGreenMass;
    public JFormattedTextField simulationTemperature;
    public JFormattedTextField simulationDrs;
    public JFormattedTextField simulationWidth;
    public JFormattedTextField simulationHeight;
    
    public CommanderWindow(MainWindow mainWindow)
    {
        super("Commander", false, false, false, false);
        
        setSize(new Dimension(400, 250));
        setMinimumSize(new Dimension(400, 250));
        setMaximumSize(new Dimension(400, 250));
        setLocation(10, 10);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));
        
        setVisible(true);
        
        mainWindow.desktop.add(this);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel simulationPanel = new JPanel();
        tabbedPane.addTab("Simulation", null, simulationPanel, null);
        simulationPanel.setLayout(null);
        
        JButton simulationStart = new JButton("Start");
        simulationStart.setBounds(211, 169, 172, 23);
        simulationPanel.add(simulationStart);
        
        JButton openProfiler = new JButton("Open profiler");
        openProfiler.setBounds(211, 140, 172, 23);
        simulationPanel.add(openProfiler);
        
        JLabel lblRedProperties = new JLabel("Red properties...");
        lblRedProperties.setBounds(10, 11, 143, 14);
        simulationPanel.add(lblRedProperties);
        
        JLabel lblRedCount = new JLabel("Count:");
        lblRedCount.setBounds(20, 36, 46, 14);
        simulationPanel.add(lblRedCount);
        
        JLabel lblRedRadius = new JLabel("Radius:");
        lblRedRadius.setBounds(20, 61, 46, 14);
        simulationPanel.add(lblRedRadius);
        
        JLabel lblRedMass = new JLabel("Mass:");
        lblRedMass.setBounds(20, 86, 46, 14);
        simulationPanel.add(lblRedMass);
        
        simulationRedCount = new JFormattedTextField(INTEGER_FORMAT);
        simulationRedCount.setText("100");
        simulationRedCount.setBounds(76, 36, 77, 17);
        simulationPanel.add(simulationRedCount);
        
        simulationRedRadius = new JFormattedTextField(NUMBER_FORMAT);
        simulationRedRadius.setText("1");
        simulationRedRadius.setBounds(76, 60, 77, 17);
        simulationPanel.add(simulationRedRadius);
        
        simulationRedMass = new JFormattedTextField(NUMBER_FORMAT);
        simulationRedMass.setText("10");
        simulationRedMass.setBounds(76, 85, 77, 17);
        simulationPanel.add(simulationRedMass);
        
        JLabel lblGreenProperties = new JLabel("Green properties...");
        lblGreenProperties.setBounds(201, 11, 143, 14);
        simulationPanel.add(lblGreenProperties);
        
        JLabel lblGreenCount = new JLabel("Count:");
        lblGreenCount.setBounds(211, 36, 46, 14);
        simulationPanel.add(lblGreenCount);
        
        JLabel lblGreenRadius = new JLabel("Radius:");
        lblGreenRadius.setBounds(211, 61, 46, 14);
        simulationPanel.add(lblGreenRadius);
        
        JLabel lblGreenMass = new JLabel("Mass:");
        lblGreenMass.setBounds(211, 86, 46, 14);
        simulationPanel.add(lblGreenMass);

        simulationGreenCount = new JFormattedTextField(INTEGER_FORMAT);
        simulationGreenCount.setText("100");
        simulationGreenCount.setBounds(267, 36, 77, 17);
        simulationPanel.add(simulationGreenCount);
        
        simulationGreenRadius = new JFormattedTextField(NUMBER_FORMAT);
        simulationGreenRadius.setText("2");
        simulationGreenRadius.setBounds(267, 60, 77, 17);
        simulationPanel.add(simulationGreenRadius);
        
        simulationGreenMass = new JFormattedTextField(NUMBER_FORMAT);
        simulationGreenMass.setText("20");
        simulationGreenMass.setBounds(267, 85, 77, 17);
        simulationPanel.add(simulationGreenMass);
        
        JLabel lblEnvironmentProperties = new JLabel("Environment properties...");
        lblEnvironmentProperties.setBounds(10, 117, 174, 14);
        simulationPanel.add(lblEnvironmentProperties);
        
        JLabel lblTemperature = new JLabel("Temp:");
        lblTemperature.setBounds(20, 142, 46, 14);
        simulationPanel.add(lblTemperature);
        
        JLabel lblDrs = new JLabel("Drš:");
        lblDrs.setBounds(107, 142, 46, 14);
        simulationPanel.add(lblDrs);
        
        JLabel lblWidth = new JLabel("Width:");
        lblWidth.setBounds(20, 173, 46, 14);
        simulationPanel.add(lblWidth);
        
        JLabel lblHeight = new JLabel("Height:");
        lblHeight.setBounds(107, 173, 46, 14);
        simulationPanel.add(lblHeight);
        
        simulationTemperature = new JFormattedTextField(NUMBER_FORMAT);
        simulationTemperature.setText("400");
        simulationTemperature.setBounds(58, 142, 40, 17);
        simulationPanel.add(simulationTemperature);
        
        simulationDrs = new JFormattedTextField(NUMBER_FORMAT);
        simulationDrs.setText("100");
        simulationDrs.setBounds(148, 142, 40, 17);
        simulationPanel.add(simulationDrs);
        
        simulationWidth = new JFormattedTextField(INTEGER_FORMAT);
        simulationWidth.setText("600");
        simulationWidth.setBounds(58, 171, 40, 17);
        simulationPanel.add(simulationWidth);
        
        simulationHeight = new JFormattedTextField(INTEGER_FORMAT);
        simulationHeight.setText("600");
        simulationHeight.setBounds(148, 171, 40, 17);
        simulationPanel.add(simulationHeight);

        simulationStart.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
				SimulationSettings newSettings = new SimulationSettings();
				
				try
				{
					newSettings.temperature = NUMBER_FORMAT.parse(simulationTemperature.getText()).doubleValue();
					newSettings.drs = NUMBER_FORMAT.parse(simulationDrs.getText()).doubleValue();
					newSettings.width = NUMBER_FORMAT.parse(simulationWidth.getText()).intValue();
					newSettings.height = NUMBER_FORMAT.parse(simulationHeight.getText()).intValue();

					newSettings.redCount = NUMBER_FORMAT.parse(simulationRedCount.getText()).intValue();
					newSettings.redRadius = NUMBER_FORMAT.parse(simulationRedRadius.getText()).doubleValue();
					newSettings.redMass = NUMBER_FORMAT.parse(simulationRedMass.getText()).doubleValue();

					newSettings.greenCount = NUMBER_FORMAT.parse(simulationGreenCount.getText()).intValue();
					newSettings.greenRadius = NUMBER_FORMAT.parse(simulationGreenRadius.getText()).doubleValue();
					newSettings.greenMass = NUMBER_FORMAT.parse(simulationGreenMass.getText()).doubleValue();

					if (newSettings.temperature < 0) throw new Exception();
					if (newSettings.width < 1) throw new Exception();
					if (newSettings.height < 1) throw new Exception();

					if (newSettings.redCount < 0) throw new Exception();
					if (newSettings.redRadius <= 0) throw new Exception();
					if (newSettings.redMass <= 0) throw new Exception();

					if (newSettings.greenCount < 0) throw new Exception();
					if (newSettings.greenRadius <= 0) throw new Exception();
					if (newSettings.greenMass <= 0) throw new Exception();
				}
				catch (Exception ex)
				{
					JOptionPane.showMessageDialog(mainWindow, "Invalid simulation parameters", "Error", JOptionPane.ERROR_MESSAGE);
				}
				
				Kinetix.settings = newSettings;
			}
		});
        
        openProfiler.addActionListener(new ActionListener()
        {
			public void actionPerformed(ActionEvent e)
			{
                new ProfilerWindow(mainWindow);
			}
		});
        
        JPanel recordingPanel = new JPanel();
        tabbedPane.addTab("Recording", null, recordingPanel, null);
    }
}
