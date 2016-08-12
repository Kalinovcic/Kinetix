package net.kalinovcic.kinetix;

import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.SimulationState;

public class Kinetix
{
    public static final SimulationState STATE = new SimulationState();

    public static SimulationSettings settings = STATE.settings;
}
