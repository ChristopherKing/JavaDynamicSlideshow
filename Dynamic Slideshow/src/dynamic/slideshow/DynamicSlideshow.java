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
    String folder;
    long modified;
 
 
    // Program entry 
    public static void main(String[] args) throws Exception { 
        
    } 
    
    /*
     * This method loads all images in a folder into an array of images.
     * In order to determine if there has been an update in the folder it checks
     * the file modification time stamps against a stored timestamp. If the
     * current modification timstamps in the folder are more recent than the
     * stored one all the images in the folder will be reloaded.
     */
    private Image[] loadImages(Image[] oldImages) {
        //load folder
        File datFolder = new File(folder);
        File[] imageFiles = datFolder.listFiles(new OnlyImage());
        Image[] images = null;
        //if the folder is empty
        if(imageFiles == null) {
            System.out.println("The folder was empty or did not contain images.");
            System.exit(0);
        }
        //otherwise it has something in it
        else {
            //check modified timestamps
            //if the new file list has been modified since last update then reload images
            if(getLatestModified(imageFiles) > this.modified) {
                images = new Image[imageFiles.length];
                //populate image array with new images from file list in files array
                for(int i=0;i<imageFiles.length;i++) {
                    images[i] = Toolkit.getDefaultToolkit().createImage(imageFiles[i].getAbsolutePath());
                }
                return images;
            }
            //otherwise continue using old images
            else {
                return oldImages;
            }
            
        }
        return images;
    }
    
    //Should return a long int representing the most recently modified file's modification time
    public static long getLatestModified(File[] files) {
        long longTime = 0L;
        if(files == null)
            return 0L;
        else {
            //find the most recently modified file time
            for(int i=0;i<files.length;i++) {
                if(files[i].lastModified() > longTime)
                    longTime = files[i].lastModified();
            }
        }
        
        return longTime;
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
