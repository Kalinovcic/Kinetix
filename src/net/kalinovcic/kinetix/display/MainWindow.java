package net.kalinovcic.kinetix.display;

import java.awt.Dimension;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	public JDesktopPane desktop;
	
	public MainWindow()
	{
		Dimension size = new Dimension(700, 700);
		setTitle("Kinetix");
		setSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		
		setVisible(true);
		
		desktop = new JDesktopPane();
		setContentPane(desktop);
	}
}
