package net.kalinovcic.kinetix.profiler.atomsovertime;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.SMALL_FONT;
import static net.kalinovcic.kinetix.imgui.ImguiTheme.TEXT;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class Chart
{
    public static final Color STRUCTURE_COLOR = TEXT;
    public static final Color TIME_FRAME_COLOR = new Color(255, 0, 0, 100);
    public static final Color MAJOR_COLOR = new Color(TEXT.getRed(), TEXT.getGreen(), TEXT.getBlue(), 40);
    public static final Color MINOR_COLOR = new Color(TEXT.getRed(), TEXT.getGreen(), TEXT.getBlue(), 10);
    public static final int GRID_DIV = 5;
    
    public String verLabel;
    public float verX;
    public float verY1;
    public float verY2;
    public float verPixelsPerUnit;
    public float verMarkEvery;
    public float verMaximum;
    
    public String horLabel;
    public float horY;
    public float horX1;
    public float horX2;
    public float horPixelsPerUnit;
    public float horMarkEvery = 2.0f;
    public float horMaximum = 30.0f;

    public boolean major = true;
    public boolean minor = true;

    public static final int RENDER_CONTINUE = 0;
    public static final int RENDER_OVERLAP = 1;
    public static final int RENDER_AVERAGE = 2;
    public static final int RENDER_MODE_COUNT = 3;
    public int renderMode = RENDER_CONTINUE;
    
    public boolean doLeastSquare;
    
    public static class DataSet
    {
        public ArrayList<Line> lines = new ArrayList<Line>();
        public float totalX;
        public float continuationX;
    }
    
    public static class Line
    {
        public int unique;
        public float x1;
        public float x2;
        public float y1;
        public float y2;
    }
    
    public DataSet activeDataSet;
    public ArrayList<DataSet> dataSets = new ArrayList<DataSet>();

    public Graphics2D g;
    public Color uniqueColors[] = new Color[Reactions.ATOM_TYPE_COUNT];
    
    private Line2D.Float line2D = new Line2D.Float();
    
    public void clear()
    {
        activeDataSet = null;
        dataSets.clear();
    }
    
    public void addDataSet()
    {
        float continuationX = 0.0f;
        if (activeDataSet != null)
            continuationX = activeDataSet.continuationX + activeDataSet.totalX;
        
        activeDataSet = new DataSet();
        activeDataSet.continuationX = continuationX;
        dataSets.add(activeDataSet);
    }
    
    public void addLine(int unique, float x1, float x2, float y1, float y2)
    {
        Line line = new Line();
        line.unique = unique;
        line.x1 = x1;
        line.x2 = x2;
        line.y1 = y1;
        line.y2 = y2;
        
        activeDataSet.lines.add(line);
        activeDataSet.totalX = Math.max(activeDataSet.totalX, line.x2);
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
        g.drawString(horLabel, horX2 - metrics.stringWidth(horLabel), horY - height + ascent);
        
        // vertical axis
        
        line2D.x1 = line2D.x2 = verX;
        line2D.y1 = verY1; line2D.y2 = verY2; 
        g.draw(line2D);
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

            String text = String.format(Locale.US, "%.1f", value);
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
                
                float length = dataSet.totalX;
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

    private float previousAverage[] = new float[Reactions.ATOM_TYPE_COUNT];
    private float currentAverageSum[] = new float[Reactions.ATOM_TYPE_COUNT];
    private float currentAverageCount[] = new float[Reactions.ATOM_TYPE_COUNT];
    
    public void renderGraph()
    {
        float maxCommonTime = Float.MAX_VALUE;
        
        float offset = 0.0f;
        for (DataSet dataSet : dataSets)
        {
            for (Line line : dataSet.lines)
            {
                line2D.x1 = horX1 + (offset + line.x1) * horPixelsPerUnit;
                line2D.x2 = horX1 + (offset + line.x2) * horPixelsPerUnit;
                line2D.y1 = verY1 - line.y1 * verPixelsPerUnit;
                line2D.y2 = verY1 - line.y2 * verPixelsPerUnit;

                Color color = uniqueColors[line.unique];
                if (renderMode == RENDER_AVERAGE)
                    g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                else
                    g.setColor(color);
                g.draw(line2D);
                
                if (offset + line.x2 >= horMaximum)
                    break;
            }
            
            if (renderMode == RENDER_CONTINUE)
            {
                if (offset + dataSet.totalX >= horMaximum)
                    break;
                offset += dataSet.totalX;
            }
            else if (renderMode == RENDER_AVERAGE)
            {
                if (dataSet != activeDataSet)
                {
                    maxCommonTime = Math.min(maxCommonTime, dataSet.totalX);
                }
            }
        }
        
        if (renderMode == RENDER_AVERAGE)
        {
            Arrays.fill(previousAverage, -1);
            
            float n, maxX, sumX, sumY, sumXY, sumXX;
            n = maxX = sumX = sumY = sumXY = sumXX = 0;

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
                        if (value < line.x1 || value >= line.x2) continue;
                        
                        currentAverageSum[line.unique] += line.y1;
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
                    
                    sumX += value;
                    sumY += avgCurrent;
                    sumXY += value * avgCurrent;
                    sumXX += value * value;
                    maxX = Math.max(maxX, value);
                    n += 1;

                    g.setColor(uniqueColors[unique]);
                    g.draw(line2D);
                }
                
                previousX = x;
            }
            
            if (doLeastSquare)
            {
                float a = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
                float b = (sumXX * sumY - sumX * sumXY) / (n * sumXX - sumX * sumX);

                line2D.x1 = horX1;
                line2D.x2 = horX1 + maxX * horPixelsPerUnit;
                line2D.y1 = verY1 - (a * ((line2D.x1 - horX1) / horPixelsPerUnit) + b) * verPixelsPerUnit;
                line2D.y2 = verY1 - (a * ((line2D.x2 - horX1) / horPixelsPerUnit) + b) * verPixelsPerUnit;

                g.setColor(Color.WHITE);
                g.draw(line2D);

                FontMetrics metrics = g.getFontMetrics();
                float height = metrics.getHeight();
                float ascent = metrics.getAscent();
                g.drawString(String.format(Locale.US, "k = %.6f, a = %.2f", a, 1.0 / b), horX1 + 5, verY1 - height + ascent);
            }
        }
    }
    
    private static class SampleInfo implements Comparable<SampleInfo>
    {
        public int unique;
        public float y;
        
        @Override public int compareTo(SampleInfo o) { return Float.compare(o.y, y); }
    }

    private int sampleUniqueCount = 0;
    private SampleInfo[] samples = new SampleInfo[Reactions.ATOM_TYPE_COUNT];
    {
        for (int i = 0; i < samples.length; i++)
            samples[i] = new SampleInfo();
    }
    
    private void clearSamples()
    {
        for (int i = 0; i < samples.length; i++)
            samples[i].y = -1;
    }
    
    private void sample(DataSet dataSet, float x)
    {
        sampleUniqueCount = 0;
        for (Line line : dataSet.lines)
            if (line.x1 <= x && x < line.x2)
            {
                if (samples[line.unique].y < 0)
                    sampleUniqueCount++;
                samples[line.unique].unique = line.unique;
                samples[line.unique].y = line.y1;
            }
    }
    
    public void interactive(Point2D.Float mouse)
    {
        if (activeDataSet == null) return;
        
        g.setFont(SMALL_FONT);
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        if (mouse.x >= horX1 && mouse.x <= horX2)
        {
            float x = (mouse.x - horX1) / horPixelsPerUnit;
            
            line2D.x2 = (line2D.x1 = mouse.x);
            line2D.y1 = verY1; line2D.y2 = verY2;
            g.setColor(TIME_FRAME_COLOR);
            g.draw(line2D);

            clearSamples();

            float textY = horY - height + ascent;
            
            for (DataSet dataSet : dataSets)
            {
                if (renderMode != RENDER_CONTINUE)
                    dataSet = activeDataSet;
                
                if (x > dataSet.totalX)
                {
                    if (renderMode != RENDER_CONTINUE)
                        break;
                    
                    if (dataSet != activeDataSet)
                        x -= dataSet.totalX;
                    continue;
                }
                
                sample(dataSet, x);
                break;
            }
            
            Arrays.sort(samples);
            textY -= (sampleUniqueCount + 1) * height;
            g.setColor(STRUCTURE_COLOR);
            g.drawString(String.format(Locale.US, "%.2f s", x), mouse.x + 2, textY += height);
            for (SampleInfo info : samples)
            {
                if (info.y < 0) break;
                
                String text = String.format(Locale.US, "%s: %.2f", Reactions.findName(info.unique), info.y);
                g.setColor(uniqueColors[info.unique]);
                g.drawString(text, mouse.x + 6, textY += height);
            }
        }
    }
}
