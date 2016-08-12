package net.kalinovcic.kinetix.profiler;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public abstract class ProfilerAction
{
	public Profiler profiler;
	
	public void addKey(KeyStroke keyStroke)
	{
		ArrayList<ProfilerAction> actions = profiler.window.actions.get(keyStroke.getKeyCode());
		if (actions == null)
		{
			actions = new ArrayList<ProfilerAction>();
			profiler.window.actions.put(keyStroke.getKeyCode(), actions);

			AbstractAction action = new AbstractAction()
			{
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e)
				{
					ArrayList<ProfilerAction> actions = profiler.window.actions.get(keyStroke.getKeyCode());
					for (ProfilerAction action  : actions)
					{
						if (!action.profiler.isShowing()) continue;
						action.onKey(keyStroke);
					}
				}
			};
			
	        profiler.window.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, action);
	        profiler.window.getActionMap().put(action, action);
		}
		
		actions.add(this);
	}
	
	public abstract void initialize();
	public void onKey(KeyStroke keyStroke) {}
}
