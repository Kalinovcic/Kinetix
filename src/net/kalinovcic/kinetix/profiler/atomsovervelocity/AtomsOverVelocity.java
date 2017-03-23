package net.kalinovcic.kinetix.profiler.atomsovervelocity;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.ImguiContext;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.SimulationUpdateListener;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerUI;

public class AtomsOverVelocity extends Profiler implements SimulationUpdateListener
{
    private static final float PADDING_LEFT = 10.0f;
    private static final float PADDING_BOTTOM = 35.0f;
    private static final float PADDING_RIGHT = 10.0f;
    private static final float PADDING_TOP = 24.0f;
    
    public static final Color STRUCTURE_COLOR = TEXT;
    
    public AtomsOverVelocity()
    {
        super("Atoms over velocity");
        
        state = Kinetix.STATE;
        state.updateListeners.add(this);
        
        recreateGraph();
    }
    
    @Override
    public void onClose(ProfilerUI ui)
    {
        state.updateListeners.remove(this);
    }

    private Graphics2D g;
    private ProfilerUI ui;
    private ImguiContext context;
    private SimulationState state;
    
    private static class FrameData
    {
        public double simulationTime;
        
        public int[] uniques;
        public double[] velocities;
    };

    private Object currentInstance = null;
    private float timeInterval = 1.0f;
    private List<FrameData> frames = new ArrayList<FrameData>();
    
    @Override
    public void onSimulationUpdate(SimulationState state)
    {
        assert(state == this.state);
        synchronized (state)
        {
            collectData();
        }
    }
    
    public void checkInstance()
    {
        if (state.instanceID != currentInstance)
        {
            frames.clear();
            currentInstance = state.instanceID;

            uniques.clear();
            for (int unique = 0; unique < Reactions.ATOM_TYPE_COUNT; unique++)
            {
                if (state.atomTypes[unique] == null) continue;
                uniques.add(unique);
            }
            
            recreateGraph();
        }
    }
    
    public void collectData()
    {
        checkInstance();
        
        while (frames.size() > 0 && (state.simulationTime - frames.get(0).simulationTime) >= timeInterval)
        {
            updateGraph(frames.get(0), -1);
            frames.remove(0);
        }
        
        FrameData frame = new FrameData();
        frame.simulationTime = state.simulationTime;
        frame.uniques = new int[state.atoms.size()];
        frame.velocities = new double[state.atoms.size()];
        
        int index = 0;
        for (Atom atom : state.atoms)
        {
            frame.uniques[index] = atom.type.unique;
            frame.velocities[index] = atom.velocity.length();
            index++;
        }
        
        frames.add(frame);
        updateGraph(frame, 1);
    }

    private static final int DISPLAY_OVERLAPPED = 0;
    private static final int DISPLAY_LAYERED    = 1;
    private int displayMode = DISPLAY_OVERLAPPED;

    private List<Integer> uniques = new ArrayList<Integer>();
    private double maxVelocity = 4000.0;
    private double velocityInterval = 100.0;
    private int columnCount;

    private int maxCount;
    private int[] maxCountPerUnique = new int[Reactions.ATOM_TYPE_COUNT];
    private int[][] columns;
    
    public void updateGraph(FrameData frame, int addOrRemove)
    {
        for (int i = 0; i < frame.velocities.length; i++)
        {
            int unique = frame.uniques[i];
            double velocity = frame.velocities[i];
            
            int column = (int)(velocity / velocityInterval);
            if (column >= columnCount) continue;
            columns[column][unique] += addOrRemove;
        }

        maxCount = 0;
        for (int unique = 0; unique < Reactions.ATOM_TYPE_COUNT; unique++)
        {
            maxCountPerUnique[unique] = 0;
            for (int column = 0; column < columnCount; column++)
                maxCountPerUnique[unique] = Math.max(maxCountPerUnique[unique], columns[column][unique]);
            maxCount = Math.max(maxCount, maxCountPerUnique[unique]);
        }
    }
    
    public void recreateGraph()
    {
        synchronized (state)
        {
            columnCount = (int)(maxVelocity / velocityInterval);
            columns = new int[columnCount][Reactions.ATOM_TYPE_COUNT];
            
            for (int[] arr : columns)
                for (int i = 0; i < arr.length; i++)
                    arr[i] = 0;
            
            maxCount = 0;
            for (FrameData frame : frames)
                updateGraph(frame, 1);
        }
    }
    
    private int framesToSkip;

    @Override
    public void update(ProfilerUI ui)
    {
        g = ui.g;
        this.ui = ui;
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
                
                left   = PADDING_LEFT;
                right  = context.bounds.width - PADDING_RIGHT;
                bottom = context.bounds.height - PADDING_BOTTOM;
                top    = PADDING_TOP;
                
                checkInstance();
                
                renderGraph();
                renderStructure();
            }
        }
        if (!state.realtime && framesToSkip == 0)
            framesToSkip = 8;
    }

    private float desiredWidth = Float.NaN;
    private float desiredHeight = Float.NaN;
    
    public void updateInput()
    {
        if (Float.isNaN(desiredWidth)) desiredWidth = context.currentFrameWidth;
        if (Float.isNaN(desiredHeight)) desiredHeight = context.currentFrameHeight;
        
        ui.beginRow();
        ui.doLabel("T=" + String.format(Locale.US, "%.0f", timeInterval) + "s", 0);
        if (ui.doButton("+ T", 0, 18)) timeInterval += 1;
        if (ui.doButton("- T", 0, 18)) timeInterval = Math.max(0, timeInterval - 1);
        ui.doLabel("R=" + String.format(Locale.US, "%.1f", velocityInterval), 0);
        if (ui.doButton("+ R", 0, 18)) { velocityInterval /= 2; recreateGraph(); }
        if (ui.doButton("- R", 0, 18)) { velocityInterval *= 2; recreateGraph(); }
        if (ui.doButton("+ W", 0, 18)) desiredWidth += 50;
        if (ui.doButton("- W", 0, 18)) desiredWidth -= 50;
        if (ui.doButton("+ H", 0, 18)) desiredHeight += 50;
        if (ui.doButton("- H", 0, 18)) desiredHeight -= 50;
        
        String displayModeText = null;
        switch (displayMode)
        {
        case DISPLAY_OVERLAPPED: displayModeText = "overlap"; break;
        case DISPLAY_LAYERED:    displayModeText = "layered"; break;
        }
        if (ui.doButton(displayModeText, 0, 18))
            displayMode = (displayMode + 1) % 2;
        
        context.nextFrameWidth = (int) desiredWidth;
        context.nextFrameHeight = (int) desiredHeight;
        ui.endRow();
    }

    private Line2D.Float line2D = new Line2D.Float();
    private float left;
    private float right;
    private float bottom;
    private float top;
    
    public void renderStructure()
    {
        g.setColor(STRUCTURE_COLOR);
        g.setFont(SMALL_FONT);

        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();

        line2D.x1 = left; line2D.x2 = right;
        line2D.y1 = line2D.y2 = bottom;
        g.draw(line2D);

        float columnWidth = (right - left) / (float) columnCount;
        for (int column = 0; column <= columnCount; column++)
        {
            float velocity = (float)(column * velocityInterval);
            float x = left + column * columnWidth;
            
            line2D.x1 = line2D.x2 = x;
            line2D.y2 = (line2D.y1 = bottom) + 2;
            g.draw(line2D);

            String text = String.format(Locale.US, "%.0f", velocity);
            float textX = x + height * 0.5f - ascent;
            float textY = bottom + 6;
            
            AffineTransform oldTransform = g.getTransform();
            g.translate(textX, textY);
            g.rotate(Math.toRadians(90));
            g.drawString(text, 0, 0);
            g.setTransform(oldTransform);
        }
    }
    
    public void renderGraph()
    {
        g.setFont(SMALL_FONT);
        
        boolean layered = displayMode == DISPLAY_LAYERED;
        
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();

        int indexToReposition = -1;
        
        float columnWidth = (right - left) / (float) columnCount;
        for (int column = 0; column < columnCount; column++)
        {
            double velocity = column * velocityInterval;
            
            float lowY = bottom;
            float availableHeight = (bottom - top) * (layered ? (1.0f / uniques.size()) : 1);
            
            int currentMaximum = 0;
            for (int i = 0; i < uniques.size(); i++)
            {
                int unique = uniques.get(i);
                
                boolean eligable = false;
                for (Reaction reaction : state.atomTypes[unique].reactantInReactions)
                {
                    double v = Math.sqrt(2 * reaction.Ea / reaction.reducedMass / Reaction.AVOGADRO * 1000) / 2;
                    if (velocity >= v)
                    {
                        eligable = true;
                        break;
                    }
                }
                
                if (state.atomTypes[unique] == null) continue;
                Color color = state.atomTypes[unique].color;
                if (eligable)
                    color = color.brighter();
                
                Color textColor = color.brighter().brighter();
            
                int count = columns[column][unique];
                if (count != 0)
                {
                    float x = left + column * columnWidth;
                    float y = (count / (float) maxCount) * (availableHeight - 16);
    
                    g.setColor(color);
                    if (count > currentMaximum)
                    {
                        float y2 = (currentMaximum / (float) maxCount) * (availableHeight - 16);
                        
                        Point2D.Float mouse = ui.mousePoint();
                        if (!layered)
                        {
                            if (context.mouseReleased &&
                                mouse.x >= x        && mouse.x <= x + columnWidth &&
                                mouse.y >= lowY - y && mouse.y <= lowY - y2)
                                indexToReposition = i;
                        }
                        
                        Shape box = new Rectangle2D.Float(x, lowY - y, columnWidth, y - y2);
                        g.fill(box);
                        g.setColor(color.darker());
                        g.draw(box);
                        
                        currentMaximum = count;
                    }
                    else
                    {
                        line2D.x1 = (line2D.x2 = x) + columnWidth;
                        line2D.y1 = line2D.y2 = lowY - y;
                        g.draw(line2D);
                    }
                    
                    String text = String.format(Locale.US, "%.1f", count / (float) frames.size());
                    g.setColor(textColor);
                    g.drawString(text, x + (columnWidth - metrics.stringWidth(text)) * 0.5f, lowY - y - height + ascent - 1);
                }
                
                if (layered)
                {
                    g.setColor(STRUCTURE_COLOR);
                    line2D.x1 = left; line2D.x2 = right;
                    line2D.y1 = line2D.y2 = lowY;
                    g.draw(line2D);
                    
                    Point2D.Float mouse = ui.mousePoint();
                    if (context.mouseReleased &&
                        mouse.x >= left && mouse.x <= right &&
                        mouse.y >= lowY - availableHeight && mouse.y <= lowY)
                        indexToReposition = i;
                    
                    lowY -= availableHeight;
                    currentMaximum = 0;
                }
            }
        }
        
        if (indexToReposition >= 0)
        {
            int unique = uniques.get(indexToReposition);
            uniques.remove(indexToReposition);
            if (indexToReposition == 0)
                uniques.add(unique);
            else
                uniques.add(0, unique);
        }
    }
}
