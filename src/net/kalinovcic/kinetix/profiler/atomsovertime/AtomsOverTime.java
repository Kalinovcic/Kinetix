package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
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
    public static final Color TIME_FRAME_COLOR = new Color(255, 0, 0, 100);
    public static final Color MAJOR_COLOR = new Color(TEXT.getRed(), TEXT.getGreen(), TEXT.getBlue(), 40);
    public static final Color MINOR_COLOR = new Color(TEXT.getRed(), TEXT.getGreen(), TEXT.getBlue(), 10);
    public static final int GRID_DIV = 5;
    
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

    private boolean major = true;
    private boolean minor = true;

    private static final int RENDER_CONTINUE = 0;
    private static final int RENDER_OVERLAP = 1;
    private static final int RENDER_AVERAGE = 2;
    private static final int RENDER_MODE_COUNT = 3;
    private int renderMode = RENDER_CONTINUE;

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
        
        interactive();
    }
    
    public void updateInput()
    {
        ui.beginRow();
        if (ui.doButton("clear", 0, 18))
        {
            maximumCount = 0;
            activeInstance = null;
            activeDataSet = null;
            dataSets.clear();
        }
        if (ui.doButton("+ R", 0, 18))
        {
            horMaximum *= 2.0f;
            horMarkEvery *= 2.0f;
        }
        if (ui.doButton("- R", 0, 18))
        {
            horMaximum /= 2.0f;
            horMarkEvery /= 2.0f;
        }
        if (ui.doButton("+ W", 0, 18)) context.nextFrameWidth += 50;
        if (ui.doButton("- W", 0, 18)) context.nextFrameWidth -= 50;
        if (ui.doButton("+ H", 0, 18)) context.nextFrameHeight += 50;
        if (ui.doButton("- H", 0, 18)) context.nextFrameHeight -= 50;

        major = ui.doCheckbox("major", 0, 18, major);
        minor = ui.doCheckbox("minor", 0, 18, minor);
        
        String renderModeText = null;
        switch (renderMode)
        {
        case RENDER_CONTINUE: renderModeText = "continue"; break;
        case RENDER_OVERLAP: renderModeText = "overlap"; break;
        case RENDER_AVERAGE: renderModeText = "average"; break;
        }
        if (ui.doButton(renderModeText, 0, 18))
            renderMode = (renderMode + 1) % RENDER_MODE_COUNT;
        
        ui.endRow();
    }
    
    private static class DataSet
    {
        public ArrayList<Line> lines = new ArrayList<Line>();
        public float totalTime;
        public float continuationTime;
    }
    
    private static class Line
    {
        public int unique;
        public float time1;
        public float time2;
        public int count1;
        public int count2;
    }
    
    private Object activeInstance = null;
    private DataSet activeDataSet;
    private ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
    
    private int maximumCount = 0;
    
    private float previousTime;
    private int[] previousCounts = new int[Reactions.ATOM_TYPE_COUNT];

    private float newTime;
    private int[] newCounts = new int[Reactions.ATOM_TYPE_COUNT];
    
    private Color colors[] = new Color[Reactions.ATOM_TYPE_COUNT];
    
    public void collectData()
    {
        synchronized (state)
        {
            if (state.instanceID != activeInstance)
            {
                float continuationTime = 0.0f;
                if (activeDataSet != null)
                    continuationTime = activeDataSet.continuationTime + activeDataSet.totalTime;
                
                previousTime = 0;
                for (int unique = 0; unique < newCounts.length; unique++)
                {
                    if (state.atomTypes[unique] == null) continue;
                    previousCounts[unique] = state.atomTypes[unique].initialCount;
                    colors[unique] = state.atomTypes[unique].color;
                }
                
                activeInstance = state.instanceID;
                activeDataSet = new DataSet();
                activeDataSet.continuationTime = continuationTime;
                dataSets.add(activeDataSet);
            }
            
            Arrays.fill(newCounts, 0);
            
            newTime = (float) state.simulationTime;
            for (Atom atom : state.atoms)
                newCounts[atom.type.unique]++;
            
            for (int unique = 0; unique < newCounts.length; unique++)
            {
                if (state.atomTypes[unique] == null) continue;
                
                Line line = new Line();
                line.unique = unique;
                line.count1 = previousCounts[unique];
                line.count2 = newCounts[unique];
                line.time1 = previousTime;
                line.time2 = newTime;
                
                activeDataSet.lines.add(line);

                if (newCounts[unique] > maximumCount)
                    maximumCount = newCounts[unique];
                
                previousCounts[unique] = newCounts[unique];
            }
            previousTime = newTime;
            activeDataSet.totalTime = previousTime;
        }
    }
    
    private Line2D.Float line2D = new Line2D.Float();
    private float previousAverage[] = new float[Reactions.ATOM_TYPE_COUNT];
    private int currentAverageSum[] = new int[Reactions.ATOM_TYPE_COUNT];
    private int currentAverageCount[] = new int[Reactions.ATOM_TYPE_COUNT];

    public void renderGraph()
    {
        float maxCommonTime = Float.MAX_VALUE;
        
        float offset = 0.0f;
        for (DataSet dataSet : dataSets)
        {
            for (Line line : dataSet.lines)
            {
                line2D.x1 = horX1 + (offset + line.time1) * horPixelsPerUnit;
                line2D.x2 = horX1 + (offset + line.time2) * horPixelsPerUnit;
                line2D.y1 = verY1 - line.count1 * verPixelsPerUnit;
                line2D.y2 = verY1 - line.count2 * verPixelsPerUnit;

                Color color = colors[line.unique];
                if (renderMode == RENDER_AVERAGE)
                    g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                else
                    g.setColor(color);
                g.draw(line2D);
                
                if (offset + line.time2 >= horMaximum)
                    break;
            }
            
            if (renderMode == RENDER_CONTINUE)
            {
                if (offset + dataSet.totalTime >= horMaximum)
                    break;
                offset += dataSet.totalTime;
            }
            else if (renderMode == RENDER_AVERAGE)
            {
                if (dataSet != activeDataSet)
                {
                    maxCommonTime = Math.min(maxCommonTime, dataSet.totalTime);
                }
            }
        }
        
        if (renderMode == RENDER_AVERAGE)
        {
            Arrays.fill(previousAverage, -1);

            float previousX = horX1;
            for (float x = horX1; x <= horX2; x += 10.0f)
            {
                float value = (x - horX1) / horPixelsPerUnit;
                if (value >= maxCommonTime)
                    break;

                Arrays.fill(currentAverageSum, 0);
                Arrays.fill(currentAverageCount, 0);
                
                for (DataSet dataSet : dataSets)
                    for (Line line : dataSet.lines)
                    {
                        if (value < line.time1 || value >= line.time2) continue;
                        currentAverageSum[line.unique] += line.count1;
                        currentAverageCount[line.unique]++;
                    }
                
                for (int unique = 0; unique < currentAverageSum.length; unique++)
                {
                    if (currentAverageCount[unique] == 0) continue;
                    
                    float avgCurrent = currentAverageSum[unique] / (float) currentAverageCount[unique];
                    float avgPrevious = previousAverage[unique];
                    
                    previousAverage[unique] = avgCurrent;
                    if (avgPrevious < 0) continue;

                    line2D.x1 = previousX;
                    line2D.x2 = x;
                    line2D.y1 = verY1 - avgPrevious * verPixelsPerUnit;
                    line2D.y2 = verY1 - avgCurrent * verPixelsPerUnit;

                    g.setColor(colors[unique]);
                    g.draw(line2D);
                }
                
                previousX = x;
            }
        }
    }
    
    public void updateGraph()
    {
        verX = PADDING_LEFT;
        verY1 = context.bounds.height - PADDING_BOTTOM;
        verY2 = PADDING_TOP;
        
        verPixelsPerUnit = (verY1 - verY2) * 0.9f / maximumCount;
        verMarkEvery = 20.0f;
        verMaximum = maximumCount;
        
        horY = context.bounds.height - PADDING_BOTTOM;
        horX1 = PADDING_LEFT;
        horX2 = context.bounds.width - PADDING_RIGHT;

        horPixelsPerUnit = (horX2 - horX1) / horMaximum;
    }
    
    private void renderAxes()
    {
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        // horizontal axis
        
        line2D.x1 = horX1; line2D.x2 = horX2;
        line2D.y1 = line2D.y2 = horY; 
        g.draw(line2D);
        
        final String horLabel = "t [s]";
        g.drawString(horLabel, horX2 - metrics.stringWidth(horLabel), horY - height + ascent);
        
        // vertical axis
        
        line2D.x1 = line2D.x2 = verX;
        line2D.y1 = verY1; line2D.y2 = verY2; 
        g.draw(line2D);
        
        final String verLabel = "atom [1]";
        g.drawString(verLabel, verX - metrics.stringWidth(verLabel) * 0.5f, verY2 - 2);
    }
    
    private void markVerticalAxis()
    {
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        for (float value = verMarkEvery; value <= verMaximum; value += verMarkEvery)
        {
            line2D.x2 = (line2D.x1 = verX) - 2;
            line2D.y2 = (line2D.y1 = verY1 - value * verPixelsPerUnit);
            g.draw(line2D);

            String text = String.format(Locale.US, "%.0f", value);
            g.drawString(text, line2D.x1 - metrics.stringWidth(text) - 5, line2D.y1 - height * 0.5f + ascent);
            
            if (major || minor)
            {
                line2D.x2 = horX2;
                for (int i = 0; i < GRID_DIV; i++)
                {
                    if (i == 0)
                    {
                        if (!major) continue;
                        g.setColor(MAJOR_COLOR);
                    }
                    else
                    {
                        if (!minor) continue;
                        g.setColor(MINOR_COLOR);
                    }
                    
                    float gridValue = value - (i / (float) GRID_DIV) * verMarkEvery;
                    line2D.y2 = (line2D.y1 = verY1 - gridValue * verPixelsPerUnit);
                    g.draw(line2D);
                    g.setColor(STRUCTURE_COLOR);
                }
            }
        }
    }
    
    private void markHorizontalAxis(float offset, float maximum)
    {
        FontMetrics metrics = g.getFontMetrics();
        float ascent = metrics.getAscent();

        line2D.x2 = (line2D.x1 = horX1 + offset * horPixelsPerUnit);
        line2D.y1 = verY1; line2D.y2 = verY2; 
        g.draw(line2D);
        
        float minorValue = horMarkEvery / GRID_DIV;
        int minorCount = (int) Math.ceil(maximum / minorValue);
        for (int index = 0; index < minorCount; index++)
        {
            boolean majorValue = (index % GRID_DIV) == 0;
            float value = index * minorValue;

            line2D.x2 = (line2D.x1 = horX1 + (offset + value) * horPixelsPerUnit);
            line2D.y2 = (line2D.y1 = horY) + 2;
            
            if (majorValue)
            {
                g.draw(line2D);
                
                String text = String.format(Locale.US, "%.0f", value);
                g.drawString(text, line2D.x1 - metrics.stringWidth(text) * 0.5f, line2D.y1 + ascent + 4);
            }
            
            if (minor || major)
            {
                if (minor) g.setColor(MINOR_COLOR);
                if (major && majorValue) g.setColor(MAJOR_COLOR);
    
                line2D.y2 = verY2;
                g.draw(line2D);
                g.setColor(STRUCTURE_COLOR);
            }
        }
    }
    
    public void renderStructure()
    {
        g.setColor(STRUCTURE_COLOR);
        g.setFont(SMALL_FONT);
        
        renderAxes();
        markVerticalAxis();

        if (renderMode == RENDER_CONTINUE)
        {
            float offset = 0.0f;
            for (DataSet dataSet : dataSets)
            {
                boolean end = false;
                
                float length = dataSet.totalTime;
                if ((offset + length > horMaximum) || (dataSet == activeDataSet))
                {
                    length = horMaximum - offset;
                    end = true;
                }
                
                markHorizontalAxis(offset, length);
                offset += length;
                
                if (end) break;
            }
        }
        else
        {
            markHorizontalAxis(0, horMaximum);
        }
    }
    
    private static class InteractiveInfo implements Comparable<InteractiveInfo>
    {
        public int unique;
        public int count;
        
        @Override public int compareTo(InteractiveInfo o) { return o.count - count; }
    }

    private InteractiveInfo[] interactive = new InteractiveInfo[Reactions.ATOM_TYPE_COUNT];
    {
        for (int i = 0; i < interactive.length; i++)
            interactive[i] = new InteractiveInfo();
    }
    
    public void interactive()
    {
        if (activeDataSet == null) return;
        
        g.setFont(SMALL_FONT);
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        Point2D.Float mouse = ui.mousePoint();
        if (mouse.x >= horX1 && mouse.x <= horX2)
        {
            float time = (mouse.x - horX1) / horPixelsPerUnit;
            
            line2D.x2 = (line2D.x1 = mouse.x);
            line2D.y1 = verY1; line2D.y2 = verY2;
            g.setColor(TIME_FRAME_COLOR);
            g.draw(line2D);

            for (int i = 0; i < interactive.length; i++)
                interactive[i].count = -1;

            float textY = horY - height + ascent;
            
            for (DataSet dataSet : dataSets)
            {
                if (renderMode != RENDER_CONTINUE)
                    dataSet = activeDataSet;
                
                if (time > dataSet.totalTime)
                {
                    if (renderMode != RENDER_CONTINUE)
                        break;
                    
                    if (dataSet != activeDataSet)
                        time -= dataSet.totalTime;
                    continue;
                }
                
                for (Line line : dataSet.lines)
                    if (line.time1 <= time && time < line.time2)
                    {
                        if (interactive[line.unique].count == -1)
                            textY -= height;
                        interactive[line.unique].unique = line.unique;
                        interactive[line.unique].count = line.count1;
                    }
                break;
            }
            
            Arrays.sort(interactive);

            textY -= height;
            g.setColor(STRUCTURE_COLOR);
            g.drawString(String.format(Locale.US, "%.2f s", time), mouse.x + 2, textY += height);
            for (InteractiveInfo info : interactive)
            {
                if (info.count == -1) break;
                
                String text = String.format(Locale.US, "%s: %d", Reactions.findName(info.unique), info.count);
                g.setColor(colors[info.unique]);
                g.drawString(text, mouse.x + 6, textY += height);
            }
        }
    }
}
