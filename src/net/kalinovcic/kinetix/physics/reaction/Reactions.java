package net.kalinovcic.kinetix.physics.reaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Reactions
{
	private static boolean loaded = false;
	private static List<Reaction> reactions;
	
	private static void loadReactions()
	{
		reactions = new ArrayList<Reaction>();
		
		try
		{
			InputStream is = Reactions.class.getResourceAsStream("/net/kalinovcic/kinetix/physics/reaction/reactions.txt");
			InputStreamReader isR = new InputStreamReader(is);
			BufferedReader bR = new BufferedReader(isR);
			
			// skip 3 lines
			bR.readLine();
			bR.readLine();
			bR.readLine();
			
			String line;
			while ((line = bR.readLine()) != null)
			{
				line = line.trim();
				if (line.length() == 0) continue;
				
				String[] tokens = line.split("\\s+");
				if (tokens.length != 12) throw new RuntimeException("Invalid line");
				
				Reaction reaction = new Reaction();
				
				reaction.reactant1 = tokens[0].replaceAll("2", "₂").replaceAll("3", "₃").replaceAll("4", "₄");
				reaction.reactant2 = tokens[1].replaceAll("2", "₂").replaceAll("3", "₃").replaceAll("4", "₄");
				reaction.product1 = tokens[2];
				reaction.product2 = tokens[3];
				reaction.mass1 = Double.valueOf(tokens[4].replaceAll(",", "."));
				reaction.mass2 = Double.valueOf(tokens[5].replaceAll(",", "."));
				reaction.radius1 = Double.valueOf(tokens[6].replaceAll(",", "."));
				reaction.radius2 = Double.valueOf(tokens[7].replaceAll(",", "."));
				if (!tokens[8].equals("-"))
				{
					reaction.temperatureRange_low = Double.valueOf(tokens[8].split("-")[0].replaceAll(",", "."));
					reaction.temperatureRange_high = Double.valueOf(tokens[8].split("-")[1].replaceAll(",", "."));
				}
				reaction.preExponentialFactor_experimental = Double.valueOf(tokens[9].replaceAll(",", "."));
				reaction.b = Double.valueOf(tokens[10].replaceAll(",", "."));
				reaction.ratio = Double.valueOf(tokens[11].replaceAll(",", "."));
				
				reaction.temperature = 1000;
				reaction.concentration1 = 0.02;
				reaction.concentration2 = 0.02;
				reaction.recalculate();
				
				reactions.add(reaction);
			}
			
			bR.close();
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public static synchronized List<Reaction> getReactions()
	{
		if (!loaded)
		{
			loadReactions();
			loaded = true;
		}
		
		return reactions;
	}
}
