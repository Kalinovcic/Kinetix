package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class AtomsOverTimeRenderer
{
    public AtomsOverTime profiler;
    
    public AtomsOverTimeRenderer(AtomsOverTime profiler)
    {
    	this.profiler = profiler;
    }
    
    public void render(SimulationState state, double deltaTime)
    {
        BufferedImage buffer = profiler.getBuffer(0);
        Graphics2D g2D = buffer.createGraphics();
        KinetixUI.setHints(g2D);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, profiler.targetWidth, profiler.targetHeight);

        int headerHeight = 0;
        int textHeight = g2D.getFontMetrics().getHeight();
        g2D.setColor(Color.BLACK);
        g2D.drawString("# atoms: " + state.atoms.size(), 4, headerHeight += textHeight);
        g2D.drawString("Time: " + String.format(Locale.US, "%.2f", state.simulationTime) + "s", 4, headerHeight += textHeight);
        headerHeight += textHeight;
        
        for (int iter = 1; iter < profiler.iteration; iter++)
        {
            double x1 = profiler.targetWidth * ((iter - 1) / (double) (AtomsOverTime.ITERATION_COUNT - 1));
            double x2 = profiler.targetWidth * ((iter - 0) / (double) (AtomsOverTime.ITERATION_COUNT - 1));
            for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
            {
                if (!profiler.exists[type]) continue;
                
            	double py1 = profiler.countOverTime[iter - 1][type] / (double) profiler.maximumAtomCount;
            	double py2 = profiler.countOverTime[iter - 0][type] / (double) profiler.maximumAtomCount;
            	double y1 = profiler.targetHeight - py1 * (profiler.targetHeight - headerHeight);
            	double y2 = profiler.targetHeight - py2 * (profiler.targetHeight - headerHeight);

                g2D.setColor(Atom.getType(state, type).color);
                g2D.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                if (iter == profiler.iteration - 1)
                {
                    String text = profiler.countOverTime[iter][type] + "";
                    int textX = (int) x2 - g2D.getFontMetrics().stringWidth(text);
                    int textY = (int) y2 - 4;
                    g2D.drawString(text, textX, textY);
                }
            }
        }
        
        g2D.dispose();

        profiler.swapBuffers();
    }
}
