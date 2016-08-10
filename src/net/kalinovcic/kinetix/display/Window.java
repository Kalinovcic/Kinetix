package net.kalinovcic.kinetix.display;

import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends JFrame
{
	private static final long serialVersionUID = 1L;

	public Window()
	{
		Dimension size = new Dimension(600, 600);
		setTitle("Kinetix");
		setSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		pack();
		
		setVisible(true);
	}
}
