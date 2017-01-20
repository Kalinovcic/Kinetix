package net.kalinovcic.kinetix.test;

import java.awt.Color;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.test.TestingConfiguration.TestingUnit;

public class TestAll
{
    private static final int TEMP_DIV = 10;
    private static final int[] COUNTS = new int[] { 100 };
    private static final double TIME = 60.0;
    private static final int REPEAT = 100;
    
    private static void initAtom(TestingUnit unit, String type, int count)
    {
        int unique = Reactions.uniqueAtoms.get(type);
        unit.atomTypes[unique] = new AtomType();
        unit.atomTypes[unique].name = type;
        unit.atomTypes[unique].unique = unique;
        unit.atomTypes[unique].initialCount = count;
        unit.atomTypes[unique].mass = Reactions.findMass(type);
        unit.atomTypes[unique].radius = Reactions.findRadius(type);
        unit.atomTypes[unique].color = Color.BLACK;
    }
    
    private static void initUnit(TestingUnit unit, Reaction reaction, double temperature, int count)
    {
        unit.time = TIME;
        unit.repeat = REPEAT;
        unit.scale = 1.0;

        unit.reaction = reaction.clone();
        unit.reaction.temperature = temperature;
        unit.reaction.recalculate();
        
        unit.settings = new SimulationSettings();
        unit.settings.doSteric = false;
        unit.settings.timeFactor = 1.0;
        unit.settings.temperature = temperature;
        unit.settings.width = 1000;
        unit.settings.height = 1000;
        unit.settings.depth = 1000;
        
        unit.atomTypes = new AtomType[Reactions.ATOM_TYPE_COUNT];
        initAtom(unit, unit.reaction.reactant1, count);
        initAtom(unit, unit.reaction.reactant2, count);
        initAtom(unit, unit.reaction.product1, 0);
        initAtom(unit, unit.reaction.product2, 0);
        unit.atomTypes[Reactions.uniqueAtoms.get(unit.reaction.reactant1)].reactantInReaction = unit.reaction;
        unit.atomTypes[Reactions.uniqueAtoms.get(unit.reaction.reactant2)].reactantInReaction = unit.reaction;
    }
    
    public static void testAll(MainWindow mainWindow)
    {
        
        TestingConfiguration configuration = new TestingConfiguration();
        TestingUnit previous = null;
        
        for (Reaction reaction : Reactions.reactions)
        {
            double tempBegin = 200;
            double tempEnd = 3800;
            if (reaction.temperatureRange_known)
            {
                tempBegin = reaction.temperatureRange_low;
                tempEnd = reaction.temperatureRange_high;
            }
            
            for (int i = 0; i <= TEMP_DIV; i++)
            {
                double temperature = tempBegin + (tempEnd - tempBegin) / TEMP_DIV * i;
                
                for (int count : COUNTS)
                {
                    TestingUnit unit = new TestingUnit();
                    if (previous == null)
                        configuration.head = unit;
                    else
                        previous.next = unit;
                    previous = unit;
                    
                    initUnit(unit, reaction, temperature, count);
                }
            }
        }
        
        new TestingThread(mainWindow, configuration).start();
    }
}
