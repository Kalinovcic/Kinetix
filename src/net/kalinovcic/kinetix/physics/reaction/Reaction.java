package net.kalinovcic.kinetix.physics.reaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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
	
	public String reactant1;						// R1
	public String reactant2;						// R2
	public String product1;							// P1
	public String product2;							// P2
	public double mass1;							// M1
	public double mass2;							// M2
	public double radius1;							// r1
	public double radius2;							// r2
	public double temperatureRange_low;				// T(raspon)
	public double temperatureRange_high;			// T(raspon)
    public boolean temperatureRange_known;
	public double preExponentialFactor_experimental;// A(eksp)
	public double b;								// B
	public double ratio;							// Ea/R
	
	public double reducedMass;						// μ
	public double sigma;							// σ
	public double activationEnergy;					// Ea
	
	public double temperature;						// T
	
	public double relativeSpeed;					// U(rel)
	public double x;								// Ea/RT
	public double expMinusX;						// e-Ea/RT
	public double preExponentialFactor_theoretical;	// A(teor)
	public double speedCoefficient_experimental;	// k(eksp)
	public double speedCoefficient_theoretical;		// k(teor)
	public double steric;							// k(eksp)/k(teor)
	
	public double concentration1;					// c1
	public double concentration2;					// c2
	
	public double rate_experimental;				// v(eksp)
	public double rate_theoretical;					// v(teor)
	
	public double _stericRemaining;
	
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
		case 10: return "B [1]";
		case 11: return "Eₐ⁄R [K]";
		case 12: return "μ [kg]";
		case 13: return "σ [m²]";
		case 14: return "Eₐ [kJ⁄mol]";
		case 15: return "T [K]";
		case 16: return "U(rel) [m⁄s]";
		case 17: return "Eₐ⁄RT [1]";
		case 18: return "exp(-Eₐ⁄RT) [1]";
		case 19: return "A(teor) [" + akUnits[akUnit] + "]";
		case 20: return "k(eksp) [" + akUnits[akUnit] + "]";
		case 21: return "k(teor) [" + akUnits[akUnit] + "]";
		case 22: return "k(eksp)⁄k(teor) [1]";
		case 23: return "c₁ [" + cUnits[cUnit] + "]";
		case 24: return "c₂ [" + cUnits[cUnit] + "]";
		case 25: return "v(eksp) [" + vUnits[vUnit] + "]";
		case 26: return "v(teor) [" + vUnits[vUnit] + "]";
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
		case 8:  return temperatureRange_known ? (normal.format(temperatureRange_low) + "-" + normal.format(temperatureRange_high)) : "-";
		case 9:  return scientific.format(preExponentialFactor_experimental * akFactors[akUnit]);
		case 10: return normal.format(b);
		case 11: return normal.format(ratio);
		case 12: return scientific.format(reducedMass);
		case 13: return scientific.format(sigma);
		case 14: return normal.format(activationEnergy);
		case 15: return normal.format(temperature);
		case 16: return normal.format(relativeSpeed);
		case 17: return normal.format(x);
		case 18: return scientific.format(expMinusX);
		case 19: return scientific.format(preExponentialFactor_theoretical * akFactors[akUnit]);
		case 20: return scientific.format(speedCoefficient_experimental * akFactors[akUnit]);
		case 21: return scientific.format(speedCoefficient_theoretical * akFactors[akUnit]);
		case 22: return scientific.format(steric);
		case 23: return normal.format(concentration1 * cFactors[cUnit]);
		case 24: return normal.format(concentration2 * cFactors[cUnit]);
		case 25: return ((vUnit == 4) ? normal : scientific).format(rate_experimental * vFactors[vUnit]);
		case 26: return ((vUnit == 4) ? normal : scientific).format(rate_theoretical * vFactors[vUnit]);
		default: return null;
		}
	}
	
	public String toString()
	{
		String full = "Reaction(";
		for (int i = 0; i < 27; i++)
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
		activationEnergy = ratio * (IDEAL_GAS / 1000);
		
		relativeSpeed = Math.sqrt((8 * BOLTZMANN * temperature) / (Math.PI * reducedMass));
		x = (activationEnergy * 1000) / (IDEAL_GAS * temperature);
		expMinusX = Math.exp(-x);
		preExponentialFactor_theoretical = sigma * relativeSpeed * AVOGADRO * Math.exp(0.5) * 1000;
		speedCoefficient_experimental = preExponentialFactor_experimental * Math.pow(temperature, b) * expMinusX;
		speedCoefficient_theoretical = preExponentialFactor_theoretical * expMinusX;
		steric = speedCoefficient_experimental / speedCoefficient_theoretical;

		rate_experimental = speedCoefficient_experimental * concentration1 * concentration2;
		rate_theoretical = speedCoefficient_theoretical * concentration1 * concentration2;
	}
	
	public Reaction clone()
	{
	    Reaction n = new Reaction();
	    n.reactant1 = reactant1;
	    n.reactant2 = reactant2;
	    n.product1 = product1;
	    n.product2 = product2;
	    n.mass1 = mass1;
	    n.mass2 = mass2;
	    n.radius1 = radius1;
	    n.radius2 = radius2;
	    n.temperatureRange_low = temperatureRange_low;
	    n.temperatureRange_high = temperatureRange_high;
	    n.temperatureRange_known = temperatureRange_known;
	    n.preExponentialFactor_experimental = preExponentialFactor_experimental;
	    n.b = b;
	    n.ratio = ratio;
	    n.reducedMass = reducedMass;
	    n.sigma = sigma;
	    n.activationEnergy = activationEnergy;
	    n.temperature = temperature;
	    n.relativeSpeed = relativeSpeed;
	    n.x = x;
	    n.expMinusX = expMinusX;
	    n.preExponentialFactor_theoretical = preExponentialFactor_theoretical;
	    n.speedCoefficient_experimental = speedCoefficient_experimental;
	    n.speedCoefficient_theoretical = speedCoefficient_theoretical;
	    n.steric = steric;
	    n.concentration1 = concentration1;
	    n.concentration2 = concentration2;
	    n.rate_experimental = rate_experimental;
	    n.rate_theoretical = rate_theoretical;
	    return n;
	}
}
