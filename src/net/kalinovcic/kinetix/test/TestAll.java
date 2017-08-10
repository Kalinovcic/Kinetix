package net.kalinovcic.kinetix.test;

import java.awt.Color;

import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.reaction.AtomData;
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
        int unique = Reactions.findUnique(type);
        if (unit.atomTypes[unique] != null)
            return;
        unit.atomTypes[unique] = new AtomType();
        unit.atomTypes[unique].name = type;
        unit.atomTypes[unique].unique = unique;
        unit.atomTypes[unique].initialCount = count;
        unit.atomTypes[unique].mass = AtomData.calculateMass(type);
        unit.atomTypes[unique].radius = AtomData.getRadius(type);
        unit.atomTypes[unique].color = Color.BLACK;
        unit.atomTypes[unique].currentCount = unit.atomTypes[unique].initialCount;

        if (unit.atomTypes[unique].mass < 0) throw new IllegalStateException();
        if (unit.atomTypes[unique].radius < 0) throw new IllegalStateException();
    }
    
    private static void initUnit(TestingUnit unit, Reaction reaction, double temperature, int count)
    {
        unit.time = TIME;
        unit.repeat = REPEAT;
        unit.scale = 1.0;

        unit.reactions = new Reaction[1];
        unit.reactions[0].clone();
        unit.reactions[0].temperature = temperature;
        unit.reactions[0].recalculate();
        
        unit.settings = new SimulationSettings();
        unit.settings.doSteric = false;
        unit.settings.timeFactor = 1.0;
        unit.settings.temperature = temperature;
        unit.settings.width = 1000;
        unit.settings.height = 1000;
        unit.settings.depth = 1000;
        
        unit.atomTypes = new AtomType[Reactions.ATOM_TYPE_COUNT];
        initAtom(unit, unit.reactions[0].reactant1, count);
        initAtom(unit, unit.reactions[0].product1, 0);
        if (unit.reactions[0].reactant2 != null)
            initAtom(unit, unit.reactions[0].reactant2, count);
        if (unit.reactions[0].product2 != null)
            initAtom(unit, unit.reactions[0].product2, 0);
        unit.atomTypes[Reactions.findUnique(unit.reactions[0].reactant1)].reactantInReactions.add(unit.reactions[0]);
        if (unit.reactions[0].reactant2 != null)
            unit.atomTypes[Reactions.findUnique(unit.reactions[0].reactant2)].reactantInReactions.add(unit.reactions[0]);
    }
    
    public static void testAll(MainWindow mainWindow)
    {
        
        TestingConfiguration configuration = new TestingConfiguration();
        TestingUnit previous = null;
        
        for (Reaction reaction : Reactions.reactions)
        {
            double tempBegin = reaction.t_low;
            double tempEnd = reaction.t_high;
            
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
