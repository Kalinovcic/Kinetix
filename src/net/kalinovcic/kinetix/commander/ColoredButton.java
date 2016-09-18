package net.kalinovcic.kinetix.commander;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

public class ColoredButton extends JButton
{
    private static final long serialVersionUID = 1L;

    public ColoredButton()
    {
        super("");
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        final Graphics2D g2D = (Graphics2D) g.create();
        g2D.setColor(getBackground());
        g2D.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2D.setColor(Color.LIGHT_GRAY);
        g2D.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2D.dispose();
    }
}