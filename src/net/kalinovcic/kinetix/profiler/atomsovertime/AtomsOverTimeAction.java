package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import net.kalinovcic.kinetix.profiler.ProfilerAction;

public class AtomsOverTimeAction extends ProfilerAction
{
	@Override
	public void initialize()
	{
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
	}
	
	@Override
	public void onKey(KeyStroke keyStroke)
	{
		AtomsOverTime thisProfiler = (AtomsOverTime) profiler;
		
		switch (keyStroke.getKeyCode())
		{
		case KeyEvent.VK_SPACE:
		{
			synchronized (thisProfiler)
			{
				thisProfiler.iteration = 0;
				thisProfiler.maximumAtomCount = 0;
			}
		} break;
		}
	}
}