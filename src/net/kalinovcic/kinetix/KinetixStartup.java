package net.kalinovcic.kinetix;

import java.awt.Color;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import net.kalinovcic.kinetix.commander.CommanderWindow;
import net.kalinovcic.kinetix.imgui.Imgui;
import net.kalinovcic.kinetix.imgui.ImguiFrame;
import net.kalinovcic.kinetix.imgui.ImguiHorizontalLayout;
import net.kalinovcic.kinetix.imgui.ImguiTheme;
import net.kalinovcic.kinetix.physics.PhysicsThread;
import net.kalinovcic.kinetix.profiler.ProfilerWindow;
import net.kalinovcic.kinetix.simulation.SimulationThread;

public class KinetixStartup
{
    private static int counter = 1;
    private static boolean[] fold = new boolean[64];
    private static String cowabunga = "lalala";
    private static int tab = 0;
    
	public static void main(String[] args)
	{
        new PhysicsThread().start();
        
	    try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e) {}
        
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                MainWindow mainWindow = new MainWindow();
                new CommanderWindow(mainWindow);
                new SimulationThread(mainWindow).start();
                new ProfilerWindow(mainWindow);
                
                new ImguiFrame(mainWindow, "IMGUI", 700, 20, 400, 450, true,new Imgui()
                {
                    @Override
                    public void update()
                    {
                        tab = doTabs(new String[] { "aADSF1", "alala", "ohooh"}, tab);
                        
                        doLabel("Uuu, vidi sve te super duper reakcije");
                        doLabel("Još, još, još!");
                        doSpace(0, 8);
                        
                        for (int i = 0; i < 7; i++)
                        {
                            if (i >= 6)
                            {
                                doSpace(0, ImguiTheme.BUTTON_HEIGHT);
                                continue;
                            }
                            
                            pushLayout(new ImguiHorizontalLayout());
                            doLabel("NO2", 150.0f);
                            doInput("", 60.0f, "5.0");
                            doInput("", 60.0f, "2.0");
                            doInput("", 60.0f, "3.0");
                            doSpace(5, 0);
                            
                            Color c = new Color(Color.HSBtoRGB(i * 0.1f, 1.0f, 1.0f));
                            renderShape(pushBox(ImguiTheme.BUTTON_HEIGHT, ImguiTheme.BUTTON_HEIGHT), c);
                            popLayout();
                        }
                        
                        for (int i = 0; i < counter; i++)
                        {
                            if (fold[i] = doFold("Fold " + i, fold[i]))
                            {
                                pushLayout(new ImguiHorizontalLayout());
                                for (int j = 0; j < (i % 4) + 1; j++)
                                    if (doButton("Hello " + j))
                                    {
                                        counter++;
                                        System.out.println("here");
                                    }
                                popLayout();
                            }
                        }

                        cowabunga = doInput("Cowabunga:", cowabunga);
                        cowabunga = doInput("", cowabunga);
                    }
                });
            }
        });
	}
}
