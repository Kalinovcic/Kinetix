package net.kalinovcic.kinetix.imgui;

import java.text.NumberFormat;
import java.text.ParseException;

public class ImguiIntegerInput extends ImguiInput
{
    public static final NumberFormat INTEGER_FORMAT = NumberFormat.getIntegerInstance();
    static
    {
        INTEGER_FORMAT.setGroupingUsed(false);
    }
    
    public int value;
    public int min;
    public int max;
    
    public ImguiIntegerInput()
    {
        value = getInitial();
        min = getMinimum();
        max = getMaximum();
        text = format();
    }
    
    public ImguiIntegerInput(int initial)
    {
        value = initial;
        min = getMinimum();
        max = getMaximum();
        if (value < min) value = min;
        if (value > max) value = max;
        text = format();
    }
    
    public ImguiIntegerInput(int initial, int minValue, int maxValue)
    {
        value = initial;
        min = minValue;
        max = maxValue;
        if (value < min) value = min;
        if (value > max) value = max;
        text = format();
    }
    
    @Override
    public String accept(String text)
    {
        try
        {
            if (text.length() == 0) text = "0";
            value = INTEGER_FORMAT.parse(text).intValue();
            if (value < min) value = min;
            if (value > max) value = max;
            assign(value);
        }
        catch (ParseException ex) {}
        return format();
    }
    
    private String format()
    {
        return INTEGER_FORMAT.format(value);
    }

    public int getInitial() { return 0; }
    public int getMinimum() { return Integer.MIN_VALUE; }
    public int getMaximum() { return Integer.MAX_VALUE; }
    public void assign(int value) {}
}
