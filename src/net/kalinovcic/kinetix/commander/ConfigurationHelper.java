package net.kalinovcic.kinetix.commander;

import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSettings;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.test.TestingConfiguration;

public class ConfigurationHelper
{
    public static SimulationSettings settings;
    public static Reaction[] reactions;
    public static AtomType[] atomTypes;
    
    public static TestingConfiguration tests;
    
    public static void configureSimulation()
    {
        settings = new SimulationSettings();
        reactions = new Reaction[CommanderWindow.reactions.size()];
        atomTypes = new AtomType[Reactions.ATOM_TYPE_COUNT];
        
        // Simulation settings
        
        settings.doSteric = CommanderWindow.simulateSteric;
        settings.do2D = CommanderWindow.simulate2D;
        settings.doV = CommanderWindow.simulateV;
        settings.doActive = CommanderWindow.simulateActive;
        settings.reactionStartTime = CommanderWindow.reactionStartTimeInput.value;
        settings.timeFactor = CommanderWindow.timeFactorInput.value;
        settings.temperature = CommanderWindow.temperatureInput.value;
        settings.width = CommanderWindow.widthInput.value;
        settings.height = CommanderWindow.heightInput.value;
        settings.depth = CommanderWindow.depthInput.value;
        
        // Atom types
        
        for (AtomType type : CommanderWindow.atomTypes)
        {
            int unique = type.unique;
            
            atomTypes[unique] = new AtomType();
            atomTypes[unique].name = type.name;
            atomTypes[unique].unique = type.unique;
            atomTypes[unique].initialCount = type.initialCount;
            atomTypes[unique].mass = type.mass;
            atomTypes[unique].radius = type.radius;
            atomTypes[unique].color = type.color;
            atomTypes[unique].currentCount = atomTypes[unique].initialCount;
        }
        
        // Reactions
        
        for (int index = 0; index < CommanderWindow.reactions.size(); index++)
        {
            Reaction reaction = CommanderWindow.reactions.get(index);
            reactions[index] = reaction.clone();

            int unique1 = reaction.reactant1_unique;
            reactions[index].mass1 = atomTypes[unique1].mass;
            reactions[index].radius1 = atomTypes[unique1].radius;
            atomTypes[unique1].reactantInReactions.add(reactions[index]);

            reactions[index] = reaction.clone();
            reactions[index].temperature = settings.temperature;

            if (reaction.reactant2 != null)
            {
                int unique2 = reaction.reactant2_unique;
                reactions[index].mass2 = atomTypes[unique2].mass;
                reactions[index].radius2 = atomTypes[unique2].radius;
                atomTypes[unique2].reactantInReactions.add(reactions[index]);
            }

            reactions[index].recalculate();
            reactions[index].Ea = reaction.activationEnergyInput.value;
        }
    }
    
    public static void configureTesting()
    {
        ConfigurationHelper.configureSimulation();
        
        tests = new TestingConfiguration();
        TestingConfiguration.TestingUnit previousUnit = null;
        for (int i = 0; i < CommanderWindow.testsTimeInputs.size(); i++)
        {
            TestingConfiguration.TestingUnit unit = new TestingConfiguration.TestingUnit();
            if (i == 0)
                tests.head = unit;
            else
                previousUnit.next = unit;
            previousUnit = unit;

            unit.time = CommanderWindow.testsTimeInputs.get(i).value;
            unit.repeat = CommanderWindow.testsRepeatInputs.get(i).value;
            unit.scale = CommanderWindow.testsScaleInputs.get(i).value;

            unit.settings = settings;
            unit.reactions = reactions;
            unit.atomTypes = atomTypes;
        }
    }
}
