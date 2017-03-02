package net.kalinovcic.kinetix.physics.reaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AtomData
{
    public static Map<String, Double> pse = new HashMap<String, Double>();
    public static Map<String, Double> radii = new HashMap<String, Double>();
    
    static
    {
        loadPSE();
        loadRadii();
    }
    
    private static void loadPSE()
    {
        try
        {
            InputStream is = Reactions.class.getResourceAsStream("/net/kalinovcic/kinetix/physics/reaction/PSE.txt");
            InputStreamReader isR = new InputStreamReader(is);
            BufferedReader bR = new BufferedReader(isR);

            pse = new HashMap<String, Double>();
            
            String line;
            while ((line = bR.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0) continue;
                
                String[] tokens = line.split("\\s+");
                if (tokens.length != 2) throw new RuntimeException("Invalid PSE line: " + line);
                
                pse.put(Reactions.toSimpleName(tokens[0]), Double.parseDouble(tokens[1]));
            }
            
            bR.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void loadRadii()
    {
        try
        {
            InputStream is = Reactions.class.getResourceAsStream("/net/kalinovcic/kinetix/physics/reaction/ROVI.txt");
            InputStreamReader isR = new InputStreamReader(is);
            BufferedReader bR = new BufferedReader(isR);
            
            String line;
            while ((line = bR.readLine()) != null)
            {
                line = line.trim();
                if (line.length() == 0) continue;
                
                String[] tokens = line.split("\\s+");
                if (tokens.length != 2) throw new RuntimeException("Invalid ROVI line:" + line);
                
                radii.put(Reactions.toSimpleName(tokens[0]).replaceAll("\\*", ""), Double.parseDouble(tokens[1]));
            }
            
            bR.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    public static double calculateMass(String name)
    {
        name = Reactions.toSimpleName(name);
        
        double mass = 0;
        
        int i = 0;
        while (i < name.length())
        {
            char c = name.charAt(i);
            if (c == '*') { i++; continue; }
            
            double thisMass;
            if (c == '(')
            {
                int beginI = i + 1;
                int cnt = 1;
                while (cnt > 0)
                {
                    if (i + 1 >= name.length())
                        return -1;
                    c = name.charAt(++i);
                    if (c == '(') cnt++;
                    if (c == ')') cnt--;
                }
                
                String sub = name.substring(beginI, i);
                thisMass = calculateMass(sub);
                if (thisMass < 0)
                    return -1;
                i++;
            }
            else if (Character.isUpperCase(c))
            {
                i++;
                String atomName = c + "";
                if (i < name.length() && Character.isLowerCase(c = name.charAt(i)))
                {
                    atomName += c;
                    i++;
                }
                
                Double massObj = pse.get(atomName);
                if (massObj == null)
                    return -1;
                thisMass = massObj;
            }
            else return -1;
            
            int count = 0;
            while (i < name.length() && Character.isDigit(c = name.charAt(i)))
            {
                count = count * 10 + c - '0';
                i++;
            }
            
            thisMass *= Math.max(1, count);
            mass += thisMass;
        }
        
        return mass;
    }
    
    public static double getRadius(String name)
    {
        name = Reactions.toSimpleName(name).replaceAll("\\*", "");
        Double radiusObj = radii.get(name);
        if (radiusObj == null)
            return -1;
        return radiusObj;
    }
}
