package net.kalinovcic.kinetix.physics.reaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Reactions
{
    public static final int ATOM_TYPE_COUNT;
	public static List<Reaction> reactions;
	public static Map<String, Integer> uniqueAtoms;
	
	static
	{
	    loadReactions();
	    
	    HashSet<String> names = new HashSet<String>();
	    for (Reaction reaction : reactions)
	    {
            names.add(reaction.reactant1);
            names.add(reaction.reactant2);
            names.add(reaction.product1);
            names.add(reaction.product2);
	    }
	    
	    uniqueAtoms = new HashMap<String, Integer>();
	    
	    int id = 0;
	    for (String name : names)
	        uniqueAtoms.put(name, id++);
	    
	    ATOM_TYPE_COUNT = names.size();
	    
        for (Reaction reaction : reactions)
        {
            reaction.reactant1_unique = uniqueAtoms.get(reaction.reactant1);
            reaction.reactant2_unique = uniqueAtoms.get(reaction.reactant2);
            reaction.product1_unique = uniqueAtoms.get(reaction.product1);
            reaction.product2_unique = uniqueAtoms.get(reaction.product2);
        }
	}
	
	public static String simpleName(String name)
	{
	    return name.replaceAll("₂", "2").replaceAll("₃", "3").replaceAll("₄", "4");
	}
    
    public static double findMass(String atom)
    {
        for (Reaction reaction : reactions)
        {
            if (reaction.reactant1.equals(atom)) return reaction.mass1;
            if (reaction.reactant2.equals(atom)) return reaction.mass2;
        }
        return 1.0;
    }
    
    public static double findRadius(String atom)
    {
        for (Reaction reaction : reactions)
        {
            if (reaction.reactant1.equals(atom)) return reaction.radius1;
            if (reaction.reactant2.equals(atom)) return reaction.radius2;
        }
        return 1.0;
    }
	
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
				reaction.product1 = tokens[2].replaceAll("2", "₂").replaceAll("3", "₃").replaceAll("4", "₄");
				reaction.product2 = tokens[3].replaceAll("2", "₂").replaceAll("3", "₃").replaceAll("4", "₄");
				reaction.mass1 = Double.valueOf(tokens[4].replaceAll(",", "."));
				reaction.mass2 = Double.valueOf(tokens[5].replaceAll(",", "."));
				reaction.radius1 = Double.valueOf(tokens[6].replaceAll(",", "."));
				reaction.radius2 = Double.valueOf(tokens[7].replaceAll(",", "."));
				if (!tokens[8].equals("-"))
				{
				    reaction.temperatureRange_known = true;
					reaction.temperatureRange_low = Double.valueOf(tokens[8].split("-")[0].replaceAll(",", "."));
					reaction.temperatureRange_high = Double.valueOf(tokens[8].split("-")[1].replaceAll(",", "."));
				}
				else
				{
				    reaction.temperatureRange_known = false;
				}
				reaction.preExponentialFactor_experimental = Double.valueOf(tokens[9].replaceAll(",", "."));
				reaction.b = Double.valueOf(tokens[10].replaceAll(",", "."));
				reaction.ratio = Double.valueOf(tokens[11].replaceAll(",", "."));
				
				reaction.temperature = reaction.temperatureRange_known ? reaction.temperatureRange_high : 300;
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
}
