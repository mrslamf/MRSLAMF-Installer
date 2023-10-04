
package installer;

import static installer.Installer.PROGRAM_NAME;
import static installer.Installer.TROUBLESHOOTING;
import static installer.Installer.desktopIsSupported;
import static installer.Installer.fileSep;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import javax.swing.SwingUtilities;
import static installer.Installer.lineSeparator;
import static installer.Installer.preferencesFileName;
import static installer.Installer.sourceDir;
import static installer.Installer.targetDir;
import static installer.Installer.upgradePreferencesNeeded;
import static installer.Installer.usersHomeDirectory;

/**
 *
 * @author tcsma
 * revision date April 19, 2021
 */
public class mainWindow extends javax.swing.JFrame {

    /**
     * Creates new form mainWindow
     */
    /**
     *
     * @author tcsma
     * revision date April 29, 2021
     * revision date Oct. 20, 2022 - added support for Windows 11
     * 
     */
    public mainWindow() {
        initComponents();
        
        Window w = SwingUtilities.windowForComponent(outputTextArea);
        centerWindowOnScreen(w);
        
         
        boolean targetDirectoryExists = false;
        boolean convertPrefsSuccess = false;
        
        if(TROUBLESHOOTING == 1)
            targetDirectoryExists = true;
        
        boolean fileCopyError = false;

        outputTextArea.append("Greeting user " + System.getProperty("user.name") + lineSeparator);
        outputTextArea.append(lineSeparator);
        outputTextArea.append("This computer is running the " + System.getProperty("os.name") + " operating system, version " + System.getProperty("os.version") + lineSeparator);     
        if(!(System.getProperty("os.name").equals("Windows 10")|| System.getProperty("os.name").equals("Windows 11")) && !System.getProperty("os.name").equals("Mac OS X")){
            outputTextArea.append("This program has not been tested on this operating system, but I'll attempt to install the program." + lineSeparator);
            outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of issues." + lineSeparator);
            outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);
        }
        outputTextArea.append(lineSeparator);
        outputTextArea.append("This computer is running Java version " + System.getProperty("java.version") + lineSeparator);    
        outputTextArea.append("This computer is running Java specification version " + System.getProperty("java.specification.version") + lineSeparator);
        float javaSpecVersion = Float.parseFloat(System.getProperty("java.specification.version"));
        if(javaSpecVersion < 1.8f){
            outputTextArea.append("your java version should be 1.8 or higher, but it is " + javaSpecVersion + lineSeparator);
            outputTextArea.append("Suggest you update Java runtime to version 1.8 prior to running this program." + lineSeparator);
        }

       
        
        outputTextArea.append(lineSeparator);
        outputTextArea.append("Preparing to install the program in your home directory: " + usersHomeDirectory + lineSeparator);



             
        if(TROUBLESHOOTING == 0){
            
            // first lets test if the target directory already exists, if so this is an upgrade, not a new install
            File fn = new File(targetDir);
            if(fn.exists() && fn.isDirectory()){
                outputTextArea.append("directory " + targetDir + " already exists" +  lineSeparator);
                targetDirectoryExists = true;
                
            }
            else {
                outputTextArea.append("directory " + targetDir + " does not exist so lets create it"+  lineSeparator);
                boolean dirCreated = fn.mkdir();
                if(dirCreated == true){
                    outputTextArea.append("the directory was successfully created " + targetDir + lineSeparator);
                    targetDirectoryExists = true;
                }
                else {
                    outputTextArea.append("the file directory was not successfully created" + lineSeparator);
                    outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator);
                    outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);
                    targetDirectoryExists = false;
                }
            }

            outputTextArea.append(lineSeparator);
            
            // now lets locate the installer's source directory
            File fs = new File(sourceDir);
            if(targetDirectoryExists && fs.exists() && fs.isDirectory()){     
                // lets get the list of files to be copied
                String fileNames[] = fs.list();
                outputTextArea.append("There are " + fileNames.length + " files to copy." + lineSeparator);
                // copy the files from the source folder to the target directory     
                for(int i=0;i< fileNames.length; i++){        
                    Path sourceFile = Paths.get(sourceDir + fileSep + fileNames[i]);
                    Path targetFile = Paths.get(targetDir + fileSep + fileNames[i]);
                    
                    File source = new File(sourceDir + fileSep + fileNames[i]);
                    File target = new File(targetDir + fileSep + fileNames[i]);
                    if(source.isDirectory() && target.exists()){
                        outputTextArea.append("The directory " + fileNames[i] + " already exists in the target directory."+ lineSeparator);
                    }
                    else {
                        try {
                            Files.copy(sourceFile,targetFile,REPLACE_EXISTING);
                            outputTextArea.append("Copying file " + fileNames[i] + "  to " + targetDir + lineSeparator);
                        } catch (IOException ex) {
                            outputTextArea.append(lineSeparator);
                            outputTextArea.append("IO Exception while copying file " + fileNames[i] + "  to " + targetDir + lineSeparator);
                            outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator);
                            outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);
                            fileCopyError = true;
                        }
                    }
                } // end of stepping through the files list and copying files
                
                
                // if fileCopyError = true at this point, should just stop the install
                
                
                // now lets copy the contents of the lib subdirectory        
                File fsl = new File(sourceDir + fileSep + "lib");
                if(fsl.exists() && fsl.isDirectory()){ 
                    String fileNames2[] = fsl.list();  
                    outputTextArea.append("There are " + fileNames2.length + " library files to copy." + lineSeparator);
                    if(fileNames2.length < 1)
                        fileCopyError = true;
                    for(int i=0;i< fileNames2.length; i++){        
                        Path sourceFile = Paths.get(sourceDir + fileSep + "lib" + fileSep + fileNames2[i]);
                        Path targetFile = Paths.get(targetDir + fileSep + "lib" + fileSep + fileNames2[i]);
                        try {
                            Files.copy(sourceFile,targetFile,REPLACE_EXISTING);
                            outputTextArea.append("Copying file " + fileNames2[i] + "  to " + targetDir + fileSep + "lib" + lineSeparator);
                        } catch (IOException ex) {
                        outputTextArea.append(lineSeparator);
                        outputTextArea.append("IO Exception while coopying file " + fileNames2[i] + "  to " + targetDir + fileSep + "lib" + lineSeparator);
                        outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator);
                        outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);
                        fileCopyError = true;
                        }
                    }
                    if(fileCopyError == true){
                        outputTextArea.append(lineSeparator);
                        outputTextArea.append("Library file copy error " + lineSeparator);
                        outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator);
                        outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);                        
                    }
                }
                else {
                    outputTextArea.append(lineSeparator);
                    outputTextArea.append("Couldn't fine the library folder in " + fsl + lineSeparator); 
                    outputTextArea.append("Email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator);
                    outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);                        

                    fileCopyError = true;
                }
            }
            else {
                outputTextArea.append(lineSeparator);   
                outputTextArea.append("MR SLAM Formatter was NOT successfully installed as the required folder couldn't be created." + lineSeparator); 
                outputTextArea.append(lineSeparator); 
                outputTextArea.append("Make sure you extracted the download and then executed the installer." + lineSeparator); 
                outputTextArea.append("If that doesn't resolve the issue, delete the download"  + lineSeparator); 
                outputTextArea.append("and then download a fresh copy of the install file and attempt the install again." + lineSeparator);
                outputTextArea.append("Should you still have difficulties email the author at mrslamf21@gmail.com to notify me of the issue." + lineSeparator); 
                outputTextArea.append("Copy the info from the installer's window and paste it into the  message" + lineSeparator);
            }
        } // end of if troubleshooting = 0
        
        
        // if the user's preferences file needs updating then lets do it
        if(fileCopyError == false && targetDirectoryExists == true) {
            
            File preferencesFile = new File(targetDir + fileSep + preferencesFileName);
            if(preferencesFile.exists()){
                outputTextArea.append("A preferences file has been found. let's see if it needs converting to the newer version." + lineSeparator);
                convertPrefsSuccess = convertPreferences.isAConversionRequired(preferencesFile);
            }// end of preferences file exists  
            else {
                upgradePreferencesNeeded = false;
            }
        } // end of upgrade Preferences Needed        
        
        
        if(fileCopyError == false && (upgradePreferencesNeeded == false || (upgradePreferencesNeeded == true && convertPrefsSuccess == true))){
            outputTextArea.append(lineSeparator);   
            outputTextArea.append("MR SLAM Formatter has been successfully installed at " + targetDir + lineSeparator);   
        }
        
        
        
        // now lets open up the user's home directory in file explorer
        if(fileCopyError == false && targetDirectoryExists == true && desktopIsSupported == true){
            try{
                File file = new File (targetDir);
                if(file.exists() && file.isDirectory()){
                    Desktop desktop = Desktop.getDesktop();
                    desktop.open(file); 
                }
            }
            catch (IOException e){
                outputTextArea.append(lineSeparator);   
                outputTextArea.append("MR SLAM Formatter folder could not be opened.");   
                outputTextArea.append("Try opening it yourself: " + targetDir + lineSeparator);
            }
        }
        else {
            if(desktopIsSupported == false)
            outputTextArea.append("desktop is not supported on your system. You will have to open up the new program folder manually." + lineSeparator);
        }
        
 
       
        outputTextArea.append(lineSeparator);         
        outputTextArea.append(lineSeparator);   
        outputTextArea.append("Press the Exit button to exit the installer.");            
    }  // end of mainWindow

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        exitButton = new javax.swing.JButton();
        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        versionLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setAlwaysOnTop(true);

        jPanel1.setBackground(new java.awt.Color(0, 153, 255));

        outputTextArea.setColumns(20);
        outputTextArea.setRows(5);
        jScrollPane1.setViewportView(outputTextArea);

        exitButton.setBackground(new java.awt.Color(102, 255, 102));
        exitButton.setFont(new java.awt.Font("Times New Roman", 1, 14)); // NOI18N
        exitButton.setText("Exit");
        exitButton.setBorderPainted(false);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        titleLabel.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        titleLabel.setForeground(new java.awt.Color(255, 255, 255));
        titleLabel.setText("Model Railroad Switch List and Manifest Formatter");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Installer for");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Installer version: ");

        versionLabel.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        versionLabel.setForeground(new java.awt.Color(255, 255, 255));
        versionLabel.setText("jLabel3");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(294, 294, 294)
                                .addComponent(exitButton))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(147, 147, 147)
                                .addComponent(titleLabel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(292, 292, 292)
                                .addComponent(jLabel1)))
                        .addGap(0, 147, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(titleLabel)
                .addGap(43, 43, 43)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(exitButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(versionLabel))
                .addGap(22, 22, 22))
        );

        versionLabel.setText(String.format("%.02f", Installer.PROGRAM_VERSION));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
System.exit(0);
    }//GEN-LAST:event_exitButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(mainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(mainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(mainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(mainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainWindow().setVisible(true);
            }
        });
        


    }

/**
 *
 * @author tcsma
 * revision date April 19, 2021
 */    
public static void centerWindowOnScreen(Window window) {
  final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  final Dimension size = window.getSize();
  
  if (size.height > screenSize.height) {
    size.height = screenSize.height;
  }
  if (size.width > screenSize.width) {
    size.width = screenSize.width;
  }
  window.setLocation((screenSize.width - size.width) / 2,
      (screenSize.height - size.height) / 2);
  
}    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTextArea outputTextArea;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
