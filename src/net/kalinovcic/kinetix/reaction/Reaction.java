package net.kalinovcic.kinetix.reaction;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Reaction
{
	public static final double BOLTZMANN = 1.38064852 * 1e-23;
	public static final double DALTON = 1.660539040 * 1e-27;
	public static final double IDEAL_GAS = 8.3144598;
	public static final double AVOGADRO = 6.022140857 * 1e+23;
	
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
	public double preExponentialFactor_experimental;// A(eksp)
	public double b;								// B
	public double ratio;							// Ea/R
	
	public double reducedMass;						// Î¼
	public double sigma;							// Ï?
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
	
	public String toString()
	{
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		otherSymbols.setDecimalSeparator('.');
		otherSymbols.setGroupingSeparator(',');
		
		DecimalFormat normal = new DecimalFormat();
		normal.setDecimalFormatSymbols(otherSymbols);
		normal.setGroupingUsed(false);
		
		DecimalFormat scientific = new DecimalFormat();
		scientific.setDecimalFormatSymbols(otherSymbols);
		scientific.applyPattern("0.##E0#");
		scientific.setGroupingUsed(false);
		
		return "Reaction(" + reactant1 + ", " + reactant2 + ", "
						   + product1 + ", " + product2 + ", "
						   + normal.format(mass1) + ", " + normal.format(mass2) + ", "
						   + normal.format(radius1) + ", " + normal.format(radius2) + ", "
						   + normal.format(temperatureRange_low) + "-" + normal.format(temperatureRange_high) + ", "
						   + scientific.format(preExponentialFactor_experimental) + ", "
						   + normal.format(b) + ", "
						   + normal.format(ratio) + ", "
						   + scientific.format(reducedMass) + ", "
						   + scientific.format(sigma) + ", "
						   + normal.format(activationEnergy) + ", "
						   + normal.format(temperature) + ", "
						   + normal.format(relativeSpeed) + ", "
						   + normal.format(x) + ", "
						   + scientific.format(expMinusX) + ", "
						   + scientific.format(preExponentialFactor_theoretical) + ", "
						   + scientific.format(speedCoefficient_experimental) + ", "
						   + scientific.format(speedCoefficient_theoretical) + ", "
						   + scientific.format(steric) + ", "
						   + normal.format(concentration1) + ", "
						   + normal.format(concentration2) + ", "
						   + scientific.format(rate_experimental) + ", "
						   + scientific.format(rate_theoretical) + ")";
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
}
