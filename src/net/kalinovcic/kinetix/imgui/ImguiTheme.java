package net.kalinovcic.kinetix.imgui;

import java.awt.Color;
import java.awt.Font;

public class ImguiTheme
{
    public static final Font FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BIG_FONT = new Font("Segoe UI", Font.PLAIN, 32);

    public static final float WINDOW_ROUNDED_RADIUS = 10.0f;
    public static final Color WINDOW_NORMAL = new Color(40, 51, 54);
    /* public static final Color WINDOW_NORMAL = new Color(239, 239, 239);*/
    
    public static final float PADDING_VERTICAL = 4.0f;
    public static final float PADDING_HORIZONTAL = 10.0f;
    public static final Color OUTLINE = new Color(40, 40, 40);
    public static final Color TEXT = new Color(229, 229, 229);
    public static final Color TEXT_DISABLED = new Color(140, 140, 140);
    /*public static final Color OUTLINE = new Color(190, 198, 200);
    public static final Color TEXT = new Color(63, 63, 63);
    public static final Color TEXT_DISABLED = new Color(153, 153, 153);*/
    
    public static final float BUTTON_HEIGHT = 24.0f;
    public static final float BUTTON_ROUNDED_RADIUS = 10.0f;
    public static final Color BUTTON_NORMAL = new Color(118, 82, 83);
    public static final Color BUTTON_HOT = new Color(153, 86, 77);
    public static final Color BUTTON_ACTIVE = new Color(153, 139, 61);
    public static final Color BUTTON_DISABLED = new Color(89, 97, 99);
    /*public static final Color BUTTON_NORMAL = new Color(251, 209, 88);
    public static final Color BUTTON_HOT = new Color(248, 176, 29);
    public static final Color BUTTON_ACTIVE = new Color(107, 191, 255);
    public static final Color BUTTON_DISABLED = new Color(173, 173, 173);*/
    
    public static final float CHECKBOX_HEIGHT = 24.0f;
    public static final float CHECKBOX_ROUNDED_RADIUS = 12.0f;
    public static final Color CHECKBOX_FALSE = new Color(118, 82, 83);
    public static final Color CHECKBOX_TRUE = new Color(87, 153, 61);
    /*public static final Color CHECKBOX_FALSE = new Color(173, 173, 173);
    public static final Color CHECKBOX_TRUE = new Color(251, 209, 88);*/

    public static final float FOLD_HEIGHT = 24.0f;
    public static final float FOLD_ROUNDED_RADIUS = 10.0f;
    public static final Color FOLD_NORMAL = new Color(68, 74, 132);
    public static final Color FOLD_HOT = new Color(101, 87, 176);
    public static final Color FOLD_ACTIVE = new Color(153, 61, 139);
    /*public static final Color FOLD_NORMAL = new Color(179, 226, 240);
    public static final Color FOLD_HOT = new Color(132, 186, 230);
    public static final Color FOLD_ACTIVE = new Color(107, 191, 255);*/
    
    public static final float INPUT_HEIGHT = 24.0f;
    public static final float INPUT_ROUNDED_RADIUS = 10.0f;
    public static final float INPUT_DIVIDE_PERCENT = 0.3f;
    public static final Color INPUT_NORMAL = new Color(89, 97, 99);
    public static final Color INPUT_HOT = new Color(157, 166, 168);
    /*public static final Color INPUT_NORMAL = new Color(194, 205, 208);
    public static final Color INPUT_HOT = new Color(107, 191, 255);*/
}
