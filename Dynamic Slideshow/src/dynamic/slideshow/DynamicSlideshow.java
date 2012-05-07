/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamic.slideshow;



import java.awt.Graphics; 
import java.awt.GraphicsEnvironment; 
import java.awt.Image; 
import java.awt.Toolkit; 
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.net.MalformedURLException; 
import java.net.URL; 

import java.io.*;
 
import javax.swing.JFrame; 

/**
 *
 * @author chris
 */ 
public class DynamicSlideshow extends JFrame { 
 
    // this line is needed to avoid serialization warnings  
    private static final long serialVersionUID = 1L;
 
 
    Image screenImage; // downloaded image  
    int w, h; // Display height and width 
    String folder;
 
 
    // Program entry 
    public static void main(String[] args) throws Exception { 
        if (args.length < 1) //load test images directory by default
            new DynamicSlideshow("C:\testimages"); 
        else {
        }
            new DynamicSlideshow(args[0]); // or first command-line argument 
    } 
 
    // Class constructor  
    DynamicSlideshow(String source) throws MalformedURLException { 
 
        this.folder = source;
        // Exiting program on window close 
        addWindowListener(new WindowAdapter() { 
            public void windowClosing(WindowEvent e) { 
                System.exit(0);
            } 
        });
 
        // Exitig program on mouse click 
        addMouseListener(new MouseListener() { 
            public void mouseClicked(MouseEvent e) { System.exit(0); } 
            public void mousePressed(MouseEvent e) {} 
            public void mouseReleased(MouseEvent e) {} 
            public void mouseEntered(MouseEvent e) {} 
            public void mouseExited(MouseEvent e) {} 
        } 
        );
 
        // remove window frame  
        this.setUndecorated(true);
 
        // window should be visible 
        this.setVisible(true);
 
        // switching to fullscreen mode 
        GraphicsEnvironment.getLocalGraphicsEnvironment().
        getDefaultScreenDevice().setFullScreenWindow(this);
 
        // getting display resolution: width and height 
        w = this.getWidth();
        h = this.getHeight();
        System.out.println("Display resolution: " + String.valueOf(w) + "x" + String.valueOf(h));
 
        //load images
        //
        if (source.startsWith("http://")) // http:// URL was specified 
            screenImage = Toolkit.getDefaultToolkit().getImage(new URL(source));
        else 
            screenImage = Toolkit.getDefaultToolkit().getImage(source); // otherwise - file 
    } 
    
    /*
     * This method loads all images in a folder into an array of images.
     * In order to determine if there has been an update in the folder it checks
     * the file modification time stamps against a stored timestamp. If the
     * current modification timstamps in the folder are more recent than the
     * stored one all the images in the folder will be reloaded.
     */
    public Image[] loadImages() {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage());
        //if the folder is empty
        if(imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        }
        //otherwise it has something in it
        else {
            
        }
        
        
        return null;
    }
    
    public long getLatestModified(File[] files) {
        long longTime = 0L;
        if(files == null)
            return 0L;
        else {
            //find the most recently modified file time
            for(int i=0;i<files.length-1;i++) {
                if(files[i].lastModified() > longTime)
                    longTime = files[i].lastModified();
            }
        }
        
        return longTime;
    }
 
    public void paint (Graphics g) { 
        if (screenImage != null) // if screenImage is not null (image loaded and ready) 
            g.drawImage(screenImage, // draw it  
                        w/2 - screenImage.getWidth(this) / 2, // at the center  
                        h/2 - screenImage.getHeight(this) / 2, // of screen 
                        this);
            // to draw image at the center of screen 
            // we calculate X position as a half of screen width minus half of image width 
            // Y position as a half of screen height minus half of image height 
    } 
    
    public class OnlyImage implements FilenameFilter {
        
        public boolean accept(File dir, String name) {
            return (name.endsWith(".jpg") ||
                    name.endsWith(".jpeg") ||
                    name.endsWith(".JPG") ||
                    name.endsWith(".JPEG") ||
                    name.endsWith(".gif") ||
                    name.endsWith(".GIF") ||
                    name.endsWith(".png") ||
                    name.endsWith(".PNG"));
        }
    }
 
 
} 
