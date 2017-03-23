package net.kalinovcic.kinetix.imgui;

import java.util.ArrayList;
import java.util.List;

public class ImguiContext 
{
    public int currentFrameX;
    public int currentFrameY;
    public int currentFrameWidth;
    public int currentFrameHeight;
    public int nextFrameWidth;
    public int nextFrameHeight;
    
    public ImguiBounds bounds;
    
    public List<ImguiLayout> layoutStack = new ArrayList<ImguiLayout>();
    public ImguiLayout layout;

    public boolean focus;
    public float totalTranslationX;
    public float totalTranslationY;

    public int WINDOW_LEFT  = 0;
    public int WINDOW_RIGHT = 1;
    public int WINDOW_UP    = 2;
    public int WINDOW_DOWN  = 3;
    
    public boolean resizable;
    
    public boolean mouseBusy;
    public boolean mouseDragging;
    public boolean mouseResize[] = new boolean[4];
    public int pressMouseScreenX;
    public int pressMouseScreenY;
    public int pressFrameX;
    public int pressFrameY;
    public int pressFrameWidth;
    public int pressFrameHeight;

    public int mouseX;
    public int mouseY;
    public int mouseScreenX;
    public int mouseScreenY;
    
    public boolean mouseDown;
    public boolean mouseDownPrevious;
    public boolean mousePressed;
    public boolean mouseReleased;

    public float mouseVerticalScrollDelta;
    public float mouseHorizontalScrollDelta;
    
    public List<Character> typedChars = new ArrayList<Character>();
}
