/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamic.slideshow;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author chris
 */
public class DynamicSlideshow extends JFrame {

    // this line is needed to avoid serialization warnings  
    private static final long serialVersionUID = 1L;
    private BufferedImage[] slideshow;
    private int w, h; // Display height and width 
    private static final String folder = "Z:\\test";
    private long modified;
    private static final int FONT_SIZE = 24;
    private static final long TRANSITION_TIME = 5000;
    private SimpleScreenManager screen;
    private int currentSlide;
    private boolean imagesLoaded;
    private boolean go;
    
    // Program entry 
    public static void main(String[] args) throws Exception {
        DisplayMode displayMode;
        int screenNum = 0;

        if(args.length == 1) {
            screenNum = Integer.parseInt(args[0]);
        }
        
        else {
            screenNum = 0;
        }

        DynamicSlideshow test = new DynamicSlideshow();
        test.run(screenNum);
    }


    public void run(int screenNum) {
        setBackground(Color.blue);
        setForeground(Color.white);
        setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        imagesLoaded = false;
        slideshow = getImages(); //initial loading of images

        screen = new SimpleScreenManager(screenNum);
        h = screen.getDevice().getDisplayMode().getHeight();
        w = screen.getDevice().getDisplayMode().getWidth();
        DisplayMode displayMode = new DisplayMode(w, h, 16, DisplayMode.REFRESH_RATE_UNKNOWN);
        
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent ev) {
                if(ev.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE)
                    System.exit(0);
            }
        });

        try {
            screen.setFullScreen(displayMode, this);
            go = true;
            while (go) {
                //loadSlide(); //load the first image
                /*
                 * try { Thread.sleep(TRANSITION_TIME); } catch
                 * (InterruptedException ex) { }
                 */
                //drawnImage = slideshow[0];
                repaint();
                waiting(5);
                //drawnImage.flush();
                //drawnImage = slideshow[1];
                currentSlide++; //advance the slide

                //if we just displayed the last image check if the images changed
                if (currentSlide == slideshow.length) {
                    slideshow = getImages(slideshow); //we give old slideshow as input to check against
                    currentSlide = 0; //reset slide counter because we were at the end
                }
            }
        } finally {
            screen.restoreScreen();
        }
    }

    //wait a specified amount of time
    public static void waiting(int n) {

        long t0, t1;

        t0 = System.currentTimeMillis();

        do {
            t1 = System.currentTimeMillis();
        } while ((t1 - t0) < (n * 1000));
    }

    @Override
    public void paint(Graphics g) {
        // draw images
        if (imagesLoaded) {
            //drawImage(g, slideshow[currentSlide], 0, 0, "Image " + currentSlide);
            //g.drawImage(slideshow[currentSlide].getScaledInstance(w, h, java.awt.Image.SCALE_FAST), w / 2 - slideshow[currentSlide].getWidth()/2, h / 2 - slideshow[currentSlide].getHeight()/2, null);
            g.drawImage(slideshow[currentSlide].getScaledInstance(w, h, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
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
    private BufferedImage[] getImages(BufferedImage[] oldImages) {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage()); //load images
        BufferedImage[] images = null;
        //if the folder is empty
        if (imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        } //otherwise it has something in it
        else {
            //check modified timestamps
            //if the new file list has been modified since last update then reload images
            long temp = getLatestModified(imageFiles); //store time in temp var so files are only accessed once
            if (temp > this.modified) {
                this.modified = temp; //store new modified time
                
                //flush old images out of memory
                for (int i = 0; i < oldImages.length; i++) {
                    oldImages[i].flush();
                }
                images = new BufferedImage[imageFiles.length]; //new array
                //populate image array with new images from file list in files array
                for (int i = 0; i < imageFiles.length; i++) {
                    try {
                        images[i] = ImageIO.read(imageFiles[i]);
                    } catch (IOException e) {
                        System.err.println("Caught IOException: " + e.getMessage());
                        System.exit(1);
                    }
                }
                imagesLoaded = true;
                return images;
            } //otherwise continue using old images
            else {
                return oldImages;
            }

        }
        return null; //program will never get here
    }

    private BufferedImage[] getImages() {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage()); //load array with all image files found in folder
        BufferedImage[] images = null;
        //if the folder is empty
        if (imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        } //otherwise it has something in it
        else {
            //create an array to hold the images
            images = new BufferedImage[imageFiles.length];
            //populate image array with new images from file list in files array
            for (int i = 0; i < imageFiles.length; i++) {
                try {
                    images[i] = ImageIO.read(imageFiles[i]); //create BufferedImages
                } catch (IOException e) {
                    System.err.println("Caught IOException: " + e.getMessage());
                    System.exit(1);
                }
            }
            imagesLoaded = true;
            modified = getLatestModified(imageFiles); //set modified time to be checked on next update
            return images;


        }
        return null; //the program will never get here as imageFiles will either be null or not null.
    }

    //Should return a long int representing the most recently modified file's modification time
    public long getLatestModified(File[] files) {
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
