package net.kalinovcic.kinetix.profiler.atomsovertime;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.SMALL_FONT;
import static net.kalinovcic.kinetix.imgui.ImguiTheme.TEXT;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import net.kalinovcic.kinetix.imgui.Imgui;
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
    public float verMinimum;
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
    
    public static class Series
    {
        public ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
        public float totalX;
        public float continuationX;
    }
    
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

    public ArrayList<Series> series = new ArrayList<Series>();
    public Series activeSeries;
    public DataSet activeDataSet;

    public Graphics2D g;
    public Color uniqueColors[] = new Color[Reactions.ATOM_TYPE_COUNT];
    
    private Line2D.Float line2D = new Line2D.Float();

    private final float TOP_PADDING = 0.05f;
    private float maximumY = -Float.MAX_VALUE;
    private float minimumY =  Float.MAX_VALUE;
    private boolean scaleToFit = true;
    public boolean noScalePadding = false;
    
    public void clear()
    {
        activeSeries = null;
        activeDataSet = null;
        series.clear();
        maximumY = -Float.MAX_VALUE;
        minimumY =  Float.MAX_VALUE;
        scaleToFit = true;
        noScalePadding = false;
    }
    
    public void addSeries()
    {
        float continuationX = 0.0f;
        if (activeSeries != null)
            continuationX = activeSeries.continuationX + activeSeries.totalX;
        
        activeSeries = new Series();
        activeSeries.continuationX = continuationX;
        series.add(activeSeries);
    }
    
    public void addDataSet()
    {
        float continuationX = 0.0f;
        if (activeDataSet != null)
            continuationX = activeDataSet.continuationX + activeDataSet.totalX;
        
        activeDataSet = new DataSet();
        activeDataSet.continuationX = continuationX;
        activeSeries.dataSets.add(activeDataSet);
    }
    
    public void addLine(Line line)
    {
        if (line.y1 > maximumY) maximumY = line.y1;
        if (line.y2 > maximumY) maximumY = line.y2;
        if (line.y1 < minimumY) minimumY = line.y1;
        if (line.y2 < minimumY) minimumY = line.y2;
        
        activeDataSet.lines.add(line);
        activeDataSet.totalX = Math.max(activeDataSet.totalX, line.x2);
        activeSeries.totalX = Math.max(activeSeries.totalX, activeDataSet.continuationX + activeDataSet.totalX);
    }
    
    public void addLine(int unique, float x1, float x2, float y1, float y2)
    {
        Line line = new Line();
        line.unique = unique;
        line.x1 = x1;
        line.x2 = x2;
        line.y1 = y1;
        line.y2 = y2;
        
        addLine(line);
    }
    
    public void scaleVerticalAxis()
    {
        if (maximumY == -Float.MAX_VALUE || minimumY == Float.MAX_VALUE)
        {
            verPixelsPerUnit = 1;
            verMarkEvery = 1;
            verMinimum = 0;
            verMaximum = 1;
            return;
        }
        
        if (scaleToFit)
        {
            float verticalRange = maximumY - minimumY;
            float verticalRangeWithPadding = noScalePadding ? verticalRange : (verticalRange / 0.9f);
            float padding = verticalRangeWithPadding - verticalRange;
            
            float minimumAndPadding = minimumY - padding * 0.5f;
            float maximumAndPadding = maximumY + padding * 0.5f;
            
            verMinimum = minimumAndPadding;
            verMaximum = maximumAndPadding;
        }
        
        verPixelsPerUnit = (verY1 - verY2) / (verMaximum - verMinimum) * (1 - TOP_PADDING);
        verMarkEvery = (verMaximum - verMinimum) * 0.1f;
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
        
        for (float value = verMinimum; value <= verMaximum; value += verMarkEvery)
        {
            line2D.x2 = (line2D.x1 = verX) - 2;
            line2D.y2 = (line2D.y1 = verY1 - (value - verMinimum) * verPixelsPerUnit);
            g.draw(line2D);

            String text;
            if (verMarkEvery < 1)
                text = String.format(Locale.US, "%.3f", value);
            else
                text = String.format(Locale.US, "%.1f", value);
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
                    line2D.y2 = (line2D.y1 = verY1 - (gridValue - verMinimum) * verPixelsPerUnit);
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
            for (Series aSeries : series)
            {
                float seriesOffset = offset;
                
                for (DataSet dataSet : aSeries.dataSets)
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
                
                line2D.x2 = (line2D.x1 = horX1 + seriesOffset * horPixelsPerUnit);
                line2D.y1 = verY1; line2D.y2 = verY2;
                Stroke stroke = g.getStroke();
                g.setStroke(new BasicStroke(2.0f));
                g.setColor(Color.YELLOW);
                g.draw(line2D);
                g.setColor(STRUCTURE_COLOR);
                g.setStroke(stroke);
            }
        }
        else
        {
            float offset = 0.0f;
            for (Series aSeries : series)
            {
                float maxTotalX = 0;
                for (DataSet dataSet : aSeries.dataSets)
                    maxTotalX = Math.max(maxTotalX, dataSet.totalX);
                    
                float length = maxTotalX;
                if ((offset + length > horMaximum) || (aSeries == activeSeries))
                    length = horMaximum - offset;

                markHorizontalAxis(offset, length);

                line2D.x2 = (line2D.x1 = horX1 + offset * horPixelsPerUnit);
                line2D.y1 = verY1; line2D.y2 = verY2;
                Stroke stroke = g.getStroke();
                g.setStroke(new BasicStroke(2.0f));
                g.setColor(Color.YELLOW);
                g.draw(line2D);
                g.setColor(STRUCTURE_COLOR);
                g.setStroke(stroke);
                
                offset += length;
            }
        }
    }

    private float previousAverage[] = new float[Reactions.ATOM_TYPE_COUNT];
    private float currentAverageSum[] = new float[Reactions.ATOM_TYPE_COUNT];
    private float currentAverageCount[] = new float[Reactions.ATOM_TYPE_COUNT];
    
    public void renderGraph()
    {   
        float offset = 0.0f;
        for (Series aSeries : series)
        {
            Shape previousClip = g.getClip();

            float clipLeft = horX1 + aSeries.continuationX * horPixelsPerUnit;
            float clipRight = horX1 + (aSeries.continuationX + aSeries.totalX) * horPixelsPerUnit;
            if (clipLeft > horX2) break;
            if (clipRight > horX2) clipRight = horX2;
            
            float clipBottom = verY1;
            float clipTop = clipBottom + (verY2 - verY1) * (1 - TOP_PADDING);
            Shape newClip = new Rectangle2D.Float(clipLeft, clipTop, clipRight - clipLeft, clipBottom - clipTop);
            g.setClip(newClip);
            
            float maxTotalX = 0;
            float maxCommonTime = Float.MAX_VALUE;
            for (DataSet dataSet : aSeries.dataSets)
            {
                for (Line line : dataSet.lines)
                {
                    line2D.x1 = horX1 + (offset + line.x1) * horPixelsPerUnit;
                    line2D.x2 = horX1 + (offset + line.x2) * horPixelsPerUnit;
                    line2D.y1 = verY1 - (line.y1 - verMinimum) * verPixelsPerUnit;
                    line2D.y2 = verY1 - (line.y2 - verMinimum) * verPixelsPerUnit;
    
                    Color color = uniqueColors[line.unique];
                    if (renderMode == RENDER_AVERAGE)
                        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));
                    else
                        g.setColor(color);
                    g.draw(line2D);
                    
                    if (offset + line.x2 >= horMaximum)
                        break;
                }

                maxTotalX = Math.max(maxTotalX, dataSet.totalX);
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
            if (maxCommonTime == Float.MAX_VALUE)
                maxCommonTime = horMaximum - offset;
            
            if (renderMode == RENDER_AVERAGE)
            {
                Arrays.fill(previousAverage, Float.MIN_VALUE);
                
                float n, sumX, sumY, sumXY, sumXX;
                n = sumX = sumY = sumXY = sumXX = 0;
    
                float previousValue = 0;
                for (float value = 0; value <= maxCommonTime; value += 0.1f)
                {
                    Arrays.fill(currentAverageSum, 0);
                    Arrays.fill(currentAverageCount, 0);
                    
                    for (DataSet dataSet : aSeries.dataSets)
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
                        if (avgPrevious == Float.MIN_VALUE) continue;
    
                        line2D.x1 = horX1 + (previousValue + offset) * horPixelsPerUnit;
                        line2D.x2 = horX1 + (value + offset) * horPixelsPerUnit;
                        line2D.y1 = verY1 - (avgPrevious - verMinimum) * verPixelsPerUnit;
                        line2D.y2 = verY1 - (avgCurrent - verMinimum) * verPixelsPerUnit;
                        
                        float value2 = value * 1e-10f;
                        sumX += value2;
                        sumY += avgCurrent;
                        sumXY += value2 * avgCurrent;
                        sumXX += value2 * value2;
                        n += 1;
    
                        g.setColor(uniqueColors[unique]);
                        g.draw(line2D);
                    }
                    
                    previousValue = value;
                }
                
                if (doLeastSquare)
                {
                    // System.out.println(n + " " + sumX + " " + sumXX + " " + sumY + " " + sumXY);
                    float a = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
                    float b = (sumXX * sumY - sumX * sumXY) / (n * sumXX - sumX * sumX);
    
                    line2D.x1 = horX1 + offset * horPixelsPerUnit;
                    line2D.x2 = horX1 + (maxCommonTime + offset) * horPixelsPerUnit;
                    line2D.y1 = verY1 - (b - verMinimum) * verPixelsPerUnit;
                    line2D.y2 = verY1 - (a * maxCommonTime * 1e-10f + b - verMinimum) * verPixelsPerUnit;
    
                    g.setColor(Color.WHITE);
                    g.draw(line2D);
    
                    FontMetrics metrics = g.getFontMetrics();
                    float height = metrics.getHeight();
                    float ascent = metrics.getAscent();
                    g.drawString(String.format(Locale.US, "k = %.3e, a = %.3e", a, 1.0 / b), horX1 + offset * horPixelsPerUnit + 5, verY1 - height + ascent);
                }
            }
            
            g.setClip(previousClip);
            if (renderMode != RENDER_CONTINUE)
                offset += maxTotalX;
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
    
    public void interactive(Imgui imgui)
    {
        if (activeDataSet == null) return;
        
        g.setFont(SMALL_FONT);
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();
        
        Point2D.Float mouse = imgui.mousePoint();
        if (mouse.x >= horX1 && mouse.x <= horX2)
        {
            if (mouse.y >= verY2 && mouse.y <= verY1)
            {
                if (imgui.context.mouseReleased)
                {
                    scaleToFit = true;
                    scaleVerticalAxis();
                }
                
                if (imgui.context.mouseScrollDelta != 0)
                {
                    scaleToFit = false;
                    
                    float verticalRange = verMaximum - verMinimum;
                    float newVerticalRange = verticalRange / (1 + imgui.context.mouseScrollDelta * 0.1f);
                    
                    float centerY = verY1 -  mouse.y;
                    float centerYValue = verMinimum + centerY / verPixelsPerUnit;

                    verMinimum = centerYValue - (centerYValue - verMinimum) * (newVerticalRange / verticalRange);
                    verMaximum = centerYValue + (verMaximum - centerYValue) * (newVerticalRange / verticalRange);
                    
                    scaleVerticalAxis();
                }
            }
        }
        
        if (mouse.x >= horX1 && mouse.x <= horX2)
        {
            float x = (mouse.x - horX1) / horPixelsPerUnit;
            
            line2D.x2 = (line2D.x1 = mouse.x);
            line2D.y1 = verY1; line2D.y2 = verY2;
            g.setColor(TIME_FRAME_COLOR);
            g.draw(line2D);

            clearSamples();

            float textY = horY - height + ascent;
            
            for (Series aSeries : series)
            {
                for (DataSet dataSet : aSeries.dataSets)
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
