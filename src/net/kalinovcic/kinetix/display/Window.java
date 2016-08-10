package net.kalinovcic.kinetix.display;

import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;

public class Window extends JFrame
{
	private static final long serialVersionUID = 1L;

	public boolean pause = false;
	
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
}
