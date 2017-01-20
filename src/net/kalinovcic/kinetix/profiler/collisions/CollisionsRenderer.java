package net.kalinovcic.kinetix.profiler.collisions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class CollisionsRenderer
{
    public Collisions profiler;
    
    public CollisionsRenderer(Collisions profiler)
    {
        this.profiler = profiler;
    }
    
    public void render(SimulationState state, double deltaTime)
    {
        BufferedImage buffer = profiler.getBuffer(0);
        Graphics2D g2D = buffer.createGraphics();
        KinetixUI.setHints(g2D);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, profiler.getWidth(), profiler.getHeight());

        int headerHeight = 0;
        int textHeight = g2D.getFontMetrics().getHeight();
        g2D.setColor(Color.BLACK);

        double totalSum = 0.0;
        int totalCount = 0;
        Map<String, Double> sums = new HashMap<String, Double>();
        Map<String, Integer> counts = new HashMap<String, Integer>();
        synchronized(Kinetix.STATE)
        {
            for (Atom atom : Kinetix.STATE.atoms)
            {
                double v = atom.velocity.length();
                double kineticEnergy = 0.5 * (atom.mass * Reaction.DALTON * Reaction.AVOGADRO) * v * v;
                if (!sums.containsKey(atom.type.name))
                {
                    sums.put(atom.type.name, kineticEnergy);
                    counts.put(atom.type.name, 1);
                }
                else
                {
                    sums.put(atom.type.name, sums.get(atom.type.name) + kineticEnergy);
                    counts.put(atom.type.name, counts.get(atom.type.name) + 1);
                }
                totalSum += kineticEnergy;
                totalCount += 1;
            }
        }
        
        for (String name : sums.keySet())
        {
            g2D.drawString(String.format(Locale.US, "%s: %.5f", name, (sums.get(name) / counts.get(name))), 4, headerHeight += textHeight);
        }
        g2D.drawString(String.format(Locale.US, "total: %.5f", (totalSum / totalCount)), 4, headerHeight += textHeight);

        /*
        int reactant1 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant1);
        int reactant2 = Reactions.uniqueAtoms.get(Kinetix.reaction.reactant2);
        int product1 = Reactions.uniqueAtoms.get(Kinetix.reaction.product1);
        int product2 = Reactions.uniqueAtoms.get(Kinetix.reaction.product2);
        
        int collisions = state.collisionInfo[reactant1][reactant2][0];
        int reactions = state.collisionInfo[reactant1][reactant2][1];
        int total = collisions + reactions;
        double ratio = reactions / (double) total;
        g2D.drawString("# red-yellow collisions: " + collisions, 4, headerHeight += textHeight);
        g2D.drawString("# red-yellow reactions: " + reactions, 4, headerHeight += textHeight);
        g2D.drawString("# red-yellow ratio: " + String.format(Locale.US, "%.5f", ratio), 4, headerHeight += textHeight);
        headerHeight += textHeight;

        g2D.drawString("# red-red collisions: " + state.collisionInfo[reactant1][reactant1][0], 4, headerHeight += textHeight);
        g2D.drawString("# red-green collisions: " + state.collisionInfo[reactant1][product1][0], 4, headerHeight += textHeight);
        g2D.drawString("# red-blue collisions: " + state.collisionInfo[reactant1][product2][0], 4, headerHeight += textHeight);
        g2D.drawString("# yellow-yellow collisions: " + state.collisionInfo[reactant2][reactant2][0], 4, headerHeight += textHeight);
        g2D.drawString("# yellow-green collisions: " + state.collisionInfo[reactant2][product1][0], 4, headerHeight += textHeight);
        g2D.drawString("# yellow-blue collisions: " + state.collisionInfo[reactant2][product2][0], 4, headerHeight += textHeight);
        g2D.drawString("# green-green collisions: " + state.collisionInfo[product1][product1][0], 4, headerHeight += textHeight);
        g2D.drawString("# green-blue collisions: " + state.collisionInfo[product1][product2][0], 4, headerHeight += textHeight);
        g2D.drawString("# blue-blue collisions: " + state.collisionInfo[product2][product2][0], 4, headerHeight += textHeight);
        headerHeight += textHeight;
        */
        
        g2D.dispose();

        profiler.swapBuffers();
    }
}
