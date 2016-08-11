package net.kalinovcic.kinetix.profile.velocity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Locale;

import net.kalinovcic.kinetix.KinetixUI;
import net.kalinovcic.kinetix.KinetixWindow;
import net.kalinovcic.kinetix.physics.Atom;
import net.kalinovcic.kinetix.physics.SimulationState;

public class ProfileVelocityRenderer
{
    public KinetixWindow window;
    public ProfileVelocitySettings settings;
    
    public int[] columns = null;
    public int maximumColumnHeight = 0;
    
    public ProfileVelocityRenderer(KinetixWindow window, ProfileVelocitySettings settings)
    {
        this.window = window;
        this.settings = settings;
    }
    
    public void update(SimulationState state)
    {
        if (columns == null || columns.length != settings.columnCount)
        {
            columns = new int[settings.columnCount];
        }
        
        maximumColumnHeight = 0;
        for (int i = 0; i < columns.length; i++)
            columns[i] = 0;
        
        for (Atom atom : state.atoms)
        {
            double velocity = atom.velocity.length();
            int index = (int) (velocity / settings.velocityInterval);
            if (index < columns.length)
            {
                columns[index]++;
                maximumColumnHeight = Math.max(maximumColumnHeight, columns[index]);
            }
        }
    }
    
    public void render(SimulationState state, double deltaTime)
    {
        update(state);

        BufferedImage buffer = window.getBuffer(0);
        Graphics2D g2D = buffer.createGraphics();
        KinetixUI.setHints(g2D);

        g2D.setColor(Color.WHITE);
        g2D.fillRect(0, 0, window.getWidth(), window.getHeight());

        int headerHeight = 0;
        g2D.setColor(Color.BLACK);
        g2D.drawString("Velocity interval: " + String.format(Locale.US, "%.2f", settings.velocityInterval), 4, headerHeight += g2D.getFontMetrics().getHeight());
        g2D.drawString("Maximum velocity: " + (settings.maximumVelocity), 4, headerHeight += g2D.getFontMetrics().getHeight());
        g2D.drawString("# columns: " + (settings.columnCount), 4, headerHeight += g2D.getFontMetrics().getHeight());
        headerHeight += g2D.getFontMetrics().getHeight();
        
        g2D.setFont(g2D.getFont().deriveFont(10.0f));
        for (int i = 0; i < columns.length; i++)
        {
            if (columns[i] == 0) continue;
            
            double width = window.targetWidth / (double) columns.length;
            double height = (window.targetHeight - headerHeight) * (columns[i] / (double) maximumColumnHeight);
            double x = i * width;
            double y = window.targetHeight - height;

            g2D.setColor(Color.ORANGE);
            g2D.fill(new Rectangle2D.Double(x, y, width, height));
            
            g2D.translate(x + width, y);
            g2D.rotate(Math.toRadians(-90.0));
            
            g2D.setColor(Color.BLACK);
            g2D.drawString(columns[i] + "", 0, 0);
            
            g2D.rotate(Math.toRadians(90.0));
            g2D.translate(-x - width, -y);
        }
        
        g2D.dispose();

        window.swapBuffers();
    }
}
