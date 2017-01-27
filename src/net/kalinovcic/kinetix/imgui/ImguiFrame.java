package net.kalinovcic.kinetix.imgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import net.kalinovcic.kinetix.MainWindow;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class ImguiFrame extends JInternalFrame implements ActionListener, MouseListener, MouseMotionListener, KeyListener
{
    private static final long serialVersionUID = 1L;

    private boolean closable;
    public MainWindow mainWindow;
    public Imgui imgui;
    public ImguiContext context;

    public ImguiFrame(MainWindow mainWindow, String title, int x, int y, int width, int height, boolean closable, Imgui imgui)
    {
        super("IMGUI", false, false, false, false);

        this.closable = closable;
        this.mainWindow = mainWindow;
        this.imgui = imgui;
        this.context = new ImguiContext();
        
        // Make it transparent
        setOpaque(false);
        getContentPane().setBackground(new Color(0,0,0,0));

        // Make sure it's undecorated
        putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        ((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
        this.setBorder(null);

        // Size it
        context.nextFrameWindowWidth = width;
        context.nextFrameWindowHeight = height;
        context.currentWindowWidth = context.nextFrameWindowWidth;
        context.currentWindowHeight = context.nextFrameWindowHeight;
        resizeToMatchContext();
        setLocation(x, y);
        
        // Link it
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setVisible(true);
        mainWindow.desktop.add(this);
        
        // Active rendering:
        Timer timer = new Timer(10, this);
        timer.setInitialDelay(0);
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g1)
    {
        super.paintComponent(g1);

        Graphics2D g = (Graphics2D) g1;
        imgui.context = context;
        imgui.g = g;
        
        context.bounds = null;
        context.mousePressed = context.mouseDown && !context.mouseDownPrevious;
        context.mouseReleased = !context.mouseDown && context.mouseDownPrevious;
        context.mouseDownPrevious = context.mouseDown;

        float w = getWidth();
        float h = getHeight();

        g.setColor(WINDOW_NORMAL);
        g.fill(imgui.rounded(new Rectangle2D.Float(0, 0, w, h), WINDOW_ROUNDED_RADIUS));
        
        if (closable)
            if (imgui.doButton("", new Rectangle2D.Float(w - 25, 5, 20, 20), true))
            {
                dispose();
                return;
            }
        
        imgui.pushBounds(new ImguiBounds(12, 8, w - 24, h - 16));
        imgui.pushLayout(new ImguiVerticalLayout());
        imgui.update();
        imgui.popLayout();
        imgui.popBounds();
        
        context.typedChars.clear();
        
        if (context.nextFrameWindowWidth != context.currentWindowWidth ||
            context.nextFrameWindowHeight != context.currentWindowHeight)
        {
            context.currentWindowWidth = context.nextFrameWindowWidth;
            context.currentWindowHeight = context.nextFrameWindowHeight;
            resizeToMatchContext();
        }
        
        g.dispose();
    }
    
    private void resizeToMatchContext()
    {
        Dimension size = new Dimension(context.currentWindowWidth, context.currentWindowHeight);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        repaint();
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) { context.mouseDown = context.dragging = context.mouseBusy = false; }
    @Override public void mousePressed(MouseEvent e)
    {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        context.mouseDown = true;
        context.mouseBeginX = e.getLocationOnScreen().x;
        context.mouseBeginY = e.getLocationOnScreen().y;
    }
    @Override public void mouseReleased(MouseEvent e) { context.mouseDown = context.dragging = context.mouseBusy = false; }

    @Override public void mouseDragged(MouseEvent e)
    {
        if (context.mouseBusy) return;
        int dragX = e.getLocationOnScreen().x;
        int dragY = e.getLocationOnScreen().y;
        if (!context.dragging)
        {
            context.dragging = true;
            context.windowBeginX = getLocation().x;
            context.windowBeginY = getLocation().y;
        }
        if (context.dragging)
        {
            int x = context.windowBeginX + (dragX - context.mouseBeginX);
            int y = context.windowBeginY + (dragY - context.mouseBeginY);
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x + getWidth() > mainWindow.desktop.getWidth()) x = mainWindow.desktop.getWidth() - getWidth();
            if (y + getHeight() > mainWindow.desktop.getHeight()) y = mainWindow.desktop.getHeight() - getHeight();
            setLocation(x, y);
        }
    }
    @Override public void mouseMoved(MouseEvent e) { context.mouseX = e.getX(); context.mouseY = e.getY(); }
    
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) { context.typedChars.add(e.getKeyChar()); }
}
