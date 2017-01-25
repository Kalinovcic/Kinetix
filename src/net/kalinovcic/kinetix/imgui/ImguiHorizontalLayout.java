package net.kalinovcic.kinetix.imgui;

import java.awt.geom.Rectangle2D;

public class ImguiHorizontalLayout extends ImguiLayout
{
    @Override
    public void moveBox(Rectangle2D.Float r)
    {
        x += r.width + ImguiTheme.PADDING_HORIZONTAL;

        if (width != 0) width += ImguiTheme.PADDING_HORIZONTAL;
        width += r.width;
        
        height = Math.max(height, r.height);
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