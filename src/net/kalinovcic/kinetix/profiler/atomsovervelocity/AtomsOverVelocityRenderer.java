package net.kalinovcic.kinetix.profiler.atomsovervelocity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;
import net.kalinovcic.kinetix.physics.reaction.Reactions;

public class AtomsOverVelocityRenderer
{
    public AtomsOverVelocity profiler;
    
    public AtomsOverVelocityRenderer(AtomsOverVelocity profiler)
    {
        this.profiler = profiler;
    }
    
    public void displayColumns(SimulationState state, Graphics2D g2D, int[] typeOrder, double top, double bottom, boolean stacked)
    {
    	double maximum = 0;
    	if (stacked)
    	{
		    for (int column = 0; column < profiler.columnCount; column++)
		    {
		    	double localMaximum = 0;
	            for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
	            	localMaximum += profiler.averages[column][type];
	        	maximum = Math.max(maximum, localMaximum);
		    }
    	}
    	else
    	{
	        for (int type = 0; type < Reactions.ATOM_TYPE_COUNT; type++)
	        	maximum = Math.max(maximum, profiler.maximumAverage[type]);
    	}

        for (int column = 0; column < profiler.columnCount; column++)
        {
        	double currentHeight = 0;
            for (int typeIndex = 0; typeIndex < typeOrder.length; typeIndex++)
            {
            	int type = typeOrder[typeIndex];
	        	double average = profiler.averages[column][type];
	            if (average == 0) continue;
	            
	            double width = profiler.targetWidth / (double) profiler.columnCount;
	            double height = (bottom - top) * (average / maximum);
	            double x = column * width;
	            double y = bottom - height;
	            
	            if (stacked)
	            {
		            g2D.setColor(Atom.getType(state, type).color);
		            g2D.fill(new Rectangle2D.Double(x, y - currentHeight, width, height));
		            
		            g2D.translate(x + width, y - currentHeight);
		            g2D.rotate(Math.toRadians(-90.0));
		            
		            g2D.setColor(Color.BLACK);
		            g2D.drawString(String.format(Locale.US, "%.2f", average) + "", 0, 0);
		            
		            g2D.rotate(Math.toRadians(90.0));
		            g2D.translate(-x - width, -y + currentHeight);
		            
		            currentHeight += height;
	            }
	            else if (height > currentHeight)
            	{
		            g2D.setColor(Atom.getType(state, type).color);
		            g2D.fill(new Rectangle2D.Double(x, y, width, height - currentHeight));
		            
		            g2D.translate(x + width, y);
		            g2D.rotate(Math.toRadians(-90.0));
		            
		            g2D.setColor(Color.BLACK);
		            g2D.drawString(String.format(Locale.US, "%.2f", average) + "", 0, 0);
		            
		            g2D.rotate(Math.toRadians(90.0));
		            g2D.translate(-x - width, -y);
		            
		            currentHeight = height;
            	}
            	else
            	{
		            g2D.setColor(Atom.getType(state, type).color);
		            g2D.drawLine((int) Math.round(x), (int) Math.round(y), (int) Math.round(x + width), (int) Math.round(y));
            	}
            }
        }
    }
    
    public void render(SimulationState state, double deltaTime)
    {
        BufferedImage buffer = profiler.getBuffer(0);
        Graphics2D g2D = buffer.createGraphics();
        KinetixUI.setHints(g2D);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, profiler.getWidth(), profiler.getHeight());

        int headerHeight = 0;
        int textHeight = g2D.getFontMetrics().getHeight();
        g2D.setColor(Color.BLACK);
        g2D.drawString("Frame interval: " + profiler.frameInterval + " (" + String.format(Locale.US, "%.2f", profiler.frameInterval / 60.0) + "s)", 4, headerHeight += textHeight);
        g2D.drawString("Velocity interval: " + String.format(Locale.US, "%.2f", profiler.velocityInterval), 4, headerHeight += textHeight);
        g2D.drawString("Maximum velocity: " + profiler.maximumVelocity, 4, headerHeight += textHeight);
        g2D.drawString("# columns: " + (profiler.columnCount), 4, headerHeight += textHeight);
        headerHeight += textHeight;
        
        g2D.setFont(g2D.getFont().deriveFont(10.0f));

        int typeCount = 0;
        for (int i = 0; i < state.atomTypes.length; i++)
            if (state.atomTypes[i] != null)
                typeCount++;
        int[] typeOrder = new int[typeCount];
        typeCount = 0;
        for (int i = 0; i < state.atomTypes.length; i++)
            if (state.atomTypes[i] != null)
                typeOrder[typeCount++] = i;
        
        switch (profiler.displayOption)
        {
        case AtomsOverVelocity.DISPLAY_SINGLE:
        {
        	displayColumns(state, g2D, typeOrder, headerHeight, profiler.targetHeight, false);
        } break;
        case AtomsOverVelocity.DISPLAY_STACK:
        {
        	displayColumns(state, g2D, typeOrder, headerHeight, profiler.targetHeight, true);
        } break;
        case AtomsOverVelocity.DISPLAY_SEPARATED:
        {
        	double availableHeight = profiler.targetHeight - headerHeight;
        	double heightPerGraph = availableHeight / typeOrder.length;
        	
    		int[] singleType = new int[1];
        	for (int typeIndex = 0; typeIndex < typeOrder.length; typeIndex++)
        	{
        		double top = headerHeight + heightPerGraph * typeIndex;
        		double bottom = top + heightPerGraph;
        		
        		singleType[0] = typeOrder[typeIndex];
        		displayColumns(state, g2D, singleType, top, bottom, false);
        	}
        } break;
        }
        
        g2D.dispose();

        profiler.swapBuffers();
    }
}
