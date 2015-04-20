/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import rmi.interfaces.RegistryInterface;
import rmi.registry.RegistryDaemon;
import rmi.registry.RegistryFrame;
import rmi.monitor.MonitorFrame;

/**
 *
 * @author Martin
 */
public class Main {

    public static String[] names;
    public static Random rand;
    public static int windowsOpened;

    private static MonitorFrame mainMonitorWindow;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws RemoteException {
        //Initialise static variables
        rand = new Random();
        windowsOpened = 0;
        
        //Set RMI policy variables
        System.setProperty("java.security.policy", "resources/simusic.policy");
        System.setSecurityManager(new SecurityManager());

        //Get some agent names
        try {
            names = readLines("resources/names.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            java.util.logging.Logger.getLogger(MonitorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MonitorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MonitorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MonitorFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Start main monitor frame
                mainMonitorWindow = new MonitorFrame();
            }
        });
    }

    public static void closeWindow(JFrame window) {
        if (--windowsOpened == 0) {
            System.exit(1);
        } else {
            JOptionPane.showMessageDialog(window, "Simusic will exit once all windows are closed.");
        }
        window.dispose();
    }

    public static void startLocalRegistryDaemon(String ipAddress, String registryName, int regPort, int regSport) throws RemoteException {
        RegistryFrame newRegistryFrame = new RegistryFrame(ipAddress, registryName, regPort, regSport);
        newRegistryFrame.startDaemon();
    }

    public static int getRandomPort() {
        return 51000 + rand.nextInt(8000);
    }

    public static String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }

    public static String getRandomName() {
        return names[rand.nextInt(names.length)];
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimestamp() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(currentDate) + " ";
    }

}
