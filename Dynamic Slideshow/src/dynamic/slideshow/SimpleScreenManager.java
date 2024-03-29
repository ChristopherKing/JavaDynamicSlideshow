package dynamic.slideshow;


import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import javax.swing.JFrame;

/**
    The SimpleScreenManager class manages initializing and
    displaying full screen graphics modes.
*/
public class SimpleScreenManager {

    private GraphicsDevice device;

    /**
        Creates a new SimpleScreenManager object.
    */
    public SimpleScreenManager(int screen) {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] temp = environment.getScreenDevices();
        device = temp[screen];
    }
    
    public GraphicsDevice getDevice() {
        return device;
    }


    /**
        Enters full screen mode and changes the display mode.
    */
    public void setFullScreen(DisplayMode displayMode, JFrame window)
    {
        window.setUndecorated(true);
        window.setResizable(false);

        device.setFullScreenWindow(window);
        if (displayMode != null &&
            device.isDisplayChangeSupported())
        {
            try{
                device.setDisplayMode(displayMode);
            }
            catch (IllegalArgumentException ex) {
                // ignore - illegal mode for this device
            }
        }
    }


    /**
        Returns the window currently used in full screen mode.
    */
    public Window getFullScreenWindow() {
        return device.getFullScreenWindow();
    }


    /**
        Restores the screen's display mode.
    */
    public void restoreScreen() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            window.dispose();
        }
        device.setFullScreenWindow(null);
    }

}