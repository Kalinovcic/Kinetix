package net.kalinovcic.kinetix.imgui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class ImguiDoubleInput extends ImguiInput
{
    private static final DecimalFormatSymbols OTHER_SYMBOLS = new DecimalFormatSymbols(Locale.US);
    static
    {
        OTHER_SYMBOLS.setDecimalSeparator('.');
        OTHER_SYMBOLS.setGroupingSeparator(',');
    }
    
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat();
    static
    {
        NUMBER_FORMAT.setDecimalFormatSymbols(OTHER_SYMBOLS);
        NUMBER_FORMAT.setGroupingUsed(false);
    }

    public double value;
    public double min;
    public double max;
    
    public ImguiDoubleInput()
    {
        value = getInitial();
        min = getMinimum();
        max = getMaximum();
        text = format();
    }
    
    public ImguiDoubleInput(double initial)
    {
        value = initial;
        min = getMinimum();
        max = getMaximum();
        if (value < min) value = min;
        if (value > max) value = max;
        text = format();
    }
    
    public ImguiDoubleInput(double initial, double minValue, double maxValue)
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
            value = NUMBER_FORMAT.parse(text).doubleValue();
            if (value < min) value = min;
            if (value > max) value = max;
            assign(value);
        }
        catch (ParseException ex) {}
        return format();
    }
    
    private String format()
    {
        return NUMBER_FORMAT.format(value);
    }
    
    public double getInitial() { return 0.0; }
    public double getMinimum() { return -Double.MAX_VALUE; }
    public double getMaximum() { return Double.MAX_VALUE; }
    public void assign(double value) {}
}
