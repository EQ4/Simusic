/*
 * The MIT License
 *
 * Copyright 2015 Martin Minovski <martin at minovski.net>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
 * @author Martin Minovski <martin at minovski.net>
 */
public class Main {

    public static final int MIN_PORT = 54000;
    public static final int MAX_PORT = 56000;
    public static final int DEFAULT_MAX_MARKOV_ORDER = 4;
    public static final int PROBABILITY_ROUNDING_DIGITS = 1000;
    public static final int MIDI_NOTE_ON = 0x90;
    public static final int MIDI_NOTE_OFF = 0x80;
    public static final int MAX_MIDI_PITCH = 250;
    public static final int NUMBER_OF_SOLO_PHRASES_PER_AGENT = 4;
    public static final int MAXIMUM_NUMBER_OF_NOTES_PER_PHRASE = 16;
    
    public static String[] names;
    public static Random rand;
    public static int windowsCurrentlyOpened;
    public static Player player;

    /**
     * @param args the command line arguments
     * @throws java.rmi.RemoteException
     */
    public static void main(String args[]) throws RemoteException {
        //Initialise static variables
        rand = new Random();
        windowsCurrentlyOpened = 0;

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
                new MonitorFrame();
            }
        });
    }

    /**
     *
     * @param window
     */
    public static void closeWindow(JFrame window) {
        if (--windowsCurrentlyOpened == 0) {
            System.exit(1);
        } else {
            JOptionPane.showMessageDialog(window, "Simusic will exit once all windows are closed.");
        }
        window.dispose();
    }

    /**
     *
     * @param ipAddress
     * @param registryName
     * @param regPort
     * @param regSport
     * @throws RemoteException
     */
    public static void startLocalRegistryDaemon(String ipAddress, String registryName, int regPort, int regSport) throws RemoteException {
        RegistryFrame newRegistryFrame = new RegistryFrame(ipAddress, registryName, regPort, regSport);
        newRegistryFrame.startDaemon();
    }

    /**
     *
     * @return
     */
    public static int getRandomPort() {
        return MIN_PORT + rand.nextInt(MAX_PORT - MIN_PORT);
    }

    /**
     *
     * @param filename
     * @return
     * @throws IOException
     */
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

    /**
     *
     * @return
     */
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

    /**
     *
     * @param precise
     * @return
     */
    public static String getCurrentTimestamp(boolean precise) {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(precise ? "HH:mm:ss.SSS" : "HH:mm:ss");
        return dateFormat.format(currentDate) + " ";
    }

    /**
     *
     * @param value
     * @return
     */
    public static double getRoundedValue(double value) {
        return (double) Math.round(value * PROBABILITY_ROUNDING_DIGITS) / PROBABILITY_ROUNDING_DIGITS;
    }

    /**
     *
     * @return
     */
    public static File getRuntimeDir() {
        return new File("runtime");
    }

    /**
     *
     * @param tempo
     * @return
     */
    public static int getBeatPeriod(int tempo) {
        double beatPeriod = (double) 1000 * ((double) 60 / (double) tempo);
        return (int) beatPeriod;
    }

}
