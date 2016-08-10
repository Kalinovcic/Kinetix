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
		for (int i = 0; i < 100; i++)
		{
			double radius = 10.0;
			double mass = 20.0;

			double x = random.nextDouble()*(State.SIMULATION_WIDTH - 2*radius) + radius;
			double y = random.nextDouble()*(State.SIMULATION_HEIGHT - 2*radius) + radius;

			double vx = (random.nextDouble() - 0.5) * 600.0;
			double vy = (random.nextDouble() - 0.5) * 600.0;

			Vector2 position = new Vector2(x, y);
			Vector2 velocity = new Vector2(vx, vy);
			
			int type = random.nextBoolean() ? Atom.ATOM_RED : Atom.ATOM_GREEN;
			state.addAtom(new Atom(type, position, velocity, radius, mass));
		}
		
		long previousNano = System.nanoTime();
		while (true)
		{
			long currentNano = System.nanoTime();
			long deltaNano = currentNano - previousNano;
			double deltaTime = deltaNano / 1000000000.0;
			previousNano = currentNano;
			
			state.update(deltaTime);

			renderer.render();
		}
	}
}
