/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamic.slideshow;

import java.awt.*;
import javax.swing.ImageIcon;

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
    Image[] slideshow;
    int w, h; // Display height and width 
    private static final String folder = "C:\\testimages";
    long modified;

    // Program entry 
    public static void main(String[] args) throws Exception {
        DisplayMode displayMode;

        if (args.length == 3) {
            displayMode = new DisplayMode(
                    Integer.parseInt(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    DisplayMode.REFRESH_RATE_UNKNOWN);
        } else {
            displayMode = new DisplayMode(1680, 1050, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
        }

        DynamicSlideshow test = new DynamicSlideshow();
        test.run(displayMode);
    }
    private static final int FONT_SIZE = 24;
    private static final long DEMO_TIME = 10000;
    private SimpleScreenManager screen;
    private Image drawnImage;
    private int currentSlide;
    private boolean imagesLoaded;

    public void run(DisplayMode displayMode) {
        setBackground(Color.blue);
        setForeground(Color.white);
        setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        imagesLoaded = false;
        slideshow = getImages();

        screen = new SimpleScreenManager();

        try {
            screen.setFullScreen(displayMode, this);
            loadImages();
            try {
                Thread.sleep(DEMO_TIME);
            } catch (InterruptedException ex) {
            }
        } finally {
            screen.restoreScreen();
        }
    }

    public void loadImages() {
        //bgImage = loadImage("images/background.jpg");
        //drawnImage = loadImage("C:\\testimages\\map.JPG");
        //transparentImage = loadImage("images/transparent.png");
        //translucentImage = loadImage("images/translucent.png");
        //antiAliasedImage = loadImage("images/antialiased.png");
        
        drawnImage = slideshow[currentSlide];
        currentSlide++;
        if(currentSlide >= slideshow.length)
            currentSlide = 0;
        imagesLoaded = true;
        // signal to AWT to repaint this window
        repaint();
    }

    private Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void paint(Graphics g) {
        // set text anti-aliasing
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        // draw images
        if (imagesLoaded) {
            //g.drawImage(bgImage, 0, 0, null);
            drawImage(g, drawnImage, 0, 0, "Opaque");
            //drawImage(g, transparentImage, 320, 0, "Transparent");
            //drawImage(g, translucentImage, 0, 300, "Translucent");
            //drawImage(g, antiAliasedImage, 320, 300, "Translucent (Anti-Aliased)");
        } else {
            g.drawString("Loading Images...", 5, FONT_SIZE);
        }
    }

    public void drawImage(Graphics g, Image image, int x, int y, String caption) {
        g.drawImage(image, x, y, null);
        g.drawString(caption, x + 5, y + FONT_SIZE + image.getHeight(null));
    }

    /*
     * This method loads all images in a folder into an array of images. In
     * order to determine if there has been an update in the folder it checks
     * the file modification time stamps against a stored timestamp. If the
     * current modification timstamps in the folder are more recent than the
     * stored one all the images in the folder will be reloaded.
     */
    private Image[] getImages(Image[] oldImages) {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage());
        Image[] images = null;
        //if the folder is empty
        if (imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        } //otherwise it has something in it
        else {
            //check modified timestamps
            //if the new file list has been modified since last update then reload images
            if (getLatestModified(imageFiles) > this.modified) {
                images = new Image[imageFiles.length];
                //populate image array with new images from file list in files array
                for (int i = 0; i < imageFiles.length; i++) {
                    images[i] = Toolkit.getDefaultToolkit().createImage(imageFiles[i].getAbsolutePath());
                }
                return images;
            } //otherwise continue using old images
            else {
                return oldImages;
            }

        }
        return images;
    }
    
    private Image[] getImages() {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage());
        Image[] images = null;
        //if the folder is empty
        if (imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        } //otherwise it has something in it
        else {
            //check modified timestamps
            //if the new file list has been modified since last update then reload images

            images = new Image[imageFiles.length];
            //populate image array with new images from file list in files array
            for (int i = 0; i < imageFiles.length; i++) {
                images[i] = loadImage(imageFiles[i].getAbsolutePath());
            }
            return images;


        }
        return images;
    }

    //Should return a long int representing the most recently modified file's modification time
    public static long getLatestModified(File[] files) {
        long longTime = 0L;
        if (files == null) {
            return 0L;
        } else {
            //find the most recently modified file time
            for (int i = 0; i < files.length; i++) {
                if (files[i].lastModified() > longTime) {
                    longTime = files[i].lastModified();
                }
            }
        }

        return longTime;
    }

    public class OnlyImage implements FilenameFilter {

        public boolean accept(File dir, String name) {
            return (name.endsWith(".jpg")
                    || name.endsWith(".jpeg")
                    || name.endsWith(".JPG")
                    || name.endsWith(".JPEG")
                    || name.endsWith(".gif")
                    || name.endsWith(".GIF")
                    || name.endsWith(".png")
                    || name.endsWith(".PNG"));
        }
    }
}
