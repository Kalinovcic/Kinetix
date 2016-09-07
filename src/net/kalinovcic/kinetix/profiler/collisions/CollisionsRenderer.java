package net.kalinovcic.kinetix.profiler.collisions;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;

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
        
        int collisions = state.collisionInfo[Atom.ATOM_REACTANT1][Atom.ATOM_REACTANT2][0];
        int reactions = state.collisionInfo[Atom.ATOM_REACTANT1][Atom.ATOM_REACTANT2][1];
        int total = collisions + reactions;
        double ratio = reactions / (double) total;
        g2D.drawString("# red-green collisions: " + collisions, 4, headerHeight += textHeight);
        g2D.drawString("# red-green reactions: " + reactions, 4, headerHeight += textHeight);
        g2D.drawString("# red-green ratio: " + String.format(Locale.US, "%.5f", ratio), 4, headerHeight += textHeight);
        headerHeight += textHeight;
        
        g2D.drawString("# red-red collisions: " + state.collisionInfo[Atom.ATOM_REACTANT1][Atom.ATOM_REACTANT1][0], 4, headerHeight += textHeight);
        g2D.drawString("# red-black collisions: " + state.collisionInfo[Atom.ATOM_REACTANT1][Atom.ATOM_PRODUCT2][0], 4, headerHeight += textHeight);
        g2D.drawString("# green-green collisions: " + state.collisionInfo[Atom.ATOM_REACTANT2][Atom.ATOM_REACTANT2][0], 4, headerHeight += textHeight);
        g2D.drawString("# green-black collisions: " + state.collisionInfo[Atom.ATOM_REACTANT2][Atom.ATOM_PRODUCT2][0], 4, headerHeight += textHeight);
        g2D.drawString("# black-black collisions: " + state.collisionInfo[Atom.ATOM_PRODUCT2][Atom.ATOM_PRODUCT2][0], 4, headerHeight += textHeight);
        headerHeight += textHeight;
        
        g2D.dispose();

        profiler.swapBuffers();
    }
}
