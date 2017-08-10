package net.kalinovcic.kinetix.physics.reaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import net.kalinovcic.kinetix.imgui.ImguiDoubleInput;

public class Reaction
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
	
	private static DecimalFormat scientific = new DecimalFormat();
	static
	{
		scientific.setDecimalFormatSymbols(otherSymbols);
		scientific.applyPattern("0.##E0#");
		scientific.setGroupingUsed(false);
	}

	public static final double BOLTZMANN = 1.38064852 * 1e-23;
	public static final double DALTON = 1.660539040 * 1e-27;
	public static final double IDEAL_GAS = 8.3144598;
	public static final double AVOGADRO = 6.022140857 * 1e+23;
	
	public static int akUnit;
	public static String[] akUnits = { "dm³⁄mol⁄s", "cm³⁄mol⁄s", "dm³⁄č⁄s", "cm³⁄č⁄s" };
	public static double[] akFactors = { 1.0, 1000.0, 1.0 / AVOGADRO, 1000.0 / AVOGADRO };
	
	public static int cUnit;
	public static String[] cUnits = { "mol⁄dm³", "č⁄dm³", "č⁄(1000Å)³" };
	public static double[] cFactors = { 1.0, AVOGADRO, AVOGADRO / 1e18 };

	public static int vUnit;
	public static String[] vUnits = { "mol⁄dm³⁄s", "mol⁄cm³⁄s", "č⁄dm³⁄s", "č⁄cm³⁄s", "č⁄(1000Å)³⁄ns" };
	public static double[] vFactors = { 1.0, 1.0 / 1000.0, AVOGADRO, AVOGADRO / 1000.0, AVOGADRO / 1e27 };
	
	public String reactant1;
	public String reactant2;
	public String product1;
	public String product2;
	public double mass1;
	public double mass2;
	public double radius1;
	public double radius2;
	public double t_low;
	public double t_high;
	public double A_exp;
	public double n;
    public double Ea;
    public int    red;
	
	public double reducedMass;
	public double sigma;
	public double temperature;
	public double relativeSpeed;
	public double x;
	public double expMinusX;
	public double A_teor;
	public double speedCoefficient_experimental;
	public double speedCoefficient_theoretical;
	public double steric;
	public double concentration1;
	public double concentration2;
	public double rate_experimental;
	public double rate_theoretical;

    public int reactant1_unique;
    public int reactant2_unique;
    public int product1_unique;
    public int product2_unique;
	public double _stericRemaining;
	
	public ImguiDoubleInput activationEnergyInput;
	
	public static String partName(int i)
	{
		i--;
		switch (i)
		{
		case 0:  return "R₁";
		case 1:  return "R₂";
		case 2:  return "P₁";
		case 3:  return "P₂";
		case 4:  return "M₁ [g⁄mol]";
		case 5:  return "M₂ [g⁄mol]";
		case 6:  return "r₁ [Å]";
		case 7:  return "r₂ [Å]";
		case 8:  return "T(raspon) [K]";
		case 9:  return "A(eksp) [" + akUnits[akUnit] + "]";
		case 10: return "n [1]";
        case 11: return "Eₐ [kJ⁄mol]";
        case 12: return "red";
		default: return null;
		}
	}
	
	public String partToString(int i)
	{
		i--;
		switch (i)
		{
		case 0:  return reactant1;
		case 1:  return reactant2;
		case 2:  return product1;
		case 3:  return product2;
		case 4:  return normal.format(mass1);
		case 5:  return normal.format(mass2);
		case 6:  return normal.format(radius1);
		case 7:  return normal.format(radius2);
		case 8:  return normal.format(t_low) + "-" + normal.format(t_high);
		case 9:  return scientific.format(A_exp * akFactors[akUnit]);
		case 10: return normal.format(n);
		case 11: return normal.format(Ea);
		case 12: return Integer.toString(red);
		default: return null;
		}
	}
	
	public String getFormula()
	{
	    String formula = reactant1;
        if (reactant2 != null)
            formula += " + " + reactant2;
        formula += " → " + product1;
        if (product2 != null)
            formula += " + " + product2;
        return formula;
	}
	
	public String getSimpleFormula()
	{
        String formula = Reactions.toSimpleName(reactant1);
        if (reactant2 != null)
            formula += " + " + Reactions.toSimpleName(reactant2);
        formula += " → " + Reactions.toSimpleName(product1);
        if (product2 != null)
            formula += " + " + Reactions.toSimpleName(product2);
        return formula;
	}
	
	public String toString()
	{
		String full = "Reaction(";
		for (int i = 0; i <= 12; i++)
		{
			if (i != 0) full += ", ";
			full += partToString(i);
		}
		return full;
	}
	
	public void recalculate()
	{
		reducedMass = (mass1 * mass2) / (mass1 + mass2) * DALTON;
		sigma = (radius1 + radius2)*(radius1 + radius2) * Math.PI * 1e-20;
		
		relativeSpeed = Math.sqrt((8 * BOLTZMANN * temperature) / (Math.PI * reducedMass));
		x = (Ea * 1000) / (IDEAL_GAS * temperature);
		expMinusX = Math.exp(-x);
		A_teor = sigma * relativeSpeed * AVOGADRO * 1000;
		speedCoefficient_experimental = A_exp * Math.pow(temperature, n) * expMinusX;
		speedCoefficient_theoretical = A_teor * expMinusX;
		steric = speedCoefficient_experimental / speedCoefficient_theoretical;

		rate_experimental = speedCoefficient_experimental * concentration1 * concentration2;
		rate_theoretical = speedCoefficient_theoretical * concentration1 * concentration2;
	}
	
	public Reaction clone()
	{
	    Reaction nn = new Reaction();
	    nn.reactant1 = reactant1;
	    nn.reactant2 = reactant2;
	    nn.product1 = product1;
	    nn.product2 = product2;
	    nn.mass1 = mass1;
	    nn.mass2 = mass2;
	    nn.radius1 = radius1;
	    nn.radius2 = radius2;
	    nn.t_low = t_low;
	    nn.t_high = t_high;
	    nn.A_exp = A_exp;
	    nn.n = n;
        nn.Ea = Ea;
        nn.red = red;
        
	    nn.reducedMass = reducedMass;
	    nn.sigma = sigma;
	    nn.temperature = temperature;
	    nn.relativeSpeed = relativeSpeed;
	    nn.x = x;
	    nn.expMinusX = expMinusX;
	    nn.A_teor = A_teor;
	    nn.speedCoefficient_experimental = speedCoefficient_experimental;
	    nn.speedCoefficient_theoretical = speedCoefficient_theoretical;
	    nn.steric = steric;
	    nn.concentration1 = concentration1;
	    nn.concentration2 = concentration2;
	    nn.rate_experimental = rate_experimental;
	    nn.rate_theoretical = rate_theoretical;
	    
        nn.reactant1_unique = reactant1_unique;
        nn.reactant2_unique = reactant2_unique;
        nn.product1_unique = product1_unique;
        nn.product2_unique = product2_unique;
	    return nn;
	}
}
