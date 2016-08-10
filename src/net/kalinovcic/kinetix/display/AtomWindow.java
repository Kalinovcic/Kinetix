package net.kalinovcic.kinetix.display;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JInternalFrame;

public class AtomWindow extends JInternalFrame
{
	private static final long serialVersionUID = 1L;

	public MainWindow mainWindow;
	public boolean pause = false;
	
	public AtomWindow(MainWindow mainWindow)
	{
		super("Simulation", false, false, false, false);
		
		this.mainWindow = mainWindow;
		
		Dimension size = new Dimension(600, 600);
		setSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setLocation(10, 10);
		
		setVisible(true);
		mainWindow.desktop.add(this);
		
		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					pause = !pause;
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		
	}
}
