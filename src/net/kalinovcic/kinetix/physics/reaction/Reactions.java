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
    
	private static Map<String, Integer> uniqueAtoms = new HashMap<String, Integer>();
    public static List<Reaction> reactions = new ArrayList<Reaction>();
    
	static
	{
	    loadReactions();
	    
	    HashSet<String> names = new HashSet<String>();
	    for (Reaction reaction : reactions)
	    {
            names.add(toSimpleName(reaction.reactant1).replaceAll("\\*", ""));
            if (reaction.reactant2 != null)
                names.add(toSimpleName(reaction.reactant2).replaceAll("\\*", ""));
            names.add(toSimpleName(reaction.product1).replaceAll("\\*", ""));
            if (reaction.product2 != null)
                names.add(toSimpleName(reaction.product2).replaceAll("\\*", ""));
	    }
	    
	    int id = 0;
	    for (String name : names)
	        uniqueAtoms.put(name, id++);
	    
	    ATOM_TYPE_COUNT = names.size();
	    
        for (Reaction reaction : reactions)
        {
            reaction.reactant1_unique = findUnique(reaction.reactant1);
            if (reaction.reactant2 != null)
                reaction.reactant2_unique = findUnique(reaction.reactant2);
            reaction.product1_unique = findUnique(reaction.product1);
            if (reaction.product2 != null)
                reaction.product2_unique = findUnique(reaction.product2);
        }
	}
    
    private static void loadReactions()
    {
        try
        {
            InputStream is = Reactions.class.getResourceAsStream("/net/kalinovcic/kinetix/physics/reaction/TABA.txt");
            InputStreamReader isR = new InputStreamReader(is);
            BufferedReader bR = new BufferedReader(isR);

            boolean first = true;
            String line;
            while ((line = bR.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0) continue;
                if (first) { first = false; continue; }
                
                String[] tokens = line.split("\\s+");
                if (tokens.length != 9) throw new RuntimeException("Invalid TABA line: " + line);
                
                Reaction reaction = new Reaction();
                reaction.reactant1 = toPrettyName(tokens[0].equals("*") ? null : tokens[0]);
                reaction.reactant2 = toPrettyName(tokens[1].equals("*") ? null : tokens[1]);
                reaction.product1 = toPrettyName(tokens[2].equals("*") ? null : tokens[2]);
                reaction.product2 = toPrettyName(tokens[3].equals("*") ? null : tokens[3]);

                if (reaction.reactant1 == null) { System.err.println("missing A? " + line); continue; }
                if (reaction.product1 == null)  { System.err.println("missing C? " + line); continue; }

                if (reaction.reactant2 != null)
                {
                    if ((reaction.mass2 = AtomData.calculateMass(reaction.reactant2)) < 0) { System.err.println("mass B? " + line); continue; }
                    if ((reaction.radius2 = AtomData.getRadius(reaction.reactant2)) < 0) { System.err.println("radius B? " + line); continue; }
                }
                if (reaction.product2 != null)
                {
                    if (AtomData.calculateMass(reaction.product2) < 0) { System.err.println("mass D? " + line); continue; }
                    if (AtomData.getRadius(reaction.product2) < 0) { System.err.println("radius D? " + line); continue; }                    
                }

                if ((reaction.mass1 = AtomData.calculateMass(reaction.reactant1)) < 0) { System.err.println("mass A? " + line); continue; }
                if (AtomData.calculateMass(reaction.product1) < 0) { System.err.println("mass C? " + line); continue; }

                if ((reaction.radius1 = AtomData.getRadius(reaction.reactant1)) < 0) { System.err.println("radius A? " + line); continue; }
                if (AtomData.getRadius(reaction.product1) < 0) { System.err.println("radius C? " + line); continue; }
                
                String[] tTokens = tokens[4].split("-");
                if (tTokens.length != 2) throw new RuntimeException("Invalid TABA line: " + line);
                reaction.t_low = Double.parseDouble(tTokens[0]);
                reaction.t_high = Double.parseDouble(tTokens[1]);

                reaction.A_exp = Double.parseDouble(tokens[5]);
                reaction.n = Double.parseDouble(tokens[6]);
                reaction.Ea = Double.parseDouble(tokens[7]) / 1000.0;
                reaction.red = Integer.parseInt(tokens[8]);
                
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
    
    public static String toPrettyName(String name)
    {
        if (name == null) return null;
        String[] subscripts = new String[] { "₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉" };
        for (int i = 0; i <= 9; i++)
            name = name.replaceAll(i + "", subscripts[i]);
        return name.replaceAll("\\*", "•");
    }
    
    public static String toSimpleName(String name)
    {
        if (name == null) return null;
        String[] subscripts = new String[] { "₀", "₁", "₂", "₃", "₄", "₅", "₆", "₇", "₈", "₉" };
        for (int i = 0; i <= 9; i++)
            name = name.replaceAll(subscripts[i], i + "");
        return name.replaceAll("•", "\\*");
    }
    
    public static int findUnique(String name)
    {
        return uniqueAtoms.get(toSimpleName(name).replaceAll("\\*", ""));
    }
    
    public static String findName(int unique)
    {
        for (Map.Entry<String, Integer> entry : uniqueAtoms.entrySet())
            if (entry.getValue() == unique)
                return entry.getKey();
        return null;
    }
}
