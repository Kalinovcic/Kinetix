package net.kalinovcic.kinetix.profile.typecount;

import java.awt.Color;
import java.awt.Graphics2D;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;

public class ProfileTypeCountRenderer
{
    public KinetixWindow window;
    public ProfileTypeCountSettings settings;
    
    public int maximumAtomCount = 0;
    public int[][] countOverTime = null;
    
    public ProfileTypeCountRenderer(KinetixWindow window, ProfileTypeCountSettings settings)
    {
        this.window = window;
        this.settings = settings;
    }
    
    public void update(SimulationState state)
    {
        if (countOverTime == null)
        {
            maximumAtomCount = state.atoms.size();
            countOverTime = new int[ProfileTypeCountSettings.ITERATION_COUNT][Atom.ATOM_TYPE_COUNT];
        }

        if (settings.iteration < ProfileTypeCountSettings.ITERATION_COUNT)
        {
            maximumAtomCount = Math.max(maximumAtomCount, state.atoms.size());
            for (int i = 0; i < Atom.ATOM_TYPE_COUNT; i++) countOverTime[settings.iteration][i] = 0;
            for (Atom atom : state.atoms) countOverTime[settings.iteration][atom.type]++;
            settings.iteration++;
        }
    }
    
    public void render(SimulationState state, double deltaTime)
    {
        update(state);
        
        Graphics2D g2D = window.canvas.createGraphics();
        KinetixUI.setHints(g2D);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, window.getWidth(), window.getHeight());

        g2D.setColor(Color.BLACK);
        g2D.drawString("# atoms: " + state.atoms.size(), 4, g2D.getFontMetrics().getHeight());
        
        for (int i = 1; i < settings.iteration; i++)
        {
            double x1 = window.targetWidth * ((i - 1) / (double) (countOverTime.length - 1));
            double x2 = window.targetWidth * ((i - 0) / (double) (countOverTime.length - 1));
            for (int j = 0; j < Atom.ATOM_TYPE_COUNT; j++)
            {
                double y1 = window.targetHeight * (1.0 - countOverTime[i - 1][j] / (double) maximumAtomCount);
                double y2 = window.targetHeight * (1.0 - countOverTime[i - 0][j] / (double) maximumAtomCount);

                g2D.setColor(Atom.getColor(j));
                g2D.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

                if (i == settings.iteration - 1)
                {
                    String text = countOverTime[i][j] + "";
                    int textX = (int) x2 - g2D.getFontMetrics().stringWidth(text);
                    int textY = (int) y2 - 4;
                    g2D.drawString(text, textX, textY);
                }
            }
        }
        
        g2D.dispose();

        window.revalidate();
        window.repaint();
    }
}
