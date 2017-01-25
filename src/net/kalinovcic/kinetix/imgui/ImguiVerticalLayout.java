package net.kalinovcic.kinetix.imgui;

import java.awt.geom.Rectangle2D;

public class ImguiVerticalLayout extends ImguiLayout
{
    @Override
    public void moveBox(Rectangle2D.Float r)
    {
        y += r.height + ImguiTheme.PADDING_VERTICAL;

        width = Math.max(width, r.width);
        if (height != 0) height += ImguiTheme.PADDING_VERTICAL;
        
        height += r.height;
    }
    
    @Override
    public void nextBox(Rectangle2D.Float r)
    {
        r.x = x;
        r.y = y;
        if (r.width <= 0.0) r.width = -r.width;
        if (r.height <= 0.0) r.height = -r.height;
        
        moveBox(r);
    }
}