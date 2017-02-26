package net.kalinovcic.kinetix.profiler;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import net.kalinovcic.kinetix.imgui.Imgui;
import net.kalinovcic.kinetix.imgui.ImguiBounds;
import net.kalinovcic.kinetix.imgui.ImguiVerticalLayout;
import net.kalinovcic.kinetix.profiler.atomsovertime.AtomsOverTime;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class ProfilerUI extends Imgui
{
    public static final float PROFILER_ROUNDED_RADIUS = 10.0f;
    public static final float PROFILER_PADDING = 5.0f;
    public static final Color PROFILER_BACKGROUND = new Color(53 / 2, 68 / 2, 72 / 2);
    
    public ProfilerWindow profilerWindow;
    
    public Profiler[] profilers;
    public String[] names;
    public int activeIndex;
    
    public ProfilerUI()
    {
        profilers = new Profiler[] { new AtomsOverTime() };
        
        names = new String[profilers.length];
        for (int i = 0; i < names.length; i++)
            names[i] = profilers[i].name;
        
        activeIndex = 0;
    }
    
    @Override
    public void update()
    {
        activeIndex = doTabs(names, context.bounds.width - 20, activeIndex);
        
        float yoff = FOLD_HEIGHT + PADDING_VERTICAL;
        pushBounds(new ImguiBounds(0.0f, yoff, context.bounds.width, context.bounds.height - yoff));
        pushLayout(new ImguiVerticalLayout());
        
        g.setColor(PROFILER_BACKGROUND);
        g.fill(rounded(new Rectangle2D.Float(0.0f, 0.0f, context.bounds.width, context.bounds.height), PROFILER_ROUNDED_RADIUS));
        
        pushBounds(new ImguiBounds(PROFILER_PADDING, PROFILER_PADDING, context.bounds.width - PROFILER_PADDING * 2, context.bounds.height - PROFILER_PADDING * 2));
        pushLayout(new ImguiVerticalLayout());

        context.layout.y = 0;
        context.layout.x = 0;
        profilers[activeIndex].update(this);
        
        popLayout();
        popBounds();
        
        popLayout();
        popBounds();
    }
    
    @Override
    public void onClose()
    {
        for (Profiler profiler : profilers)
            profiler.onClose(this);
    }
}
