
package installer;

import java.awt.Desktop;

/**
 *
 * @author tcsma
 *  rev date 07/13/2022 - version 1.17 with change to Java 11 libraries
 *  rev date 10/22/2022 - version 1.21 with change back to Java 8 libraries and added that Windows 11 is supported
 */
public class Installer {

    public static final String PROGRAM_TITLE = "Installer";
    public static final float PROGRAM_VERSION = (float) 2.00;
    public static final String PROGRAM_DATE = "May 30, 2023"; 
    public static final int TROUBLESHOOTING = 0; // set to zero for normal program operation

    public static String fileSep = System.getProperty("file.separator");
    public static String lineSeparator = System.getProperty("line.separator");
    
    public static String usersHomeDirectory = System.getProperty("user.home");
    public static String usersCurrentDirectory = System.getProperty("user.dir");    
    public static String PROGRAM_NAME = "MR SLAM FORMATTER";
    public static String preferencesFileName =  PROGRAM_NAME + " preferences.txt";
    public static String secondaryPreferencesFileName = PROGRAM_NAME + " Secondary preferences.txt";
    public static boolean upgradePreferencesNeeded = true;
    public static final float OLD_PROGRAM_VERSION = 1.14f;// MR SLAM Formatter version with old preferences file format
    public static final float NEW_PROGRAM_VERSION = 2.00f;// MR SLAM Formatter version with new preferences file format    

    public static boolean desktopIsSupported = Desktop.isDesktopSupported();
        

    public static String sourceDir = usersCurrentDirectory + fileSep + "source" + fileSep;
    public static String targetDir = usersHomeDirectory + fileSep + PROGRAM_NAME;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    
    String[] myText = new String[2];    
    mainWindow.main(myText);
    
               
    }
    
}
