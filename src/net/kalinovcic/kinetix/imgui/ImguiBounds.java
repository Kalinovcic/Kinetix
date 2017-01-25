package net.kalinovcic.kinetix.imgui;

public class ImguiBounds
{
    public ImguiBounds parent;
    
    public float x;
    public float y;
    public float width;
    public float height;
    
    public ImguiBounds(float x, float y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
