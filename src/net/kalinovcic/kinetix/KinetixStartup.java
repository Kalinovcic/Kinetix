package net.kalinovcic.kinetix;

import java.util.Random;

import net.kalinovcic.kinetix.display.Renderer;
import net.kalinovcic.kinetix.display.Window;
import net.kalinovcic.kinetix.math.Vector2;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.State;

public class KinetixStartup
{
	public static void main(String[] args)
	{
		State state = new State();
		Window window = new Window();
		Renderer renderer = new Renderer(window, state);

		Random random = new Random();
		for (int i = 0; i < 1000; i++)
		{
			double radius = 2.0;
			double mass = 20.0;

			double x = random.nextDouble()*(State.SIMULATION_WIDTH - 2*radius) + radius;
			double y = random.nextDouble()*(State.SIMULATION_HEIGHT - 2*radius) + radius;

			double vx = (random.nextDouble() - 0.5) * 400.0;
			double vy = (random.nextDouble() - 0.5) * 400.0;

			Vector2 position = new Vector2(x, y);
			Vector2 velocity = new Vector2(vx, vy);
			
			int type = ((i % 2) == 0) ? Atom.ATOM_RED : Atom.ATOM_GREEN;
			state.addAtom(new Atom(type, position, velocity, radius, mass));
		}
		
		long previousNano = System.nanoTime();
		while (true)
		{
			long frameBeginNano = System.nanoTime();
			
			long currentNano = System.nanoTime();
			long deltaNano = currentNano - previousNano;
			double deltaTime = deltaNano / 1000000000.0;
			previousNano = currentNano;
			
			if (window.pause) deltaTime = 0;
			
			state.update(deltaTime);
			renderer.render(deltaTime);
			
			long frameEndNano = System.nanoTime();
			long deltaFrameNano = frameEndNano - frameBeginNano;
			long targetFrameNano = 1000000000 / 60;
			if (deltaFrameNano < targetFrameNano)
			{
				long remainingFrameNano = targetFrameNano - deltaFrameNano;
				try
				{
					Thread.sleep(remainingFrameNano / 1000000, (int) (remainingFrameNano % 1000000));
				}
				catch (InterruptedException e) {}
			}
		}
	}
}
