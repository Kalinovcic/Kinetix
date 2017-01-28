package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Locale;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.ImguiContext;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerUI;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class AtomsOverTime extends Profiler
{
    public static final float PADDING_LEFT = 30.0f;
    public static final float PADDING_BOTTOM = 15.0f;
    public static final float PADDING_RIGHT = 10.0f;
    public static final float PADDING_TOP = 36.0f;
    
    public static final Color STRUCTURE_COLOR = TEXT;
    
    public AtomsOverTime()
    {
        super("Atoms over time");
    }
    
    private ProfilerUI ui;
    private Graphics2D g;
    private ImguiContext context;
    private SimulationState state;

    private float verX;
    private float verY1;
    private float verY2;
    
    private float verPixelsPerUnit;
    private float verMarkEvery;
    private float verMaximum;
    
    private float horY;
    private float horX1;
    private float horX2;
    
    private float horPixelsPerUnit;
    private float horMarkEvery = 2.0f;
    private float horMaximum = 30.0f;

    @Override
    public void update(ProfilerUI ui)
    {
        this.ui = ui;
        g = ui.g;
        context = ui.context;
        state = Kinetix.STATE;

        if (!state.readyToUse) return;
        if (!state.paused) collectData();
        
        updateInput();
        updateGraph();
        renderGraph();
        renderStructure();
    }
    
    public void updateInput()
    {
        ui.beginRow();
        if (ui.doButton("+ resolution", 0, 18))
        {
            horMaximum *= 2.0f;
            horMarkEvery *= 2.0f;
        }
        if (ui.doButton("- resolution", 0, 18))
        {
            horMaximum /= 2.0f;
            horMarkEvery /= 2.0f;
        }
        if (ui.doButton("+ width", 0, 18)) context.nextFrameWindowWidth += 100;
        if (ui.doButton("- width", 0, 18)) context.nextFrameWindowWidth -= 100;
        ui.endRow();
    }
    
    private static class Line
    {
        public Color color;
        public int unique;
        
        public float time1;
        public float time2;
        public int count1;
        public int count2;
    }
    
    private Object activeInstance = null;
    
    private int maximumCount = 0;
    
    private float previousTime;
    private int[] previousCounts = new int[Reactions.ATOM_TYPE_COUNT];

    private float newTime;
    private int[] newCounts = new int[Reactions.ATOM_TYPE_COUNT];
    
    private ArrayList<Line> lines = new ArrayList<Line>();
    
    public void collectData()
    {
        synchronized (state)
        {
            if (state.instanceID != activeInstance)
            {
                for (int unique = 0; unique < newCounts.length; unique++)
                {
                    previousTime = 0;
                    previousCounts[unique] = 0;
                }
                
                activeInstance = state.instanceID;
            }
            
            for (int unique = 0; unique < newCounts.length; unique++)
                newCounts[unique] = 0;
            
            newTime = (float) state.simulationTime;
            for (Atom atom : state.atoms)
                newCounts[atom.type.unique]++;
            
            for (int unique = 0; unique < newCounts.length; unique++)
            {
                if (state.atomTypes[unique] == null) continue;
                
                Line line = new Line();
                line.color = state.atomTypes[unique].color;
                line.unique = unique;

                line.count1 = previousCounts[unique];
                line.count2 = newCounts[unique];
                line.time1 = previousTime;
                line.time2 = newTime;
                
                lines.add(line);

                if (newCounts[unique] > maximumCount)
                    maximumCount = newCounts[unique];
                
                previousCounts[unique] = newCounts[unique];
            }
            previousTime = newTime;
        }
    }

    public void renderGraph()
    {
        Line2D.Float line2D = new Line2D.Float();
        for (Line line : lines)
        {
            line2D.x1 = horX1 + line.time1 * horPixelsPerUnit;
            line2D.x2 = horX1 + line.time2 * horPixelsPerUnit;
            line2D.y1 = verY1 - line.count1 * verPixelsPerUnit;
            line2D.y2 = verY1 - line.count2 * verPixelsPerUnit;
            
            g.setColor(line.color);
            g.draw(line2D);
            
            if (line.time2 >= horMaximum)
                break;
        }
    }
    
    public void updateGraph()
    {
        verX = PADDING_LEFT;
        verY1 = context.bounds.height - PADDING_BOTTOM;
        verY2 = PADDING_TOP;
        
        verPixelsPerUnit = (verY1 - verY2) * 0.9f / maximumCount;
        verMarkEvery = 40.0f;
        verMaximum = maximumCount;
        
        horY = context.bounds.height - PADDING_BOTTOM;
        horX1 = PADDING_LEFT;
        horX2 = context.bounds.width - PADDING_RIGHT;

        horPixelsPerUnit = (horX2 - horX1) * 0.9f / horMaximum;
    }
    
    public void renderStructure()
    {
        g.setColor(STRUCTURE_COLOR);
        g.setFont(SMALL_FONT);
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        // horizontal axis
        
        g.draw(new Line2D.Float(horX1, horY, horX2, horY));
        for (float value = horMarkEvery; value <= horMaximum; value += horMarkEvery)
        {
            float x = horX1 + value * horPixelsPerUnit;
            float y = horY;
            g.draw(new Line2D.Float(x, y, x, y + 2));
            
            String text = String.format(Locale.US, "%.0f", value);
            g.drawString(text, x - metrics.stringWidth(text) * 0.5f, y + ascent + 4);
        }
        
        final String horLabel = "t [s]";
        g.drawString(horLabel, horX2 - metrics.stringWidth(horLabel), horY - height + ascent);
        
        // vertical axis
        
        g.draw(new Line2D.Float(verX, verY1, verX, verY2));
        for (float value = verMarkEvery; value <= verMaximum; value += verMarkEvery)
        {
            float x = verX;
            float y = verY1 - value * verPixelsPerUnit;
            g.draw(new Line2D.Float(x, y, x - 2, y));

            String text = String.format(Locale.US, "%.0f", value);
            g.drawString(text, x - metrics.stringWidth(text) - 5, y - height * 0.5f + ascent);
        }
        
        final String verLabel = "atom [1]";
        g.drawString(verLabel, verX - metrics.stringWidth(verLabel) * 0.5f, verY2 - 2);
    }
}
