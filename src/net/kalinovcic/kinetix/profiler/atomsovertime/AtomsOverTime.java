package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.ImguiContext;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.SimulationUpdateListener;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerUI;
import net.kalinovcic.kinetix.profiler.atomsovertime.Chart.DataSet;
import net.kalinovcic.kinetix.profiler.atomsovertime.Chart.Line;
import net.kalinovcic.kinetix.profiler.atomsovertime.Chart.Series;

public class AtomsOverTime extends Profiler implements SimulationUpdateListener
{
    private static final float PADDING_LEFT = 45.0f;
    private static final float PADDING_BOTTOM = 15.0f;
    private static final float PADDING_RIGHT = 10.0f;
    private static final float PADDING_TOP = 36.0f;
    private static final float PADDING_MIDDLE = 30.0f;
    
    public AtomsOverTime()
    {
        super("Atoms over time");
        
        state = Kinetix.STATE;
        state.updateListeners.add(this);
    }
    
    @Override
    public void onClose(ProfilerUI ui)
    {
        state.updateListeners.remove(this);
    }
    
    private ProfilerUI ui;
    private ImguiContext context;
    private SimulationState state;

    private static final int SECOND_CHART_NONE = 0;
    private static final int SECOND_CHART_A_OVER_T = 1;
    private static final int SECOND_CHART_INV_C_OVER_T = 2;
    private static final int SECOND_CHART_LN_C_OVER_T = 3;
    
    private Chart firstChart = new Chart();
    private Chart secondChart = new Chart();
    private Chart thirdChart = new Chart();
    private boolean thirdChartActive = false;
    private int secondChartType = SECOND_CHART_NONE;

    private Object activeInstance = null;
    private Object activeSeries = null;
    
    private float previousTime;
    private int[] previousCounts = new int[Reactions.ATOM_TYPE_COUNT];

    private float newTime;
    private int[] newCounts = new int[Reactions.ATOM_TYPE_COUNT];
    
    public void tryAddLineToSecondChart(Line line)
    {
        if (line.unique != state.reactions[0].reactant1_unique) return;

        float c1 = Math.max(line.y1, 1);
        float c2 = Math.max(line.y2, 1);
        float c1glupo = (float)(c1 / Math.pow(state.settings.width / 100, 3) / 100 / 6.022);
        float c2glupo = (float)(c2 / Math.pow(state.settings.width / 100, 3) / 100 / 6.022);

        if (secondChartType == SECOND_CHART_A_OVER_T)
        {
            float a1 = line.y1;
            float a2 = line.y2;
            secondChart.addLine(line.unique, line.x1, line.x2, a1, a2);
        }
        else if (secondChartType == SECOND_CHART_INV_C_OVER_T)
        {
            float inverseCount1 = 1.0f / c1glupo;
            float inverseCount2 = 1.0f / c2glupo;
            secondChart.addLine(line.unique, line.x1, line.x2, inverseCount1, inverseCount2);
        }
        else if (secondChartType == SECOND_CHART_LN_C_OVER_T)
        {
            float lnCount1 = (float) Math.log(c1glupo);
            float lnCount2 = (float) Math.log(c2glupo);
            secondChart.addLine(line.unique, line.x1, line.x2, lnCount1, lnCount2);
        }
    }
    
    public void rebuildSecondChart()
    {
        synchronized (state)
        {
            secondChart.clear();
            if (!state.readyToUse) return;
            for (Series aSeries : firstChart.series)
            {
                secondChart.addSeries();
                secondChart.activeSeries.temperature = aSeries.temperature;
                secondChart.activeSeries.reaction = aSeries.reaction;
                for (DataSet dataSet : aSeries.dataSets)
                {
                    secondChart.addDataSet();
                    for (Line line : dataSet.lines)
                        tryAddLineToSecondChart(line);
                }
            }
        }
    }
    
    public void collectData()
    {
        if (state.series != null && state.series.id != activeSeries)
        {
            activeSeries = state.series.id;
            firstChart.addSeries();
            secondChart.addSeries();
            activeInstance = null;
            secondChart.activeSeries.temperature = firstChart.activeSeries.temperature = (float) state.settings.temperature;
            secondChart.activeSeries.reaction = firstChart.activeSeries.reaction = state.reactions[0];
        }
        
        if (state.instanceID != activeInstance)
        {
            activeInstance = state.instanceID;
            firstChart.addDataSet();
            secondChart.addDataSet();
            
            previousTime = 0;
            for (int unique = 0; unique < newCounts.length; unique++)
            {
                if (state.atomTypes[unique] == null) continue;
                previousCounts[unique] = state.atomTypes[unique].initialCount;
                firstChart.uniqueColors[unique] = state.atomTypes[unique].color;
                secondChart.uniqueColors[unique] = state.atomTypes[unique].color;
                thirdChart.uniqueColors[unique] = state.atomTypes[unique].color;
            }
        }
        
        newTime = (float) state.simulationTime;
        for (int unique = 0; unique < Reactions.ATOM_TYPE_COUNT; unique++)
        {
            if (state.atomTypes[unique] == null)
                newCounts[unique] = 0;
            else
                newCounts[unique] = state.atomTypes[unique].currentCount;
        }
        
        for (int unique = 0; unique < newCounts.length; unique++)
        {
            if (state.atomTypes[unique] == null) continue;

            Line line = new Line();
            line.unique = unique;
            line.x1 = previousTime;
            line.x2 = newTime;
            line.y1 = previousCounts[unique];
            line.y2 = newCounts[unique];
            
            firstChart.addLine(line);
            tryAddLineToSecondChart(line);
            
            previousCounts[unique] = newCounts[unique];
        }
        
        previousTime = newTime;
    }
    
    @Override
    public void onSimulationUpdate(SimulationState state)
    {
        assert(state == this.state);
        synchronized (state)
        {
            collectData();
        }
    }
    
    private int framesToSkip = 0;

    @Override
    public void update(ProfilerUI ui)
    {
        this.ui = ui;
        thirdChart.g = secondChart.g = firstChart.g = ui.g;
        context = ui.context;
        
        context.resizable = true;

        updateInput();

        if (framesToSkip > 0)
        {
            framesToSkip--;
        }
        else
        {
            synchronized (state)
            {
                if (!state.readyToUse) return;
                updateGraph();

                firstChart.renderGraph();
                firstChart.renderStructure();
                firstChart.interactive(ui);
                
                if (secondChartType != SECOND_CHART_NONE)
                {
                    secondChart.renderGraph();
                    secondChart.renderStructure();
                    secondChart.interactive(ui);
                }
                
                if (thirdChartActive)
                {
                    float top = PADDING_TOP + (context.bounds.height - PADDING_BOTTOM - PADDING_TOP) / 2 + 4;
                    float bottom = context.bounds.height;
                    float left = 0;
                    float right = context.bounds.width;
                    
                    ui.g.setColor(ProfilerUI.PROFILER_BACKGROUND);
                    ui.g.fill(new Rectangle.Float(left, top, right - left, bottom - top));

                    thirdChart.renderGraph();
                    Shape previousClip = ui.g.getClip();
                    Shape newClip = thirdChart.activeSeries.clip(thirdChart.activeSeries.displayX, true);
                    if (newClip != null)
                    {
                        ui.g.setClip(newClip);
                        thirdChart.renderLeastSquare(thirdChart.activeSeries, thirdChart.horMaximum, 1, true);
                        ui.g.setClip(previousClip);
                    }
                    thirdChart.renderStructure();
                    thirdChart.interactive(ui);
                }
            }
        }
        if (!state.realtime && framesToSkip == 0)
            framesToSkip = 8;
    }

    public void updateInput()
    {
        ui.beginRow();
        if (ui.doButton("clear", 0, 18))
        {
            activeInstance = null;
            activeSeries = null;
            firstChart.clear();
            rebuildSecondChart();
            thirdChartActive = false;
        }
        if (ui.doButton("+ R", 0, 18))
        {
            firstChart.horMaximum *= 2.0f;
            firstChart.horMarkEvery *= 2.0f;
            secondChart.horMaximum *= 2.0f;
            secondChart.horMarkEvery *= 2.0f;
        }
        if (ui.doButton("- R", 0, 18))
        {
            firstChart.horMaximum /= 2.0f;
            firstChart.horMarkEvery /= 2.0f;
            secondChart.horMaximum /= 2.0f;
            secondChart.horMarkEvery /= 2.0f;
        }

        thirdChart.major = secondChart.major = firstChart.major = ui.doCheckbox("major", 0, 18, firstChart.major);
        thirdChart.minor = secondChart.minor = firstChart.minor = ui.doCheckbox("minor", 0, 18, firstChart.minor);
        
        String renderModeText = null;
        switch (firstChart.renderMode)
        {
        case Chart.RENDER_CONTINUE: renderModeText = "continue"; break;
        case Chart.RENDER_OVERLAP: renderModeText = "overlap"; break;
        case Chart.RENDER_AVERAGE: renderModeText = "average"; break;
        }
        if (ui.doButton(renderModeText, 0, 18))
            secondChart.renderMode = firstChart.renderMode = (firstChart.renderMode + 1) % Chart.RENDER_MODE_COUNT;
        
        String secondChartTypeText = null;
        switch (secondChartType)
        {
        case SECOND_CHART_NONE:             secondChartTypeText = "none"; break;
        case SECOND_CHART_A_OVER_T:         secondChartTypeText = "A over t"; break;
        case SECOND_CHART_INV_C_OVER_T:     secondChartTypeText = "1⁄c over t"; break;
        case SECOND_CHART_LN_C_OVER_T:      secondChartTypeText = "ln c over t"; break;
        }
        if (ui.doButton(secondChartTypeText, 0, 18))
        {
            secondChartType = (secondChartType + 1) % 4;
            rebuildSecondChart();
        }
        
        if (firstChart.renderMode != Chart.RENDER_AVERAGE || secondChartType == SECOND_CHART_NONE)
            thirdChartActive = false;
        else
            thirdChartActive = ui.doCheckbox("ln k over 1⁄T", 0, 18, thirdChartActive);
        
        ui.endRow();
    }
    
    public void updateGraph()
    {
        {
            float top = PADDING_TOP;
            float bottom = top + (context.bounds.height - PADDING_BOTTOM - PADDING_MIDDLE - PADDING_TOP) / 2;
            if (secondChartType == SECOND_CHART_NONE)
                bottom = context.bounds.height - PADDING_BOTTOM;
            
            firstChart.verLabel = "atom [1]";
            firstChart.verX = PADDING_LEFT;
            firstChart.verY1 = bottom;
            firstChart.verY2 = top;
            firstChart.noScalePadding = true;
            firstChart.scaleVerticalAxis();
            
            firstChart.horLabel = "t [s]";
            firstChart.horY = bottom;
            firstChart.horX1 = PADDING_LEFT;
            firstChart.horX2 = context.bounds.width - PADDING_RIGHT;
            firstChart.horPixelsPerUnit = (firstChart.horX2 - firstChart.horX1) / firstChart.horMaximum;
        }
        
        if (secondChartType != SECOND_CHART_NONE)
        {
            float top = PADDING_TOP + (context.bounds.height - PADDING_BOTTOM - PADDING_MIDDLE - PADDING_TOP) / 2 + PADDING_MIDDLE;
            float bottom = context.bounds.height - PADDING_BOTTOM;
            
            secondChart.doLeastSquare = true;
            
            switch (secondChartType)
            {
            case SECOND_CHART_A_OVER_T:     secondChart.verLabel = "A";     break;
            case SECOND_CHART_INV_C_OVER_T: secondChart.verLabel = "1 ⁄ c"; break;
            case SECOND_CHART_LN_C_OVER_T:  secondChart.verLabel = "ln c";  break;
            }
            secondChart.verX = PADDING_LEFT;
            secondChart.verY1 = bottom;
            secondChart.verY2 = top;
            secondChart.scaleVerticalAxis();
            
            secondChart.horLabel = "t [s]";
            secondChart.horY = bottom;
            secondChart.horX1 = PADDING_LEFT;
            secondChart.horX2 = context.bounds.width - PADDING_RIGHT;
            secondChart.horPixelsPerUnit = (secondChart.horX2 - secondChart.horX1) / firstChart.horMaximum;
        }
        
        if (thirdChartActive)
        {
            thirdChart.clear();
            thirdChart.addSeries();
            thirdChart.addDataSet();

            float maxX = -Float.MAX_VALUE;
            float n, sumX, sumY, sumXX, sumXY;
            n = sumX = sumY = sumXX = sumXY = 0;
            
            class KPoint { float x, y; }
            List<KPoint> points = new ArrayList<KPoint>();
            for (Series aSeries : secondChart.series)
            {
                thirdChart.activeSeries.reaction = aSeries.reaction;
                
                KPoint point = new KPoint();
                point.x = (float)(1.0 / aSeries.temperature);
                point.y = (float) Math.log(aSeries.calculatedK);
                points.add(point);
                maxX = Math.max(maxX, point.x);
                
                n++;
                sumX += point.x;
                sumY += point.y;
                sumXX += point.x * point.x;
                sumXY += point.x * point.y;
            }
            points.sort(new Comparator<KPoint>(){ public int compare(KPoint arg0, KPoint arg1) { return Float.compare(arg0.x, arg1.x); }; });
            if (maxX <= 0) maxX = 1;

            {
                float a = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
                float b = (sumXX * sumY - sumX * sumXY) / (n * sumXX - sumX * sumX);
                thirdChart.activeSeries.calculatedK = a;
                thirdChart.activeSeries.calculatedA = 1.0f / b;
            }

            int unique = state.reactions[0].reactant1_unique;
            for (int i = 0; i < points.size() - 1; i++)
            {
                KPoint a = points.get(i);
                KPoint b = points.get(i + 1);
                thirdChart.addLine(unique, a.x, b.x, a.y, b.y);
            }

            float top = PADDING_TOP + (context.bounds.height - PADDING_BOTTOM - PADDING_MIDDLE - PADDING_TOP) / 2 + PADDING_MIDDLE;
            float bottom = context.bounds.height - PADDING_BOTTOM;
            
            thirdChart.renderMode = Chart.RENDER_CONTINUE;
            thirdChart.doLeastSquare = false;
            
            thirdChart.verLabel = "ln k";
            thirdChart.verX = PADDING_LEFT;
            thirdChart.verY1 = bottom;
            thirdChart.verY2 = top;
            thirdChart.scaleVerticalAxis();
            
            thirdChart.horLabel = "1⁄T";
            thirdChart.horY = bottom;
            thirdChart.horX1 = PADDING_LEFT;
            thirdChart.horX2 = context.bounds.width - PADDING_RIGHT;
            thirdChart.horMaximum = maxX;
            thirdChart.horMarkEvery = maxX * 0.2f;
            thirdChart.horPixelsPerUnit = (thirdChart.horX2 - thirdChart.horX1) / maxX;
        }
    }
}
