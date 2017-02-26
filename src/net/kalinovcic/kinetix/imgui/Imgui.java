package net.kalinovcic.kinetix.imgui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
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
    public void onClose() {}
    
    public void pushBounds(ImguiBounds bounds)
    {
        bounds.parent = context.bounds;
        context.bounds = bounds;

        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(context.bounds.x, context.bounds.y);
        g.transform(transform);
        
        context.totalTranslationX += context.bounds.x;
        context.totalTranslationY += context.bounds.y;
        
        g.setClip(new Rectangle2D.Float(0, 0, context.bounds.width + 1, context.bounds.height + 1));
    }
    
    public void popBounds()
    {
        AffineTransform transform = new AffineTransform();
        transform.setToTranslation(-context.bounds.x, -context.bounds.y);
        g.transform(transform);
        
        context.totalTranslationX -= context.bounds.x;
        context.totalTranslationY -= context.bounds.y;
        
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
    
    public Point2D.Float mousePoint()
    {
        if (!context.focus) return new Point2D.Float(-10000.0f, -10000.0f);
        if (context.mouseDragging) return new Point2D.Float(-10000.0f, -10000.0f);

        float mouseX = context.mouseX - context.totalTranslationX;
        float mouseY = context.mouseY - context.totalTranslationY;

        if (context.bounds != null)
        {
            if (mouseX > context.bounds.width) return new Point2D.Float(-10000.0f, -10000.0f);
            if (mouseY > context.bounds.height) return new Point2D.Float(-10000.0f, -10000.0f);
        }
        return new Point2D.Float(mouseX, mouseY);
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

    public Font textFont;
    public Color textColor;
    public Color textOutline;
    public float textOutlineWidth = 2.0f;
    
    public void renderText(String text, Rectangle2D.Float r, float alignment, float offset)
    {
        Font font = (textFont != null ? textFont : FONT);
        Color color = (textColor != null ? textColor : TEXT);
        
        if (text.length() == 0) return;
        
        FontMetrics metrics = g.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        
        float textX = r.x + (r.width - textWidth) * alignment + offset;
        float textY = r.y + (r.height - textHeight) / 2 + metrics.getAscent();
        
        FontRenderContext renderContext = g.getFontRenderContext();
        TextLayout layout = new TextLayout(text, font, renderContext);
        Shape shape = layout.getOutline(null);
        // Shape shape = font.createGlyphVector(, text).getOutline(textX, textY);
        
        g.translate(textX, textY);
        if (textOutline != null)
        {
            Stroke defaultStroke = g.getStroke();
            g.setStroke(new BasicStroke(textOutlineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, null, 0));
            
            g.setColor(textOutline);
            g.draw(shape);
            
            g.setStroke(defaultStroke);
        }
        g.setColor(color);
        g.fill(shape);
        g.translate(-textX, -textY);
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
        doLabel(text, context.bounds.width, 0.0f, FONT, null);
    }
    
    public void doLabel(String text, float width)
    {
        if (width <= 0) width = -(g.getFontMetrics(FONT).stringWidth(text));
        Rectangle2D.Float r = pushBox(-width, -g.getFontMetrics(FONT).getHeight());
        doLabel(text, r, 0.0f, FONT, null);
    }
    
    public void doLabel(String text, float width, float alignment, Font font, Color color)
    {
        if (width <= 0) width = -(g.getFontMetrics(font).stringWidth(text));
        float height = (color == null) ? (g.getFontMetrics(font).getHeight()) : (BUTTON_HEIGHT);
        Rectangle2D.Float r = pushBox(-width, -height);
        doLabel(text, r, alignment, font, color);
    }
    
    public void doLabel(String text, Rectangle2D.Float r, float alignment, Font font, Color color)
    {
        if (color != null)
        {
            renderShape(rounded(r, BUTTON_ROUNDED_RADIUS), color);
            
            textFont = font;
            renderText(text, r, alignment, 0);
            textFont = null;
        }
        else
        {
            textFont = font;
            renderText(text, r, alignment, 0);
            textFont = null;
        }
    }
    
    public boolean doButton(String text)
    {
        return doButton(text, 0, 0, true);
    }
    
    public boolean doButton(String text, float width, float height)
    {
        return doButton(text, width, height, true);
    }
    
    public boolean doButton(String text, float width, float height, boolean enabled)
    {
        if (width <= 0) width = -(g.getFontMetrics(FONT).stringWidth(text) + 20);
        if (height <= 0) height = -BUTTON_HEIGHT;
        Rectangle2D.Float r = pushBox(width, height);
        return doButton(text, r, enabled);
    }
    
    public boolean doButton(String text, Rectangle2D.Float r, boolean enabled)
    {
        Shape shape = rounded(r, BUTTON_ROUNDED_RADIUS);
        
        boolean hot = enabled && shape.contains(mousePoint());
        boolean active = hot && (context.mouseDown);
        boolean pressed = hot && context.mouseReleased;
        if (hot) context.mouseBusy = true;
        
        renderShape(shape, enabled ? (active ? BUTTON_ACTIVE : (hot ? BUTTON_HOT : BUTTON_NORMAL)) : BUTTON_DISABLED);
        renderText(text, r, 0.5f, 0);
        
        return pressed;
    }
    
    public boolean doCheckbox(String text, boolean current)
    {
        return doCheckbox(text, 0, 0, current);
    }
    
    public boolean doCheckbox(String text, float width, float height, boolean current)
    {
        if (width <= 0) width = -(g.getFontMetrics(FONT).stringWidth(text) + CHECKBOX_HEIGHT + 8);
        if (height <= 0) height = -CHECKBOX_HEIGHT;
        Rectangle2D.Float r = pushBox(-width, -height);
        return doCheckbox(text, r, current);
    }
    
    public boolean doCheckbox(String text, Rectangle2D.Float r, boolean current)
    {
        Rectangle2D.Float inputRect = new Rectangle2D.Float(r.x, r.y, r.height, r.height);
        Rectangle2D.Float textRect = new Rectangle2D.Float(r.x + r.height + 8, r.y, r.width - r.height - 8, r.height);
        Shape inputShape = rounded(inputRect, CHECKBOX_ROUNDED_RADIUS);
        
        boolean hot = inputShape.contains(mousePoint());
        boolean pressed = hot && context.mouseReleased;
        if (hot) context.mouseBusy = true;
        if (pressed) current = !current;
        
        renderShape(inputShape, current ? CHECKBOX_TRUE : CHECKBOX_FALSE);
        renderText(text, textRect, 0.0f, 0);
        return current;
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
    
    public void doInput(String text, ImguiInput input)
    {
        Rectangle2D.Float r = pushBox(-context.bounds.width, -INPUT_HEIGHT);
        doInput(text, r, input, null);
    }
    
    public void doInput(String text, float width, ImguiInput input)
    {
        Rectangle2D.Float r = pushBox(-width, -INPUT_HEIGHT);
        doInput(text, r, input, null);
    }
    
    public void doInput(String text, float width, ImguiInput input, String suffix)
    {
        Rectangle2D.Float r = pushBox(-width, -INPUT_HEIGHT);
        doInput(text, r, input, suffix);
    }
    
    public void doInput(String text, Rectangle2D.Float r, ImguiInput input, String suffix)
    {
        Rectangle2D.Float inputRect = (text.length() == 0) ? r : (new Rectangle2D.Float(r.x + r.width * INPUT_DIVIDE_PERCENT, r.y, r.width * (1 - INPUT_DIVIDE_PERCENT), r.height));
        Shape inputShape = rounded(inputRect, INPUT_ROUNDED_RADIUS);
        
        boolean hot = inputShape.contains(mousePoint());
        if (hot)
        {
            input.hasFocus = true;
            for (Character c : context.typedChars)
            {
                if (c == 0x08 && input.text.length() > 0)
                    input.text = input.text.substring(0, input.text.length() - 1);
                else if (c >= 0x20)
                    input.text += c.charValue();
            }
            context.typedChars.clear();
            context.mouseBusy = true;
        }
        else
        {
            if (input.hasFocus)
            {
                input.text = input.accept(input.text);
            }
        }
        
        renderShape(inputShape, hot ? INPUT_HOT : INPUT_NORMAL);
        renderText(text, r, 0.0f, 0);
        
        Shape clip = g.getClip();
        g.clip(inputRect);
        renderText(input.text, inputRect, 0.0f, 5);
        if (suffix != null)
        {
            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(input.text);
            int textHeight = metrics.getHeight();
            
            float textX = inputRect.x + 10 + textWidth;
            float textY = inputRect.y + (inputRect.height - textHeight) / 2 + metrics.getAscent();
            g.setColor(TEXT_DISABLED);
            g.drawString(suffix, textX, textY);
        }
        g.setClip(clip);
    }
    
    public int doTabs(String[] text, int current)
    {
        Rectangle2D.Float r = pushBox(-context.bounds.width, -FOLD_HEIGHT);
        return doTabs(text, r, current);
    }
    
    public int doTabs(String[] text, float width, int current)
    {
        Rectangle2D.Float r = pushBox(width, -FOLD_HEIGHT);
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

    /***********************************************************/
    /***********************************************************/
    /***********************************************************/
    
    public void beginRow()
    {
        pushLayout(new ImguiHorizontalLayout());
    }
    
    public void endRow()
    {
        popLayout();
    }
    
    public float columnWidth(int numColumns)
    {
        return (context.bounds.width - (numColumns - 1) * PADDING_HORIZONTAL) / numColumns;
    }
}
