package net.kalinovcic.kinetix.profiler.atomsovervelocity;

public class AtomsOverVelocityAction
{
    /*
	@Override
	public void initialize()
	{
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0));
		addKey(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
	}
	
	@Override
	public void onKey(KeyStroke keyStroke)
	{
		AtomsOverVelocity thisProfiler = (AtomsOverVelocity) profiler;

		switch (keyStroke.getKeyCode())
		{
		case KeyEvent.VK_SPACE:
		{
			synchronized (thisProfiler)
			{
				thisProfiler.displayOption = (thisProfiler.displayOption + 1) % AtomsOverVelocity.DISPLAY_COUNT;
			}
		} break;
		case KeyEvent.VK_LEFT:
		{
			synchronized (thisProfiler)
			{
	            if (thisProfiler.columnCount <= 10)
	            	thisProfiler.columnCount = 1;
	            else
	            	thisProfiler.columnCount -= 10;
	            thisProfiler.velocityInterval = thisProfiler.maximumVelocity / thisProfiler.columnCount;
			}
		} break;
		case KeyEvent.VK_RIGHT:
		{
			synchronized (thisProfiler)
			{
	            if (thisProfiler.columnCount < 10)
	            	thisProfiler.columnCount = 10;
	            else
	            	thisProfiler.columnCount += 10;
	            thisProfiler.velocityInterval = thisProfiler.maximumVelocity / thisProfiler.columnCount;
			}
		} break;
		case KeyEvent.VK_DOWN:
		{
			synchronized (thisProfiler)
			{
	            if (thisProfiler.maximumVelocity > 100)
	            	thisProfiler.maximumVelocity -= 100;
	            thisProfiler.velocityInterval = thisProfiler.maximumVelocity / thisProfiler.columnCount;
			}
		} break;
		case KeyEvent.VK_UP:
		{
			synchronized (thisProfiler)
			{
				thisProfiler.maximumVelocity += 100;
	            thisProfiler.velocityInterval = thisProfiler.maximumVelocity / thisProfiler.columnCount;
			}
		} break;
		case KeyEvent.VK_PLUS:
		{
			synchronized (thisProfiler)
			{
				if (thisProfiler.frameInterval < 60)
					thisProfiler.frameInterval = 60;
				else
					thisProfiler.frameInterval += 60;
			}
		} break;
		case KeyEvent.VK_MINUS:
		{
			synchronized (thisProfiler)
			{
				if (thisProfiler.frameInterval <= 60)
					thisProfiler.frameInterval = 1;
				else
					thisProfiler.frameInterval -= 60;
			}
		} break;
		}
	}
	*/
}
