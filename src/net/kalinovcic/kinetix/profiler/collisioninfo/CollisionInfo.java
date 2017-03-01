package net.kalinovcic.kinetix.profiler.collisioninfo;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.imgui.ImguiContext;
import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.profiler.Profiler;
import net.kalinovcic.kinetix.profiler.ProfilerUI;

public class CollisionInfo extends Profiler
{
    public CollisionInfo()
    {
        super("Collision Info");
        state = Kinetix.STATE;
    }
    @Override public void onClose(ProfilerUI ui) {}

    private Graphics2D g;
    private ImguiContext context;
    private SimulationState state;

    @Override
    public void update(ProfilerUI ui)
    {
        g = ui.g;
        context = ui.context;

        synchronized (state)
        {
            updateInput();
            
            if (!state.readyToUse) return;
            
            renderTable();
        }
    }
    
    public void updateInput()
    {
    }
    
    public void renderTable()
    {
        g.setFont(BIGGER_FONT);
        
        FontMetrics metrics = g.getFontMetrics();
        float height = metrics.getHeight();
        float ascent = metrics.getAscent();

        final float PADDING_LEFT = 70;
        final float PADDING_TOP  = 50;
        final float COLUMN_WIDTH = 75;
        final float ROW_HEIGHT   = height;
        
        List<AtomType> types = new ArrayList<AtomType>();
        for (AtomType type : state.atomTypes)
            if (type != null)
                types.add(type);
        
        final float[] hsbvals = new float[3];
        Collections.sort(types, new Comparator<AtomType>()
        {
            public int compare(AtomType o1, AtomType o2)
            {
                Color.RGBtoHSB(o1.color.getRed(), o1.color.getGreen(), o1.color.getBlue(), hsbvals);
                float hue1 = hsbvals[0];
                Color.RGBtoHSB(o2.color.getRed(), o2.color.getGreen(), o2.color.getBlue(), hsbvals);
                float hue2 = hsbvals[0];
                return Float.compare(hue1, hue2);
            };
        });
        
        int atomIndex = 0;
        for (AtomType type : types)
        {
            String text = type.name;
            g.setColor(type.color);
            g.drawString(text, PADDING_LEFT + atomIndex * COLUMN_WIDTH + (COLUMN_WIDTH - metrics.stringWidth(text)) / 2.0f, PADDING_TOP - height + ascent);
            g.drawString(text, PADDING_LEFT - metrics.stringWidth(text) - 4, PADDING_TOP + atomIndex * ROW_HEIGHT + (ROW_HEIGHT - height) / 2.0f + ascent);
            atomIndex++;
        }

        g.setColor(TEXT);
        g.setFont(FONT);
        
        metrics = g.getFontMetrics();
        height = metrics.getHeight();
        ascent = metrics.getAscent();
        
        atomIndex = 0;
        for (AtomType type : types)
        {
            int otherIndex = 0;
            for (AtomType otherType : types)
            {
                String text = Integer.toString(state.collisionInfo[type.unique][otherType.unique][0]);
                text += " (" + Integer.toString(state.collisionInfo[type.unique][otherType.unique][1]) + ")";
                g.drawString(text, PADDING_LEFT + atomIndex * COLUMN_WIDTH + (COLUMN_WIDTH - metrics.stringWidth(text)) / 2.0f, PADDING_TOP + otherIndex * ROW_HEIGHT + (ROW_HEIGHT - height) / 2.0f + ascent);
                otherIndex++;
            }
            atomIndex++;
        }

        context.nextFrameWidth = (int)(PADDING_LEFT * 2 + atomIndex * COLUMN_WIDTH);
        context.nextFrameHeight = (int)(PADDING_TOP * 2 + (atomIndex + 1) * ROW_HEIGHT);
    }
}
