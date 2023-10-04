
package installer;



//import static JavaManifestFormatter.newPreferencesWindow.selectedFontSize;
import static installer.Installer.NEW_PROGRAM_VERSION;
import static installer.Installer.fileSep;
import static installer.Installer.lineSeparator;
import static installer.Installer.preferencesFileName;
import static installer.Installer.secondaryPreferencesFileName;
import static installer.Installer.targetDir;
import static installer.mainWindow.outputTextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Float.parseFloat;
import static java.lang.Math.round;
import java.util.Arrays;


/**
 *
 * @author tcsma
 * This class is designed to change preferences files from ver 1.14 and earlier to 2.00
 * This entails converting each of the column lengths to widths
 * in both the main and secondary prefs files
 */
public class convertPreferences {
    static int counter = 0; 
    static String[] dataIn = new String[1000]; // maximum # of data to be stored
    static boolean primaryPrefs = true; // when true reads/writes primary prefs, when false reads/writes secondary prefs

    
public static boolean isAConversionRequired(File preferencesFile){
    boolean prefFileConversionSuccessful = false;
    boolean fileError = false;
    // lets read the primary pref file and check the rev to see if conversion is required
    fileError = readPreferences(preferencesFile);  
    if(fileError == false){
        
        // read program version from preference file and see if it requires conversion
        float prefVer = parseFloat(dataIn[0].replace(',', '.'));// for international users where , is the decimal separator

        if(prefVer < NEW_PROGRAM_VERSION){
            // then convert the file
            outputTextArea.append("An old version of the program's preferences file was found." + lineSeparator);
            outputTextArea.append("Updating the program's preferences file to the new file format." + lineSeparator);  
            
            prefFileConversionSuccessful = manageConversions();// no error checking as it is handled in the submethods
        }
        else {
            outputTextArea.append("The preferences file is current, no update required." + lineSeparator);
            prefFileConversionSuccessful = true;
        }  
    }
    else{
        outputTextArea.append("IO error reading the primary prefernce file." + lineSeparator);
        outputTextArea.append("You may need to delete the file and let the program write new ones." + lineSeparator);
        prefFileConversionSuccessful = false;
    }
    
    return(prefFileConversionSuccessful);
    
}

public static boolean manageConversions(){
    // Note for conversion from ver 1.14-beta to 2.00 both the pref & secondary prefs need to be updated
    // the toggle prefs do not need to be updated by the installer
    
    
    boolean prefFileConversionSuccessful = false;
    boolean fileError = false;
    File prefFile = null;
    
    

    prefFile = new File(targetDir + fileSep + preferencesFileName);
//    readPreferences(prefFile);
    // the file needs updating so let's convert it
    convertPreferences();
    //  write new primary preference file
    boolean prefWriteSuccessful = writePreferences(prefFile);

    
    prefFile = new File(targetDir + fileSep + secondaryPreferencesFileName);
    readPreferences(prefFile);
    // the file needs updating so let's convert it
    convertPreferences();
    //  write new primary preference file
    boolean prefWriteSuccessful2 = writePreferences(prefFile);
    
    
    if(prefWriteSuccessful == true && prefWriteSuccessful2 == true){
            outputTextArea.append("The primary & secondary prefernce files been successfully written in the new file format." + lineSeparator);
            prefFileConversionSuccessful = true;
    }
    else{
        // handle write pref error
        if(prefWriteSuccessful == false){
        prefFileConversionSuccessful =false;
        outputTextArea.append("The primary prefernce file was NOT successfully updated." + lineSeparator);
        outputTextArea.append("You may need to delete the file and let the program write new ones." + lineSeparator);
        }
        
        
         if(prefWriteSuccessful2 == false){
        prefFileConversionSuccessful =false;
        outputTextArea.append("The secondary prefernce file was NOT successfully updated." + lineSeparator);
        outputTextArea.append("You may need to delete the file and let the program write new ones." + lineSeparator);
        }   
    }

    return(prefFileConversionSuccessful);    
    }
    

    
    
    
    
    
    



public static boolean readPreferences(File preferencesFile){
    boolean fileError = false;    
    String line = "";
    
    //clear dataIn array
    Arrays.fill(dataIn,"");
    counter = 0;
    
    // read file into dataIn array
    try(BufferedReader br = new BufferedReader(new FileReader(preferencesFile.getAbsolutePath()))){

        while ((line = br.readLine()) != null) { 
        dataIn[counter] = line;
//        System.out.println(counter + "  " + dataIn[counter]);
        counter++;
        } // end of while read loop

        br.close();

    }
    catch (IOException x) {
        System.err.format("IOException: %s%n", x);
        fileError = true;
        outputTextArea.append(lineSeparator);
        outputTextArea.append("IO Exception while reading file " + preferencesFile.toString() + lineSeparator);
    } // end of catch    

    return (fileError);

}          

 

private static void convertPreferences(){
    
    // pref file conversion from 1.14-beta to 2.00
    // converts the field length to field width
    // pu, so, & mv car fields & pu & sl loco fields
    
        for(int x = 8; x <= 36; x++){//colWidth array
        dataIn[x] = Integer.toString(round(Integer.valueOf(dataIn[x]) * 6.11f));
    }

    for(int x = 37; x <= 65; x++){//scColWidth array
        dataIn[x] = Integer.toString(round(Integer.valueOf(dataIn[x]) * 6.11f));
    }    
    
    for(int x = 88; x <= 107; x++){// puLocoColWidths
        dataIn[x] = Integer.toString(round(Integer.valueOf(dataIn[x]) * 6.11f));        
    }    

    for(int x = 154; x <= 182; x++){ // mvColWidth
        dataIn[x] = Integer.toString(round(Integer.valueOf(dataIn[x]) * 6.11f)); 

    }     

    for(int x = 207; x <= 226; x++){// scLocoColWidths
        dataIn[x] = Integer.toString(round(Integer.valueOf(dataIn[x]) * 6.11f)); 
    }  
   
    
}




public static boolean writePreferences(File preferencesFile){
    
boolean prefWriteSuccessful = true;    
try{
    FileWriter fw = new FileWriter(preferencesFile);
    BufferedWriter bw = new BufferedWriter(fw);
        
    // now write the prefs back to the file
//    bw.write(String.format("%,.2f",NEW_PROGRAM_VERSION)+ "\n"); 
    for(int x = 0; x < counter; x++){
        bw.write(dataIn[x] + "\n");
    }
    bw.close();

    
} // end of try
catch (IOException x) {
    System.err.format("IOException: %s%n", x);
    outputTextArea.append(lineSeparator);
    outputTextArea.append("IO Exception while writing file " + preferencesFile.toString() + lineSeparator);
    prefWriteSuccessful = false;
} // end of catch    
 
return (prefWriteSuccessful);

}
 
} // end of class
