package net.kalinovcic.kinetix.commander;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.text.NumberFormat;
import javax.swing.JFormattedTextField;

public class CommanderWindow extends JInternalFrame
{
    private static final long serialVersionUID = 1L;

    public JFormattedTextField simulationRedCount;
    public JFormattedTextField simulationRedRadius;
    public JFormattedTextField simulationRedMass;
    public JFormattedTextField simulationGreenCount;
    public JFormattedTextField simulationGreenRadius;
    public JFormattedTextField simulationGreenMass;
    public JFormattedTextField simulationTemperature;
    public JFormattedTextField simulationWidth;
    public JFormattedTextField simulationHeight;
    
    public CommanderWindow(MainWindow mainWindow)
    {
        super("Commander", false, false, false, false);
        
        setSize(new Dimension(400, 250));
        setMinimumSize(new Dimension(400, 250));
        setMaximumSize(new Dimension(400, 250));
        setLocation(640, 10);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));
        
        setVisible(true);
        
        mainWindow.desktop.add(this);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        
        JPanel simulationPanel = new JPanel();
        tabbedPane.addTab("Simulation", null, simulationPanel, null);
        simulationPanel.setLayout(null);
        
        JButton simulationReset = new JButton("Reset");
        simulationReset.setBounds(211, 169, 172, 23);
        simulationPanel.add(simulationReset);
        
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
        
        simulationRedCount = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationRedCount.setText("100");
        simulationRedCount.setBounds(76, 36, 77, 17);
        simulationPanel.add(simulationRedCount);
        
        simulationRedRadius = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationRedRadius.setText("1");
        simulationRedRadius.setBounds(76, 60, 77, 17);
        simulationPanel.add(simulationRedRadius);
        
        simulationRedMass = new JFormattedTextField(NumberFormat.getNumberInstance());
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

        simulationGreenCount = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationGreenCount.setText("100");
        simulationGreenCount.setBounds(267, 36, 77, 17);
        simulationPanel.add(simulationGreenCount);
        
        simulationGreenRadius = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationGreenRadius.setText("2");
        simulationGreenRadius.setBounds(267, 60, 77, 17);
        simulationPanel.add(simulationGreenRadius);
        
        simulationGreenMass = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationGreenMass.setText("20");
        simulationGreenMass.setBounds(267, 85, 77, 17);
        simulationPanel.add(simulationGreenMass);
        
        JLabel lblEnvironmentProperties = new JLabel("Environment properties...");
        lblEnvironmentProperties.setBounds(10, 117, 174, 14);
        simulationPanel.add(lblEnvironmentProperties);
        
        JLabel lblTemperature = new JLabel("Temperature:");
        lblTemperature.setBounds(20, 142, 77, 14);
        simulationPanel.add(lblTemperature);
        
        JLabel lblWidth = new JLabel("Width:");
        lblWidth.setBounds(20, 173, 46, 14);
        simulationPanel.add(lblWidth);
        
        JLabel lblHeight = new JLabel("Height:");
        lblHeight.setBounds(107, 173, 46, 14);
        simulationPanel.add(lblHeight);
        
        simulationTemperature = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationTemperature.setText("400");
        simulationTemperature.setBounds(107, 142, 77, 17);
        simulationPanel.add(simulationTemperature);
        
        simulationWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationWidth.setText("600");
        simulationWidth.setBounds(58, 171, 40, 17);
        simulationPanel.add(simulationWidth);
        
        simulationHeight = new JFormattedTextField(NumberFormat.getNumberInstance());
        simulationHeight.setText("600");
        simulationHeight.setBounds(148, 171, 40, 17);
        simulationPanel.add(simulationHeight);
        
        JPanel profilerPanel = new JPanel();
        tabbedPane.addTab("Profiler", null, profilerPanel, null);
        profilerPanel.setLayout(null);
        
        JButton profilerAtomsOverTime = new JButton("# atoms / time");
        profilerAtomsOverTime.setBounds(10, 11, 183, 23);
        profilerPanel.add(profilerAtomsOverTime);
        
        JButton profilerCollisions = new JButton("# collisions");
        profilerCollisions.setBounds(10, 45, 183, 23);
        profilerPanel.add(profilerCollisions);
        
        JButton profilerAtomsOverVelocity = new JButton("# atoms / velocity");
        profilerAtomsOverVelocity.setBounds(200, 11, 183, 23);
        profilerPanel.add(profilerAtomsOverVelocity);
        
        JPanel recordingPanel = new JPanel();
        tabbedPane.addTab("Recording", null, recordingPanel, null);
    }
}
