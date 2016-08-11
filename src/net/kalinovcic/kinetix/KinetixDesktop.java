package net.kalinovcic.kinetix;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JDesktopPane;

public class KinetixDesktop extends JDesktopPane
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        KinetixUI.setHints(g2D);
        
        g2D.setColor(new Color(0, 95, 179));
        g2D.fillRect(0, 0, getWidth(), getHeight());
    }
}
