package net.kalinovcic.kinetix.physics.reaction.chooser;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JRootPane;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.commander.CommanderWindow;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class ReactionChooserWindow extends JInternalFrame
{
	private static final long serialVersionUID = 1L;
	private JTable table;
	
	public ReactionChooserWindow(MainWindow mainWindow)
	{
        super("Reaction chooser", false, true, false, false);

        Dimension size = new Dimension(1300, 600);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLocation(30, 200);
        
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setUI(new KinetixUI(this));
        setFocusable(true);

        mainWindow.desktop.add(this);
        setVisible(true);
        
        JButton selectButton = new JButton("Select");
        getContentPane().add(selectButton, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        ReactionChooserModel model = new ReactionChooserModel();
        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrollPane.setViewportView(table);
        
        selectButton.addActionListener(new ActionListener()
        {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				synchronized (Kinetix.STATE)
				{
					Reaction reaction = Reactions.getReactions().get(model.enabledIndex);
					
					CommanderWindow.simulationRedRadius.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.radius1));
					CommanderWindow.simulationGreenRadius.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.radius2));
					
					CommanderWindow.simulationRedMass.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.mass1));
					CommanderWindow.simulationGreenMass.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.mass2));

					CommanderWindow.simulationActivationEnergy.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.activationEnergy));
					CommanderWindow.simulationTemperature.setText(CommanderWindow.NUMBER_FORMAT.format(reaction.temperature));
					
					dispose();
				}
			}
		});
        
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col2 = table.columnAtPoint(e.getPoint());
                int col = table.convertColumnIndexToModel(col2);
                
                if (col == 10 || col == 20 || col == 21 || col == 22)
                {
                	Reaction.akUnit = (Reaction.akUnit + 1) % Reaction.akUnits.length;
                	
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(10)).setHeaderValue(Reaction.partName(10));
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(20)).setHeaderValue(Reaction.partName(20));
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(21)).setHeaderValue(Reaction.partName(21));
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(22)).setHeaderValue(Reaction.partName(22));
                	
                	table.getTableHeader().repaint();
                	model.fireTableDataChanged();
                }
                
                if (col == 24 || col == 25)
                {
                	Reaction.cUnit = (Reaction.cUnit + 1) % Reaction.cUnits.length;
                	
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(24)).setHeaderValue(Reaction.partName(24));
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(25)).setHeaderValue(Reaction.partName(25));
                	
                	table.getTableHeader().repaint();
                	model.fireTableDataChanged();
                }
                
                if (col == 26 || col == 27)
                {
                	Reaction.vUnit = (Reaction.vUnit + 1) % Reaction.vUnits.length;
                	
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(26)).setHeaderValue(Reaction.partName(26));
                	table.getColumnModel().getColumn(table.convertColumnIndexToView(27)).setHeaderValue(Reaction.partName(27));
                	
                	table.getTableHeader().repaint();
                	model.fireTableDataChanged();
                }
            }
        });
	}
}
