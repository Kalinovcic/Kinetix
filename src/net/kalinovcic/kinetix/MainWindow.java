package net.kalinovcic.kinetix;

import java.awt.Dimension;

import javax.swing.JFrame;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 1L;

	public KinetixDesktop desktop;
	
	public MainWindow()
	{
		Dimension size = new Dimension(1400, 800);
		setTitle("Kinetix");
		setSize(size);
		setPreferredSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		
		setVisible(true);
		
		desktop = new KinetixDesktop();
		setContentPane(desktop);
	}
}
