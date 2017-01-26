package net.kalinovcic.kinetix.physics;

import java.awt.Color;

import net.kalinovcic.kinetix.imgui.ImguiDoubleInput;
import net.kalinovcic.kinetix.imgui.ImguiIntegerInput;
import net.kalinovcic.kinetix.physics.reaction.Reaction;

public class AtomType
{
    public String name;
    public int unique;
    
    public int initialCount;
    
    public double mass;
    public double radius;
    public Color color;
    public Reaction reactantInReaction;

    public ImguiIntegerInput initialCountInput;
    public ImguiDoubleInput massInput;
    public ImguiDoubleInput radiusInput;
}
