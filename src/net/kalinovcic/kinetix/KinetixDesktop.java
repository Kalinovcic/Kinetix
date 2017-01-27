package net.kalinovcic.kinetix;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JDesktopPane;

import net.kalinovcic.kinetix.imgui.ImguiTheme;

public class KinetixDesktop extends JDesktopPane
{
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        KinetixUI.setHints(g2D);
        
        g2D.setColor(ImguiTheme.DESKTOP_NORMAL);
        g2D.fillRect(0, 0, getWidth(), getHeight());
    }
}
