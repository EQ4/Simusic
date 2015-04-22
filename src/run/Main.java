/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
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
import music.player.Player;
import rmi.interfaces.RegistryInterface;
import rmi.agents.registry.RegistryDaemon;
import rmi.agents.registry.RegistryFrame;
import rmi.agents.monitor.MonitorFrame;

/**
 *
 * @author Martin
 */
public class Main {

    public static final int DEFAULT_MARKOV_DEPTH = 3;
    public static final int MIN_PORT = 51000;
    public static final int MAX_PORT = 59000;
    public static final int ROUNDING_DIGITS = 1000;

    public static String[] names;
    public static Random rand;
    public static int windowsOpened;
    public static Player player;

    private static MonitorFrame mainMonitorFrame;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws RemoteException {
        //Initialise static variables
        rand = new Random();
        windowsOpened = 0;

        player = new Player();

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
                mainMonitorFrame = new MonitorFrame();
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
        return MIN_PORT + rand.nextInt(MAX_PORT - MIN_PORT);
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

    /**
     * Wait method (STATIC) Sleeps the thread that called it
     *
     * @param ms Duration of sleep period
     */
    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimestamp(boolean precise) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(precise ? "HH:mm:ss.SSS" : "HH:mm:ss");
        return dateFormat.format(currentDate) + " ";
    }

    public static double getRoundedValue(double value) {
        return (double) Math.round(value * ROUNDING_DIGITS) / ROUNDING_DIGITS;
    }

    public static File getRuntimeDir() {
        return new File("runtime");
    }

    public static int getBeatPeriod(int tempo) {
        double beatPeriod = (double) 1000 * ((double) 60 / (double) tempo);
        return (int) beatPeriod;
    }

}
