package net.kalinovcic.kinetix.imgui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JInternalFrame;
import javax.swing.JRootPane;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import net.kalinovcic.kinetix.MainWindow;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public class ImguiFrame extends JInternalFrame implements ActionListener, FocusListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
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
        context.nextFrameWidth = width;
        context.nextFrameHeight = height;
        context.currentFrameX = x;
        context.currentFrameY = y;
        context.currentFrameWidth = context.nextFrameWidth;
        context.currentFrameHeight = context.nextFrameHeight;
        resizeToMatchContext();
        moveToMatchContext();
        
        // Link it
        setFocusable(true);
        addFocusListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);
        setVisible(true);
        mainWindow.desktop.add(this);
        
        // Active rendering:
        Timer timer = new Timer(30, this);
        timer.setInitialDelay(0);
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g1)
    {
        super.paintComponent(g1);
        
        Graphics2D g = (Graphics2D) g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        imgui.context = context;
        imgui.g = g;
        
        context.bounds = null;
        context.mousePressed = context.mouseDown && !context.mouseDownPrevious;
        context.mouseReleased = !context.mouseDown && context.mouseDownPrevious;
        context.mouseDownPrevious = context.mouseDown;
        
        if (context.focus && context.mouseDown && !context.mousePressed && !context.mouseBusy)
        {
            context.mouseBusy = true;
            context.mouseDragging = true;
        }
        
        if (context.mouseDragging)
        {
            if (context.mouseReleased)
            {
                context.mouseDragging = false;
            }
            else
            {
                int deltaX = context.mouseScreenX - context.pressMouseScreenX;
                int deltaY = context.mouseScreenY - context.pressMouseScreenY;
                int frameX = context.pressFrameX + deltaX;
                int frameY = context.pressFrameY + deltaY;

                context.currentFrameX = frameX;
                context.currentFrameY = frameY;
            }
        }

        float w = getWidth();
        float h = getHeight();

        Shape windowShape = imgui.rounded(new Rectangle2D.Float(0, 0, w - 1, h - 1), WINDOW_ROUNDED_RADIUS);
        g.setColor(WINDOW_NORMAL);
        g.fill(windowShape);
        if (context.focus)
        {
            Stroke defaultStroke = g.getStroke();
            g.setStroke(new BasicStroke(2.0f));

            g.setColor(WINDOW_FOCUS);
            g.draw(windowShape);
            
            g.setStroke(defaultStroke);
        }
        
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
        context.mouseVerticalScrollDelta = 0;
        context.mouseHorizontalScrollDelta = 0;

        if (mainWindow.desktop.getWidth() != 0)
            if (context.currentFrameWidth > mainWindow.desktop.getWidth())
                context.nextFrameHeight = mainWindow.desktop.getWidth();
        if (mainWindow.desktop.getHeight() != 0)
            if (context.currentFrameHeight > mainWindow.desktop.getHeight())
                context.nextFrameHeight = mainWindow.desktop.getHeight();
        
        if (context.nextFrameWidth != context.currentFrameWidth ||
            context.nextFrameHeight != context.currentFrameHeight)
        {
            context.currentFrameWidth = context.nextFrameWidth;
            context.currentFrameHeight = context.nextFrameHeight;
            if (mainWindow.desktop.getWidth() != 0)
                if (context.currentFrameWidth > mainWindow.desktop.getWidth())
                    context.currentFrameWidth = mainWindow.desktop.getWidth();
            if (mainWindow.desktop.getHeight() != 0)
                if (context.currentFrameHeight > mainWindow.desktop.getHeight())
                    context.currentFrameHeight = mainWindow.desktop.getHeight();
            
            resizeToMatchContext();
        }
        
        g.dispose();
    }
    
    private void moveToMatchContext()
    {
        if (context.currentFrameX < 0) context.currentFrameX = 0;
        if (context.currentFrameY < 0) context.currentFrameY = 0;
        if (mainWindow.desktop.getWidth() != 0)
        {
            if (context.currentFrameX + context.currentFrameWidth > mainWindow.desktop.getWidth())
                context.currentFrameX = mainWindow.desktop.getWidth() - context.currentFrameWidth;
        }
        if (mainWindow.desktop.getHeight() != 0)
        {
            if (context.currentFrameY + context.currentFrameHeight > mainWindow.desktop.getHeight())
                context.currentFrameY = mainWindow.desktop.getHeight() - context.currentFrameHeight;
        }
        setLocation(context.currentFrameX, context.currentFrameY);
    }
    
    private void resizeToMatchContext()
    {
        Dimension size = new Dimension(context.currentFrameWidth, context.currentFrameHeight);
        setSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
    }
    
    @Override public void actionPerformed(ActionEvent e)
    {
        moveToMatchContext();
        repaint();
    }
    
    @Override public void focusGained(FocusEvent e) { context.focus = true; }
    @Override public void focusLost(FocusEvent e) { context.focus = context.mouseDown = context.mouseDragging = context.mouseBusy = false; }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e)
    {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        context.pressMouseScreenX = e.getXOnScreen();
        context.pressMouseScreenY = e.getYOnScreen();
        context.pressFrameX = getX();
        context.pressFrameY = getY();
        context.mouseDown = true;
        context.focus = true;
    }
    @Override public void mouseReleased(MouseEvent e)
    {
        if (e.getButton() != MouseEvent.BUTTON1) return;
        context.mouseDown = context.mouseBusy = false;
    }

    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
    @Override public void mouseMoved(MouseEvent e)
    {
        context.mouseX = e.getX();
        context.mouseY = e.getY();
        context.mouseScreenX = e.getXOnScreen();
        context.mouseScreenY = e.getYOnScreen();
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        if (e.isShiftDown())
            context.mouseHorizontalScrollDelta += e.getWheelRotation();
        else
            context.mouseVerticalScrollDelta += e.getWheelRotation();
    }
    
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) { context.typedChars.add(e.getKeyChar()); }
}
