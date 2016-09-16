package net.kalinovcic.kinetix.physics;

import java.awt.Color;

import net.kalinovcic.kinetix.physics.reaction.Reaction;

public class AtomType
{
    public Color color;
    public Reaction reactantInReaction;
    
    public AtomType(Color color, Reaction reactantInReaction)
    {
        this.color = color;
        this.reactantInReaction = reactantInReaction;
    }
}
