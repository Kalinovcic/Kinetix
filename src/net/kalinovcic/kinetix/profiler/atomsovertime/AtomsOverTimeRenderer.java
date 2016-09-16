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
        
        for (int i = 1; i < profiler.iteration; i++)
        {
            double x1 = profiler.targetWidth * ((i - 1) / (double) (AtomsOverTime.ITERATION_COUNT - 1));
            double x2 = profiler.targetWidth * ((i - 0) / (double) (AtomsOverTime.ITERATION_COUNT - 1));
            for (int j = 0; j < Reactions.ATOM_TYPE_COUNT; j++)
            {
            	double py1 = profiler.countOverTime[i - 1][j] / (double) profiler.maximumAtomCount;
            	double py2 = profiler.countOverTime[i - 0][j] / (double) profiler.maximumAtomCount;
            	double y1 = profiler.targetHeight - py1 * (profiler.targetHeight - headerHeight);
            	double y2 = profiler.targetHeight - py2 * (profiler.targetHeight - headerHeight);

                g2D.setColor(Atom.getType(state, j).color);
                g2D.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                if (i == profiler.iteration - 1)
                {
                    String text = profiler.countOverTime[i][j] + "";
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
