package net.kalinovcic.kinetix.simulation;

import java.awt.Color;
import java.awt.Shape;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.Imgui;
import net.kalinovcic.kinetix.imgui.ImguiBounds;
import net.kalinovcic.kinetix.imgui.ImguiVerticalLayout;
import net.kalinovcic.kinetix.physics.AtomSnapshot;
import net.kalinovcic.kinetix.physics.SimulationSnapshot;
import net.kalinovcic.kinetix.physics.SimulationState;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class SimulationUI extends Imgui
{
    public static final int SIM_PADDING = 8;
    public static final int SIM_FOOTER = 12;
    
    public static final int SIM_NULL_WIDTH = 300;
    public static final int SIM_NULL_HEIGHT = 50;
    
    public static final int TOTAL_NULL_WIDTH = SIM_PADDING * 2 + SIM_NULL_WIDTH;
    public static final int TOTAL_NULL_HEIGHT = SIM_PADDING * 2 + SIM_NULL_HEIGHT + SIM_FOOTER;
    
    public static final Color VIEW_BACKGROUND = new Color(53 / 2, 68 / 2, 72 / 2);
    
    private SimulationState state;
    
    @Override
    public void update()
    {
        synchronized (Kinetix.STATE)
        {
            state = Kinetix.STATE;
            updateState();
        }
    }
    
    public void updateState()
    {
        for (int i = 0; i < context.typedChars.size(); i++)
        {
            if (context.typedChars.get(i) == ' ')
            {
                if (state.readyToUse)
                {
                    state.paused = !state.paused;
                }
                
                context.typedChars.remove(i);
                i--;
            }
        }
        
        renderState();
    }
    
    public void renderState()
    {
        int viewWidth;
        int viewHeight;
        if (!state.readyToUse)
        {
            viewWidth = TOTAL_NULL_WIDTH;
            viewHeight = TOTAL_NULL_HEIGHT;
        }
        else
        {
            viewWidth = state.settings.width;
            viewHeight = state.settings.height;
        }
        int contentWidth = viewWidth;
        int contentHeight = viewHeight + SIM_FOOTER;
        int windowWidth = contentWidth + SIM_PADDING * 2;
        int windowHeight = contentHeight + SIM_PADDING * 2;
        
        context.nextFrameWindowWidth = windowWidth;
        context.nextFrameWindowHeight = windowHeight;

        popLayout();
        popBounds();
        
        // Simulation view
        pushBounds(new ImguiBounds(SIM_PADDING, SIM_PADDING, viewWidth, viewHeight));
        pushLayout(new ImguiVerticalLayout());
        
        g.setColor(VIEW_BACKGROUND);
        g.fillRect(0, 0, viewWidth, viewHeight);
        if (!state.readyToUse)
        {
            doSpace(0, (viewHeight - g.getFontMetrics(BIG_FONT).getHeight()) / 2 - 8);
            doLabel("Nothing, yet...", viewWidth, 0.5f, BIG_FONT, null);
        }
        else
        {
            renderView();
        }
        
        popLayout();
        popBounds();
        
        // Footer
        pushBounds(new ImguiBounds(SIM_PADDING, SIM_PADDING + viewHeight, contentWidth, contentHeight - viewHeight));
        pushLayout(new ImguiVerticalLayout());

        doLabel("Simulation view");
    }
    
    public void renderView()
    {
        int snapshotIndex = LookbackUtil.getSnapshotIndexForLookback(state, state.lookback);
        double snapshotTime = LookbackUtil.getSnapshotTimeForLookback(state, state.lookback);

        /*if (state.paused && state.animation != null)
        {
            state.animation.progress(state, deltaTime);
            
            g2D.setColor(Color.BLACK);
            g2D.drawString("Lookback: " + String.format(Locale.US, "%.3fs", state.lookback), 0, 20);
            
            state.animation.preRender(state, window, g2D);
        }*/
        
        /*double availableTime = state.nextSnapshotDelta;
        int currentIndex = state.nextSnapshotIndex;
        while (true)
        {
            currentIndex = LookbackUtil.previous(state, currentIndex);
            if (!state.snapshots[currentIndex].valid) break;
            
            AtomSnapshot highlight = state.snapshots[currentIndex].highlightAtom;
            if (highlight != null)
            {
                double beginX = highlight.x;
                double beginY = highlight.y;
                double endX = beginX + highlight.vx * availableTime;
                double endY = beginY + highlight.vy * availableTime;
                g.setColor(highlight.type.color);
                g.draw(new Line2D.Double(beginX, beginY, endX, endY));
            }
            
            availableTime = state.snapshots[currentIndex].deltaTime;
        }*/
        
        SimulationSnapshot snapshot = state.snapshots[snapshotIndex];
        if (snapshot.valid)
        {
            for (int i = 0; i < snapshot.atomCount; i++)
            {
                AtomSnapshot atom = snapshot.atoms[i];

                Color color = atom.type.color;
                float nr = Math.min(color.getRed() / 255.0f * 1.3f, 1.0f);
                float ng = Math.min(color.getGreen() / 255.0f * 1.3f, 1.0f);
                float nb = Math.min(color.getBlue() / 255.0f * 1.3f, 1.0f);
                color = new Color(nr, ng, nb);
                
                /*float color_multiply = (float)(1.0 - (atom.z / state.settings.depth) * 0.8);
                color = new Color(color.getRed() / 255.0f * color_multiply,
                                  color.getGreen() / 255.0f * color_multiply,
                                  color.getBlue() / 255.0f * color_multiply);*/
                g.setColor(color);
                
                Shape shape = atom.toShape(snapshotTime, state.settings.depth);
                g.fill(shape);
            }
        }
        
        /*if (state.paused && state.animation != null)
            state.animation.render(state, window, g2D);*/
    }
}
