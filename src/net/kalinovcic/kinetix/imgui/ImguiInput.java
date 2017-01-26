package net.kalinovcic.kinetix.imgui;

public abstract class ImguiInput
{
    public boolean hasFocus;
    public String text = "";
    
    public abstract String accept(String text);
}
