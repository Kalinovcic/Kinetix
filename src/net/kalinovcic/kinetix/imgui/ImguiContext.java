package net.kalinovcic.kinetix.imgui;

import java.util.ArrayList;
import java.util.List;

public class ImguiContext 
{
    public int currentWindowWidth;
    public int currentWindowHeight;
    public int nextFrameWindowWidth;
    public int nextFrameWindowHeight;
    
    public ImguiBounds bounds;
    
    public List<ImguiLayout> layoutStack = new ArrayList<ImguiLayout>();
    public ImguiLayout layout;

    public int mouseX;
    public int mouseY;
    public int mouseBeginX;
    public int mouseBeginY;
    public boolean mouseBusy;

    public boolean dragging;
    public int windowBeginX;
    public int windowBeginY;
    
    public boolean mouseDown;
    public boolean mouseDownPrevious;
    public boolean mousePressed;
    public boolean mouseReleased;
    
    public List<Character> typedChars = new ArrayList<Character>();
}
