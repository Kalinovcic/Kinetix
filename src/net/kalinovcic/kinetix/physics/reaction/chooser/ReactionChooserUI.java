package net.kalinovcic.kinetix.physics.reaction.chooser;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.kalinovcic.kinetix.commander.CommanderWindow;
import net.kalinovcic.kinetix.imgui.Imgui;
import net.kalinovcic.kinetix.imgui.ImguiBounds;
import net.kalinovcic.kinetix.imgui.ImguiVerticalLayout;
import net.kalinovcic.kinetix.physics.reaction.Reaction;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class ReactionChooserUI extends Imgui
{
    private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
    static
    {
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
    }
    
    private static DecimalFormat normal = new DecimalFormat();
    static
    {
        normal.setDecimalFormatSymbols(otherSymbols);
        normal.setGroupingUsed(false);
    }
    
    private static DecimalFormat rounded = new DecimalFormat();
    static
    {
        rounded.setDecimalFormatSymbols(otherSymbols);
        rounded.setMinimumFractionDigits(1);
        rounded.setMaximumFractionDigits(1);
        rounded.setGroupingUsed(false);
    }
    
    private static DecimalFormat scientific = new DecimalFormat();
    static
    {
        scientific.setDecimalFormatSymbols(otherSymbols);
        scientific.applyPattern("0.##E0#");
        scientific.setMinimumFractionDigits(1);
        scientific.setMaximumFractionDigits(1);
        scientific.setGroupingUsed(false);
    }

    private static final String[] SORT_NAMES = new String[] { "Don't sort", "Eₐ ▲", "Eₐ ▼" };
    private static final int SORT_NONE = 0;
    private static final int SORT_ENERGY_INCREASING = 1;
    private static final int SORT_ENERGY_DECREASING = 2;
    private static final int SORT_OPTION_COUNT = 3;
    private int sortOption = 0;
    
    private boolean[] selection = new boolean[Reactions.reactions.size()];
    
    private float scrollY = 0;

    private void scroll()
    {
        if (context.mouseVerticalScrollDelta != 0)
            scrollY -= context.mouseVerticalScrollDelta * (BUTTON_HEIGHT + PADDING_VERTICAL);
        if (scrollY > 0) scrollY = 0;
    }
    
    @Override
    public void update()
    {
        scroll();

        if (doButton(SORT_NAMES[sortOption], new Rectangle2D.Float(0, 0, 320 + 10 + CHECKBOX_HEIGHT + 2 * PADDING_HORIZONTAL, BUTTON_HEIGHT), true))
        {
            sortOption = (sortOption + 1) % SORT_OPTION_COUNT;
        }
        
        beginRow();
        doSpace(320, 0);
        doSpace(10, 0);
        doSpace(CHECKBOX_HEIGHT, 0);
        doSpace(25, 0);
        /*doLabel("M₁", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("M₂", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("r₁", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("r₂", 45.0f, 0.5f, BOLD_FONT, null);*/
        doLabel("T(raspon)", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("A(eksp)", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("n", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("Eₐ", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("red", 75.0f, 0.5f, BOLD_FONT, null);
        endRow();
        
        beginRow();
        doSpace(320, 0);
        doSpace(10, 0);
        doSpace(CHECKBOX_HEIGHT, 0);
        doSpace(25, 0);
        /*doLabel("[g⁄mol]", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("[g⁄mol]", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("[Å]", 45.0f, 0.5f, BOLD_FONT, null);
        doLabel("[Å]", 45.0f, 0.5f, BOLD_FONT, null);*/
        doLabel("[K]", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("[dm³⁄mol⁄s]", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("[1]", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("[kJ⁄mol]", 75.0f, 0.5f, BOLD_FONT, null);
        doLabel("", 75.0f, 0.5f, BOLD_FONT, null);
        endRow();
        
        doSpace(0, PADDING_VERTICAL * 2);
        
        List<Integer> sortedReactionIndices = new ArrayList<Integer>();
        for (int i = 0; i < Reactions.reactions.size(); i++)
            sortedReactionIndices.add(i);
        sortedReactionIndices.sort(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer o1, Integer o2)
            {
                switch (sortOption)
                {
                case SORT_NONE:              return o1.compareTo(o2);
                case SORT_ENERGY_INCREASING: return  Double.compare(Reactions.reactions.get(o1).Ea, Reactions.reactions.get(o2).Ea); 
                case SORT_ENERGY_DECREASING: return -Double.compare(Reactions.reactions.get(o1).Ea, Reactions.reactions.get(o2).Ea);
                default: return 0;
                }
            }
        });

        g.draw(new Line2D.Float(0, BUTTON_HEIGHT * 2 - 6, context.bounds.width, BUTTON_HEIGHT * 2 - 6));
        
        popLayout();
        pushLayout(new ImguiVerticalLayout());
        pushBounds(new ImguiBounds(0, BUTTON_HEIGHT * 2, context.bounds.width, context.bounds.height - BUTTON_HEIGHT * 2));
        context.layout.y += scrollY;
        
        for (Integer reactionIndex : sortedReactionIndices)
        {
            Reaction reaction = Reactions.reactions.get(reactionIndex);
            
            String formula = reaction.reactant1 + " + " + reaction.reactant2 + " → " + reaction.product1 + " + " + reaction.product2;
            beginRow();
            doLabel(formula, 320.0f, 1.0f, SLIGHTLY_BIGGER_FONT, null);
            doSpace(10, 0);
            selection[reactionIndex] = doCheckbox("", CHECKBOX_HEIGHT, CHECKBOX_HEIGHT, selection[reactionIndex]);
            doSpace(25, 0);
            /*doLabel(normal.format(reaction.mass1), 45.0f, 0.5f, FONT, null);
            doLabel(normal.format(reaction.mass2), 45.0f, 0.5f, FONT, null);
            doLabel(normal.format(reaction.radius1), 45.0f, 0.5f, FONT, null);
            doLabel(normal.format(reaction.radius2), 45.0f, 0.5f, FONT, null);*/
            doLabel(normal.format(reaction.t_low) + " − " + normal.format(reaction.t_high), 75.0f, 0.5f, FONT, null);
            doLabel(scientific.format(reaction.A_exp).replace("E", " (+") + ")", 75.0f, 0.5f, FONT, null);
            doLabel(normal.format(reaction.n), 75.0f, 0.5f, FONT, null);
            doLabel(rounded.format(reaction.Ea), 75.0f, 0.5f, FONT, null);
            doLabel(Integer.toString(reaction.red), 75.0f, 0.5f, FONT, null);
            endRow();
        }
        
        popBounds();
    }
    
    @Override
    public void onClose()
    {
        int selectedCount = 0;
        for (int index = 0; index < selection.length; index++)
            if (selection[index])
                selectedCount++;

        if (selectedCount == 0) return;
        
        Reaction[] reactions = new Reaction[selectedCount];
        int reactionIndex = 0;
        for (int index = 0; index < selection.length; index++)
            if (selection[index])
                reactions[reactionIndex++] = Reactions.reactions.get(index);
        
        CommanderWindow.setReactionList(reactions);
    }
}
