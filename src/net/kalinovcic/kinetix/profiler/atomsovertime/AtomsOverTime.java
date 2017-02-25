package net.kalinovcic.kinetix.profiler.atomsovertime;

import java.util.Arrays;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.ImguiContext;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerUI;

public class AtomsOverTime extends Profiler
{
    private static final float PADDING_LEFT = 30.0f;
    private static final float PADDING_BOTTOM = 15.0f;
    private static final float PADDING_RIGHT = 10.0f;
    private static final float PADDING_TOP = 36.0f;
    private static final float PADDING_MIDDLE = 30.0f;
    
    public AtomsOverTime()
    {
        super("Atoms over time");
    }
    
    private ProfilerUI ui;
    private ImguiContext context;
    private SimulationState state;

    private Chart firstChart = new Chart();
    private Chart secondChart = new Chart();

    @Override
    public void update(ProfilerUI ui)
    {
        this.ui = ui;
        secondChart.g = firstChart.g = ui.g;
        context = ui.context;
        state = Kinetix.STATE;

        if (!state.readyToUse) return;
        if (!state.paused) collectData();
        
        updateInput();
        updateGraph();
        
        firstChart.renderGraph();
        firstChart.renderStructure();
        firstChart.interactive(ui.mousePoint());
        
        secondChart.renderGraph();
        secondChart.renderStructure();
        secondChart.interactive(ui.mousePoint());
    }
    
    public void updateInput()
    {
        ui.beginRow();
        if (ui.doButton("clear", 0, 18))
        {
            maximumCount = 0;
            maximumInverseCount = 0;
            activeInstance = null;
            activeSeries = null;
            firstChart.clear();
            secondChart.clear();
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
        if (ui.doButton("+ W", 0, 18)) context.nextFrameWidth += 50;
        if (ui.doButton("- W", 0, 18)) context.nextFrameWidth -= 50;
        if (ui.doButton("+ H", 0, 18)) context.nextFrameHeight += 50;
        if (ui.doButton("- H", 0, 18)) context.nextFrameHeight -= 50;

        secondChart.major = firstChart.major = ui.doCheckbox("major", 0, 18, firstChart.major);
        secondChart.minor = firstChart.minor = ui.doCheckbox("minor", 0, 18, firstChart.minor);
        
        String renderModeText = null;
        switch (firstChart.renderMode)
        {
        case Chart.RENDER_CONTINUE: renderModeText = "continue"; break;
        case Chart.RENDER_OVERLAP: renderModeText = "overlap"; break;
        case Chart.RENDER_AVERAGE: renderModeText = "average"; break;
        }
        if (ui.doButton(renderModeText, 0, 18))
            secondChart.renderMode = firstChart.renderMode = (firstChart.renderMode + 1) % Chart.RENDER_MODE_COUNT;
        
        ui.endRow();
    }

    private Object activeInstance = null;
    private Object activeSeries = null;
    
    private int maximumCount = 0;
    private float maximumInverseCount = 0;
    
    private float previousTime;
    private int[] previousCounts = new int[Reactions.ATOM_TYPE_COUNT];

    private float newTime;
    private int[] newCounts = new int[Reactions.ATOM_TYPE_COUNT];
    
    public void collectData()
    {
        synchronized (state)
        {
            if (state.series != null && state.series.id != activeSeries)
            {
                activeSeries = state.series.id;
                firstChart.addSeries();
                secondChart.addSeries();
                activeInstance = null;
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
                }
            }
            
            Arrays.fill(newCounts, 0);
            
            newTime = (float) state.simulationTime;
            for (Atom atom : state.atoms)
                newCounts[atom.type.unique]++;

            {
                int unique = state.reactions[0].reactant1_unique;

                int c1 = Math.max(previousCounts[unique], 1);
                int c2 = Math.max(newCounts[unique], 1);
                float c1glupo = (float)(c1 / Math.pow(state.settings.width / 100, 3) / 100 / 6.022);
                float c2glupo = (float)(c2 / Math.pow(state.settings.width / 100, 3) / 100 / 6.022);
                float inverseCount1 = 1.0f / c1glupo;
                float inverseCount2 = 1.0f / c2glupo;
                maximumInverseCount = Math.max(maximumInverseCount, inverseCount2);
                secondChart.addLine(unique, previousTime, newTime, inverseCount1, inverseCount2);
            }
            
            for (int unique = 0; unique < newCounts.length; unique++)
            {
                if (state.atomTypes[unique] == null) continue;

                firstChart.addLine(unique, previousTime, newTime, previousCounts[unique], newCounts[unique]);
                if (newCounts[unique] > maximumCount)
                    maximumCount = newCounts[unique];
                
                previousCounts[unique] = newCounts[unique];
            }
            
            previousTime = newTime;
        }
    }
    
    public void updateGraph()
    {
        {
            float top = PADDING_TOP;
            float bottom = top + (context.bounds.height - PADDING_BOTTOM - PADDING_MIDDLE - PADDING_TOP) / 2;
            
            firstChart.verLabel = "atom [1]";
            firstChart.verX = PADDING_LEFT;
            firstChart.verY1 = bottom;
            firstChart.verY2 = top;
            firstChart.verPixelsPerUnit = (firstChart.verY1 - firstChart.verY2) * 0.9f / maximumCount;
            firstChart.verMarkEvery = 20.0f;
            firstChart.verMaximum = maximumCount;
            
            firstChart.horLabel = "t [s]";
            firstChart.horY = bottom;
            firstChart.horX1 = PADDING_LEFT;
            firstChart.horX2 = context.bounds.width - PADDING_RIGHT;
            firstChart.horPixelsPerUnit = (firstChart.horX2 - firstChart.horX1) / firstChart.horMaximum;
        }
        
        {
            float top = PADDING_TOP + (context.bounds.height - PADDING_BOTTOM - PADDING_MIDDLE - PADDING_TOP) / 2 + PADDING_MIDDLE;
            float bottom = context.bounds.height - PADDING_BOTTOM;
            
            float verMax = (maximumInverseCount == 0 ? 1 : maximumInverseCount);
            
            secondChart.doLeastSquare = true;
            
            secondChart.verLabel = "1 ⁄ c";
            secondChart.verX = PADDING_LEFT;
            secondChart.verY1 = bottom;
            secondChart.verY2 = top;
            secondChart.verPixelsPerUnit = (secondChart.verY1 - secondChart.verY2) * 0.9f / verMax;
            secondChart.verMarkEvery = verMax * 0.2f;
            secondChart.verMaximum = verMax;
            
            secondChart.horLabel = "t [s]";
            secondChart.horY = bottom;
            secondChart.horX1 = PADDING_LEFT;
            secondChart.horX2 = context.bounds.width - PADDING_RIGHT;
            secondChart.horPixelsPerUnit = (secondChart.horX2 - secondChart.horX1) / firstChart.horMaximum;
        }
    }
}
