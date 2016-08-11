package net.kalinovcic.kinetix;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class KinetixUI extends BasicInternalFrameUI 
{
    public static final Font TITLE_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    public static final Font UI_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    
    public static void setHints(Graphics2D g2D)
    {
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    public KinetixUI(JInternalFrame frame)
    {
        super(frame);
    }
    
    @Override
    public void installUI(JComponent c)
    {
        super.installUI(c);
        frame.setBorder(new LineBorder(Color.black));
    }
    
    protected JComponent createNorthPane(JInternalFrame frame)
    {
        return new KinetixTitleBar(frame);
    }
    
    public static class KinetixTitleBar extends BasicInternalFrameTitlePane
    {
        private static final long serialVersionUID = 1L;

        public static final Color BACKGROUND_COLOR = new Color(133, 133, 133);
        public static final Color SELECTED_BACKGROUND_COLOR = new Color(18, 196, 255);
        
        public static final Color TITLE_COLOR = new Color(0, 0, 0);
        
        public KinetixTitleBar(JInternalFrame frame)
        {
            super(frame);
        }
        
         @Override
        protected void createButtons()
        {
            super.createButtons();
            
            closeButton = new NoFocusButton();
            closeButton.addActionListener(closeAction);
            closeButton.setBorder(null);
            closeButton.setIcon(closeIcon);
        }
        
        @Override
        protected void addSubComponents()
        {
            add(closeButton);
        }

        @Override
        public void paintComponent(Graphics g)
        {
            Graphics2D g2D = (Graphics2D) g;
            setHints(g2D);
            
            boolean isSelected = frame.isSelected();
            if (isSelected) g2D.setBackground(SELECTED_BACKGROUND_COLOR);
            else g2D.setBackground(BACKGROUND_COLOR);
            g2D.clearRect(0, 0, getWidth(), getHeight());
            
            g2D.setColor(Color.black);
            g2D.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            
            int xOffset = 5;

            String frameTitle = frame.getTitle();
            if (frameTitle != null)
            {
                g2D.setFont(TITLE_FONT);
                
                FontMetrics metrics = getFontMetrics(TITLE_FONT);
                int height = metrics.getHeight();
                int yOffset = ((getHeight() - height) / 2) + metrics.getAscent();
                
                int titleRight = frame.getWidth() - frame.getInsets().right - 2;
            
                int titleAvailableWidth = titleRight - xOffset - 4;
                int titleWidth = metrics.stringWidth(frameTitle);
                
                g2D.drawString(frameTitle, xOffset + titleAvailableWidth / 2 - titleWidth / 2, yOffset);
                xOffset += titleWidth + 5;
            }
        }
        
        public static class NoFocusButton extends JButton
        {
            private static final long serialVersionUID = 1L;

            public NoFocusButton()
            {
                setFocusPainted(false);
                setMargin(new Insets(0, 0, 0, 0));
            }
            
            public boolean isFocusTraversable() { return false; }
            public void requestFocus() {}
            
            @Override
            protected void paintComponent(Graphics g)
            {
                int width = getWidth();
                int height = getHeight();
                
                g.setColor(new Color(232, 17, 35));
                g.fillRect(2, 1, width - 2, height - 1);
                
                g.setColor(Color.black);
                g.drawRect(2, 1, width - 3, height - 2);
            }
        };
    }
}
