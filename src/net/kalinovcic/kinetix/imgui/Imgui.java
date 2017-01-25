package net.kalinovcic.kinetix.imgui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import static net.kalinovcic.kinetix.imgui.ImguiTheme.*;

public abstract class Imgui
{
    public ImguiContext context;
    public Graphics2D g;
    
    public abstract void update();
    
    public void pushBounds(ImguiBounds bounds)
    {
        bounds.parent = context.bounds;
        context.bounds = bounds;

        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(context.bounds.x, context.bounds.y);
        g.transform(transform);
        g.setClip(new Rectangle2D.Float(0, 0, context.bounds.width + 1, context.bounds.height + 1));
    }
    
    public void popBounds()
    {
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(-context.bounds.x, -context.bounds.y);
        g.transform(transform);
        
        context.bounds = context.bounds.parent;
        if (context.bounds != null)
            g.setClip(new Rectangle2D.Float(0, 0, context.bounds.width + 1, context.bounds.height + 1));
    }
    
    public void pushLayout(ImguiLayout layout)
    {
        if (context.layout != null)
        {
            layout.x = context.layout.x;
            layout.y = context.layout.y;
        }
        
        context.layoutStack.add(layout);
        context.layout = layout;
    }
    
    public void popLayout()
    {
        context.layoutStack.remove(context.layoutStack.size() - 1);
        if (context.layoutStack.size() > 0)
        {
            ImguiLayout next = context.layoutStack.get(context.layoutStack.size() - 1);
            next.moveBox(new Rectangle2D.Float(0, 0, context.layout.width, context.layout.height)); 
            context.layout = next;
        }
        else
        {
            context.layout = null;
        }
    }

    /***********************************************************/
    /***********************************************************/
    /***********************************************************/
    
    public Point2D mousePoint()
    {
        Point2D point = new Point2D.Float(context.mouseX, context.mouseY);
        point.setLocation(-point.getX(), -point.getY());
        g.getTransform().transform(point, point);
        point.setLocation(-point.getX(), -point.getY());
        return point;
    }
    
    public Rectangle2D.Float pushBox(float width, float height)
    {
        Rectangle2D.Float r = new Rectangle2D.Float(0, 0, width, height);
        context.layout.nextBox(r);
        return r;
    }
    
    public RoundRectangle2D.Float rounded(Rectangle2D.Float r, float radius)
    {
        if (radius > r.width * 0.5f) radius = r.width * 0.5f;
        if (radius > r.height * 0.5f) radius = r.height * 0.5f;
        return new RoundRectangle2D.Float(r.x, r.y, r.width, r.height, radius, radius);
    }

    /***********************************************************/
    /***********************************************************/
    /***********************************************************/
    
    public void renderShape(Shape shape, Color fillColor)
    {
        g.setColor(fillColor);
        g.fill(shape);
        g.setColor(OUTLINE);
        g.draw(shape);
    }
    
    public void renderText(String text, Rectangle2D.Float r, float alignment, float offset)
    {
        if (text.length() == 0) return;
        
        g.setFont(FONT);
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        
        float textX = r.x + (r.width - textWidth) * alignment + offset;
        float textY = r.y + (r.height - textHeight) / 2 + metrics.getAscent();
        g.setColor(TEXT);
        g.drawString(text, textX, textY);
    }

    /***********************************************************/
    /***********************************************************/
    /***********************************************************/
    
    public void doSpace(float width, float height)
    {
        context.layout.moveBox(new Rectangle2D.Float(0, 0, width, height));
    }
    
    public void doLabel(String text)
    {
        doLabel(text, context.bounds.width);
    }
    
    public void doLabel(String text, float width)
    {
        if (width <= 0) width = -(g.getFontMetrics(FONT).stringWidth(text));
        Rectangle2D.Float r = pushBox(-width, -g.getFontMetrics(FONT).getHeight());
        doLabel(text, r);
    }
    
    public void doLabel(String text, Rectangle2D.Float r)
    {
        renderText(text, r, 0.0f, 0);
    }
    
    public boolean doButton(String text)
    {
        return doButton(text, 0, 0);
    }
    
    public boolean doButton(String text, float width, float height)
    {
        if (width <= 0) width = -(g.getFontMetrics(FONT).stringWidth(text) + 20);
        if (height <= 0) height = -BUTTON_HEIGHT;
        Rectangle2D.Float r = pushBox(width, height);
        return doButton(text, r);
    }
    
    public boolean doButton(String text, Rectangle2D.Float r)
    {
        Shape shape = rounded(r, BUTTON_ROUNDED_RADIUS);
        
        boolean hot = shape.contains(mousePoint());
        boolean active = hot && (context.mouseDown);
        boolean pressed = hot && context.mouseReleased;
        if (hot) context.mouseBusy = true;
        
        renderShape(shape, active ? BUTTON_ACTIVE : (hot ? BUTTON_HOT : BUTTON_NORMAL));
        renderText(text, r, 0.5f, 0);
        
        return pressed;
    }
    
    public boolean doFold(String text, boolean current)
    {
        Rectangle2D.Float r = pushBox(-context.bounds.width, -FOLD_HEIGHT);
        return doFold(text, r, current);
    }
    
    public boolean doFold(String text, Rectangle2D.Float r, boolean current)
    {
        Shape shape = rounded(r, FOLD_ROUNDED_RADIUS);
        
        boolean hot = shape.contains(mousePoint());
        boolean active = hot && (context.mouseDown);
        boolean pressed = hot && context.mouseReleased;
        current = pressed ? !current : current;
        if (hot) context.mouseBusy = true;
        
        renderShape(shape, active ? FOLD_ACTIVE : (hot ? FOLD_HOT : FOLD_NORMAL));
        renderText(text, r, 0.0f, r.height * 1.0f);

        GeneralPath triangle = new GeneralPath();
        if (current)
        {
            triangle.moveTo(r.x + r.height * 0.3, r.y + r.height * 0.35);
            triangle.lineTo(r.x + r.height * 0.5, r.y + r.height * 0.7);
            triangle.lineTo(r.x + r.height * 0.7, r.y + r.height * 0.35);
            triangle.lineTo(r.x + r.height * 0.3, r.y + r.height * 0.35);
        }
        else
        {
            triangle.moveTo(r.x + r.height * 0.4, r.y + r.height * 0.3);
            triangle.lineTo(r.x + r.height * 0.7, r.y + r.height * 0.5);
            triangle.lineTo(r.x + r.height * 0.4, r.y + r.height * 0.7);
            triangle.lineTo(r.x + r.height * 0.4, r.y + r.height * 0.3);
        }
        g.setColor(TEXT);
        g.fill(triangle);
        
        return current;
    }
    
    public String doInput(String text, String current)
    {
        Rectangle2D.Float r = pushBox(-context.bounds.width, -INPUT_HEIGHT);
        return doInput(text, r, current);
    }
    
    public String doInput(String text, float width, String current)
    {
        Rectangle2D.Float r = pushBox(-width, -INPUT_HEIGHT);
        return doInput(text, r, current);
    }
    
    public String doInput(String text, Rectangle2D.Float r, String current)
    {
        Rectangle2D.Float inputRect = (text.length() == 0) ? r : (new Rectangle2D.Float(r.x + r.width * INPUT_DIVIDE_PERCENT, r.y, r.width * (1 - INPUT_DIVIDE_PERCENT), r.height));
        Shape inputShape = rounded(inputRect, INPUT_ROUNDED_RADIUS);
        
        boolean hot = inputShape.contains(mousePoint());
        if (hot)
        {
            for (Character c : context.typedChars)
            {
                if (c == 0x08 && current.length() > 0)
                    current = current.substring(0, current.length() - 1);
                else if (c >= 0x20)
                    current += c.charValue();
            }
            context.typedChars.clear();
            context.mouseBusy = true;
        }
        
        renderShape(inputShape, hot ? INPUT_HOT : INPUT_NORMAL);
        renderText(text, r, 0.0f, 0);
        
        Shape clip = g.getClip();
        g.setClip(inputRect);
        renderText(current, inputRect, 0.0f, 5);
        g.setClip(clip);
        
        return current;
    }
    
    public int doTabs(String[] text, int current)
    {
        Rectangle2D.Float r = pushBox(-context.bounds.width, -FOLD_HEIGHT);
        return doTabs(text, r, current);
    }
    
    public int doTabs(String[] text, Rectangle2D.Float r, int current)
    {
        Shape shape = rounded(r, FOLD_ROUNDED_RADIUS);
        
        boolean hot = shape.contains(mousePoint());
        boolean active = hot && (context.mouseDown);
        boolean pressed = hot && context.mouseReleased;
        
        renderShape(shape, FOLD_NORMAL); 
        
        if (hot)
        {
            context.mouseBusy = true;
            int which = (int)((context.mouseX - r.x) / r.width * text.length);
            if (which == text.length) which--;
            if (pressed) current = which;

            float w = r.width / text.length;
            float x = r.x + which * w;
            Rectangle2D.Float buttonRectangle = new Rectangle2D.Float(x, r.y, w, r.height);
            Shape buttonShape = rounded(buttonRectangle, FOLD_ROUNDED_RADIUS);
            
            renderShape(buttonShape, active ? FOLD_ACTIVE : FOLD_HOT);
        }
        
        {
            float w = r.width / text.length;
            float x = r.x + current * w;
            Rectangle2D.Float buttonRectangle = new Rectangle2D.Float(x, r.y, w, r.height);
            Shape buttonShape = rounded(buttonRectangle, FOLD_ROUNDED_RADIUS);
            
            renderShape(buttonShape, FOLD_ACTIVE);
        }

        for (int i = 0; i < text.length; i++)
        {
            float w = r.width / text.length;
            float x = r.x + i * w;
            if (i > 0)
            {
                g.setColor(OUTLINE);
                g.draw(new Line2D.Float(x, r.y, x, r.y + r.height));
            }
            
            Rectangle2D.Float buttonRectangle = new Rectangle2D.Float(x, r.y, w, r.height);
            renderText(text[i], buttonRectangle, 0.5f, 0);
        }
        
        return current;
    }
}
