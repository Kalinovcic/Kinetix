package net.kalinovcic.kinetix.commander;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.kalinovcic.kinetix.Kinetix;
import net.kalinovcic.kinetix.MainWindow;
import net.kalinovcic.kinetix.imgui.Imgui;
import net.kalinovcic.kinetix.imgui.ImguiBounds;
import net.kalinovcic.kinetix.imgui.ImguiDoubleInput;
import net.kalinovcic.kinetix.imgui.ImguiFrame;
import net.kalinovcic.kinetix.imgui.ImguiIntegerInput;
import net.kalinovcic.kinetix.imgui.ImguiVerticalLayout;
import net.kalinovcic.kinetix.physics.AtomType;
import net.kalinovcic.kinetix.physics.SimulationSeries;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;
import net.kalinovcic.kinetix.physics.reaction.chooser.ReactionChooserWindow;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;
import net.kalinovcic.kinetix.test.TestAll;
import net.kalinovcic.kinetix.test.TestingThread;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class CommanderWindow extends ImguiFrame
{
    private static final long serialVersionUID = 1L;
    
    public static String simulationTime = "Time: 0 s";

    private static MainWindow myMainWindow;
    public static List<Reaction> reactions;
    public static List<AtomType> atomTypes;

    public static boolean simulateSteric;
    public static boolean simulate2D;
    public static boolean simulateV;
    
    public static ImguiDoubleInput reactionStartTimeInput = new ImguiDoubleInput(0, 0, Double.MAX_VALUE);
    public static ImguiDoubleInput timeFactorInput = new ImguiDoubleInput(1, Double.MIN_VALUE, Double.MAX_VALUE);
    public static ImguiDoubleInput temperatureInput = new ImguiDoubleInput(3600, 0, Double.MAX_VALUE);

    public static ImguiIntegerInput widthInput = new ImguiIntegerInput(600, 1, Integer.MAX_VALUE);
    public static ImguiIntegerInput heightInput = new ImguiIntegerInput(600, 1, Integer.MAX_VALUE);
    public static ImguiIntegerInput depthInput = new ImguiIntegerInput(600, 1, Integer.MAX_VALUE);
    
    public static final int NO_SERIES = 0;
    public static final int SERIES_TEMPERATURE = 1;
    public static final String[] SERIES_OPTION_NAMES = new String[] { "No series", "Series: temperature" };
    public static int seriesOption = 0;
    public static ImguiIntegerInput seriesCountInput = new ImguiIntegerInput(1, 1, Integer.MAX_VALUE);
    public static ImguiDoubleInput[] seriesTemperatureInputs;

    public static final int DONT_REPEAT = 0;
    public static final int REPEAT_WITH_END_TIME = 1;
    public static final int REPEAT_WITH_HALF_LIFE = 2;
    public static final String[] REPEAT_OPTION_NAMES = new String[] { "No repeat", "Repeat: end time", "Repeat: half-life" };
    public static int repeatOption = 0;
    
    public static ImguiIntegerInput seriesRepeatInput = new ImguiIntegerInput(1, 1, Integer.MAX_VALUE);
    public static ImguiDoubleInput seriesEndTimeInput = new ImguiDoubleInput(0, Double.MIN_VALUE, Double.MAX_VALUE);
    public static ImguiIntegerInput seriesHalfLifeInput = new ImguiIntegerInput(10, 0, Integer.MAX_VALUE);
    public static int seriesHalfLifeAtom;

    public static List<ImguiDoubleInput> testsTimeInputs = new ArrayList<ImguiDoubleInput>();
    public static List<ImguiIntegerInput> testsRepeatInputs = new ArrayList<ImguiIntegerInput>();
    public static List<ImguiDoubleInput> testsScaleInputs = new ArrayList<ImguiDoubleInput>();
    static
    {
        double[] times = new double[] { 20, 10, 1, 0.5, 0.2, 0.1 };
        int[] repeats = new int[] { 5, 10, 100, 200, 500, 1000 };
        double[] scales = new double[] { 0.05, 0.01, 1, 2, 5, 10 };
        for (double time : times) testsTimeInputs.add(new ImguiDoubleInput(time, Double.MIN_VALUE, Double.MAX_VALUE));
        for (int repeat : repeats) testsRepeatInputs.add(new ImguiIntegerInput(repeat, 1, Integer.MAX_VALUE));
        for (double scale : scales) testsScaleInputs.add(new ImguiDoubleInput(scale, Double.MIN_VALUE, Double.MAX_VALUE));
    }
    
    public CommanderWindow(MainWindow mainWindow)
    {
        super(mainWindow, "Commander", 20, 20, 400, 700, false, new CommanderUI());
        myMainWindow = mainWindow;
    }
    
    private static AtomType addAtom(String name, int initialCount)
    {
        for (AtomType type : atomTypes)
            if (type.name.equals(name))
            {
                if (type.initialCount < initialCount)
                    type.initialCount = initialCount;
                return type;
            }
        
        AtomType type = new AtomType();
        type.name = name;
        type.unique = Reactions.uniqueAtoms.get(name);
        type.initialCount = initialCount;
        type.mass = Reactions.findMass(name);
        type.radius = Reactions.findRadius(name);
        type.color = Color.BLACK;

        type.initialCountInput = new ImguiIntegerInput() {
            @Override public int getInitial() { return type.initialCount; }
            @Override public int getMinimum() { return 0; }
            @Override public void assign(int value) { type.initialCount = value; }
        };

        type.massInput = new ImguiDoubleInput() {
            @Override public double getInitial() { return type.mass; }
            @Override public double getMinimum() { return 0; }
            @Override public void assign(double value) { type.mass = value; }
        };
        
        type.radiusInput = new ImguiDoubleInput() {
            @Override public double getInitial() { return type.radius; }
            @Override public double getMinimum() { return 0; }
            @Override public void assign(double value) { type.radius = value; }
        };
        
        atomTypes.add(type);
        
        return type;
    }
    
    public static void setReactionList(Reaction[] newReactions)
    {
        if (newReactions == null || newReactions.length == 0)
        {
            reactions = null;
            atomTypes = null;
            return;
        }
        
        reactions = new ArrayList<Reaction>();
        atomTypes = new ArrayList<AtomType>();
        for (int i = 0; i < newReactions.length; i++)
            reactions.add(newReactions[i].clone());
        
        for (Reaction reaction : reactions)
        {
            addAtom(reaction.reactant1, 100).reactantInReactions.add(reaction);
            addAtom(reaction.reactant2, 100).reactantInReactions.add(reaction);
            reaction.activationEnergyInput = new ImguiDoubleInput(reaction.activationEnergy, 0, Double.MAX_VALUE);
        }
        for (Reaction reaction : reactions)
        {
            addAtom(reaction.product1, 0);
            addAtom(reaction.product2, 0);
        }
        for (int i = 0; i < atomTypes.size(); i++)
        {
            float hue = (i / (float)(atomTypes.size() - 1)) * 0.6f;
            atomTypes.get(i).color = new Color(Color.HSBtoRGB(hue, 1.0f, 0.75f));
        }
    }
    
    public static class CommanderUI extends Imgui
    {
        private static final String[] tabNames = { "Simulation", "Testing" };
        private int currentTab;
        
        @Override
        public void update()
        {
            currentTab = doTabs(tabNames, currentTab);
            switch (currentTab)
            {
            case 0: updateSimulationTab(); break;
            case 1: updateTestingTab(); break;
            }
        }
        
        private void updateSimulationTab()
        {
            boolean reactionsReady = reactions != null && atomTypes != null;
            
            pushBounds(new ImguiBounds(0, 0, context.bounds.width, 320 - PADDING_VERTICAL));
            if (reactionsReady)
            {
                for (Reaction reaction : reactions)
                {
                    String formula = reaction.reactant1 + " + " + reaction.reactant2 + " → " + reaction.product1 + " + " + reaction.product2;
                    beginRow();
                    doLabel(formula, (context.bounds.width - PADDING_HORIZONTAL) * 0.6f, 0.5f, FONT, null);
                    doInput("Eₐ:", (context.bounds.width - PADDING_HORIZONTAL) * 0.4f, reaction.activationEnergyInput, "kJ ⁄ mol");
                    endRow();
                }
                doSpace(0, 4);
                
                beginRow();
                doSpace(columnWidth(4), 0);
                doLabel("n", columnWidth(4), 0.5f, FONT, null);
                doLabel("M", columnWidth(4), 0.5f, FONT, null);
                doLabel("r", columnWidth(4), 0.5f, FONT, null);
                endRow();
                for (AtomType type : atomTypes)
                {
                    beginRow();
                    
                    textOutline = TEXT_OUTLINE;
                    doLabel(type.name, columnWidth(4), 0.5f, BOLD_FONT, type.color);
                    textOutline = null;
                    
                    doInput("", columnWidth(4), type.initialCountInput);
                    doInput("", columnWidth(4), type.massInput, "g ⁄ mol");
                    doInput("", columnWidth(4), type.radiusInput, "Å");
                    endRow();
                }
            }
            else
            {
                doSpace(0, 40);
                doLabel("No reactions selected.", columnWidth(1), 0.5f, FONT, null);
            }
            popBounds();
            popLayout();
            pushLayout(new ImguiVerticalLayout());
            doSpace(0, 320);

            beginRow();
            simulateSteric = doCheckbox("Steric", columnWidth(3), 0, simulateSteric);
            simulate2D = doCheckbox("2D", columnWidth(3), 0, simulate2D);
            simulateV = doCheckbox("V", columnWidth(3), 0, simulateV);
            endRow();

            beginRow();
            doInput("x:", columnWidth(1), reactionStartTimeInput, "s");
            endRow();
            
            beginRow();
            doInput("T factor:", columnWidth(2), timeFactorInput);
            doInput("Temp:", columnWidth(2), temperatureInput, "K");
            endRow();
            
            beginRow();
            if (doButton(REPEAT_OPTION_NAMES[repeatOption], columnWidth(2), 0))
                repeatOption = (repeatOption + 1) % 3;
            if (repeatOption != DONT_REPEAT)
                doInput("Repeat:", columnWidth(2), seriesRepeatInput);
            endRow();

            beginRow();
            if (repeatOption == DONT_REPEAT)
            {
                doSpace(0, BUTTON_HEIGHT);
            }
            else if (repeatOption == REPEAT_WITH_END_TIME)
            {
                doInput("End time:", columnWidth(1), seriesEndTimeInput);
            }
            else if (repeatOption == REPEAT_WITH_HALF_LIFE)
            {
                doInput("Half-life:", columnWidth(2), seriesHalfLifeInput);
                if (atomTypes != null && atomTypes.size() > 0)
                    if (doButton(atomTypes.get(seriesHalfLifeAtom).name, columnWidth(2), 0))
                        seriesHalfLifeAtom = (seriesHalfLifeAtom + 1) % atomTypes.size();
            }
            endRow();
            
            beginRow();
            if (doButton(SERIES_OPTION_NAMES[seriesOption], columnWidth(2), 0))
                seriesOption = (seriesOption + 1) % 2;
            if (seriesOption != NO_SERIES)
                doInput("Series:", columnWidth(2), seriesCountInput);
            endRow();

            beginRow();
            if (seriesOption == NO_SERIES)
            {
                doSpace(0, BUTTON_HEIGHT);
            }
            else if (seriesOption == SERIES_TEMPERATURE)
            {
                if (seriesTemperatureInputs == null || seriesTemperatureInputs.length != seriesCountInput.value)
                {
                    seriesTemperatureInputs = new ImguiDoubleInput[seriesCountInput.value];
                    for (int i = 0; i < seriesCountInput.value; i++)
                        seriesTemperatureInputs[i] = new ImguiDoubleInput(3600, 0, Double.MAX_VALUE);
                }
                
                for (int i = 0; i < seriesCountInput.value; i++)
                    doInput("", columnWidth(seriesCountInput.value), seriesTemperatureInputs[i]);
            }
            endRow();
            
            beginRow();
            doInput("W:", columnWidth(3), widthInput, "px");
            doInput("H:", columnWidth(3), heightInput, "px");
            doInput("D:", columnWidth(3), depthInput, "px");
            endRow();
            
            doLabel(simulationTime, columnWidth(1), 0.0f, BIG_FONT, null);

            if (doButton("Open reaction", columnWidth(1), 0))
            {
                new ReactionChooserWindow(myMainWindow);
            }
            
            if (doButton("Open profiler", columnWidth(1), 0))
            {
                new ProfilerWindow(myMainWindow);
            }
            
            if (doButton("Start", columnWidth(1), 0, reactionsReady))
            {
                ConfigurationHelper.configureSimulation();

                synchronized (Kinetix.STATE)
                {
                    Kinetix.STATE.settings = ConfigurationHelper.settings;
                    Kinetix.STATE.reactions = ConfigurationHelper.reactions;
                    Kinetix.STATE.atomTypes = ConfigurationHelper.atomTypes;
                    
                    SimulationSeries firstSeries = new SimulationSeries();
                    int seriesCount = (seriesOption == NO_SERIES) ? 1 : seriesCountInput.value;
                    SimulationSeries currentSeries = null;
                    for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++)
                    {
                        if (currentSeries == null)
                            currentSeries = firstSeries;
                        else
                        {
                            SimulationSeries series = new SimulationSeries();
                            currentSeries.next = series;
                            currentSeries = series;
                        }
                        
                        if (seriesOption == SERIES_TEMPERATURE)
                            currentSeries.temperature = seriesTemperatureInputs[seriesIndex].value;
                        else
                            currentSeries.temperature = temperatureInput.value;
                        
                        currentSeries.repeatRemaining = seriesRepeatInput.value;
                        currentSeries.endTime = Double.MAX_VALUE;
                        currentSeries.halfLife = Integer.MAX_VALUE;
                        if (repeatOption == REPEAT_WITH_END_TIME)
                        {
                            currentSeries.endTime = seriesEndTimeInput.value;
                        }
                        else if (repeatOption == REPEAT_WITH_HALF_LIFE)
                        {
                            currentSeries.halfLife = seriesHalfLifeInput.value;
                            currentSeries.halfLifeUnique = atomTypes.get(seriesHalfLifeAtom).unique;
                        }
                    }
                    
                    Kinetix.STATE.settings.temperature = firstSeries.temperature;
                    Kinetix.STATE.series = firstSeries;
                    
                    Kinetix.restart = true;
                }
            }
        }
        
        private void updateTestingTab()
        {
            float listHeight = context.bounds.height - 60;
            pushBounds(new ImguiBounds(0, 0, context.bounds.width, listHeight));
            
            context.bounds.width -= BUTTON_HEIGHT + PADDING_HORIZONTAL;
            beginRow();
            doLabel("time", columnWidth(3), 0.5f, FONT, null);
            doLabel("repeat", columnWidth(3), 0.5f, FONT, null);
            doLabel("scale", columnWidth(3), 0.5f, FONT, null);
            endRow();
            for (int i = 0; i < testsTimeInputs.size(); i++)
            {
                beginRow();
                doInput("", columnWidth(3), testsTimeInputs.get(i));
                doInput("", columnWidth(3), testsRepeatInputs.get(i));
                doInput("", columnWidth(3), testsScaleInputs.get(i));
                context.bounds.width += BUTTON_HEIGHT + PADDING_HORIZONTAL;
                if (doButton("-", BUTTON_HEIGHT, BUTTON_HEIGHT))
                {
                    testsTimeInputs.remove(i);
                    testsRepeatInputs.remove(i);
                    testsScaleInputs.remove(i);
                    i--;
                }
                context.bounds.width -= BUTTON_HEIGHT + PADDING_HORIZONTAL;
                endRow();
            }
            context.bounds.width += BUTTON_HEIGHT + PADDING_HORIZONTAL;
            if (doButton("+", columnWidth(1), 0))
            {
                testsTimeInputs.add(new ImguiDoubleInput(1, Double.MIN_VALUE, Double.MAX_VALUE));
                testsRepeatInputs.add(new ImguiIntegerInput(1, 1, Integer.MAX_VALUE));
                testsScaleInputs.add(new ImguiDoubleInput(1, Double.MIN_VALUE, Double.MAX_VALUE));
            }
            
            popBounds();
            popLayout();
            pushLayout(new ImguiVerticalLayout());
            doSpace(0, listHeight);

            boolean testingReady = reactions != null && atomTypes != null && reactions.size() >= 1;
            if (doButton("Start testing", columnWidth(1), 0, testingReady))
            {
                ConfigurationHelper.configureTesting();
                new TestingThread(myMainWindow, ConfigurationHelper.tests).start();
            }
            
            if (doButton("Test all", columnWidth(1), 0))
            {
                TestAll.testAll(myMainWindow);
            }
        }
    }
}