package net.kalinovcic.kinetix.imgui;

import java.awt.geom.Rectangle2D;

public abstract class ImguiLayout
{
    public float x = 0;
    public float y = 0;
    public float width = 0;
    public float height = 0;
    
    public abstract void moveBox(Rectangle2D.Float r);
    public abstract void nextBox(Rectangle2D.Float r);
}
