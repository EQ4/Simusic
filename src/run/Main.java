/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Random;
import javax.swing.JTextArea;
import rmi.interfaces.RegistryInterface;
import rmi.registry.Registry;
import rmi.registry.RegistryFrame;
import rmi.monitor.MonitorFrame;

/**
 *
 * @author Martin
 */
public class Main {
    public static final Random rand = new Random();
    
    public static enum AgentType {

        AIPerformer, HumanPerformer, Monitor
    }
    
    //Monitor vars
    public static MonitorFrame monitorFrame;
    public static int portCounter = 51000 + rand.nextInt(5000);
    public static RegistryInterface registryConnection;
    public static Integer monitorID;
    public static int updateDelay;
    //New
    
    
    //Registry vars
    public static String registryIPAddress;
    public static String registryName;
    public static int registryPort;
    public static int registryServicePort;
    public static JTextArea logOfRegistry;
    public static Registry myRegistryObject;
    public static java.rmi.registry.Registry rmiRegistryLocated;
    //New
    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws RemoteException {
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
                monitorFrame = new MonitorFrame();
            }
        });
    }

    public static void startLocalRegistryDaemon(String ipAddress, String registryName, int regPort, int regSport, int updatePeriod) throws RemoteException {
        RegistryFrame registryDaemon = new RegistryFrame(ipAddress, registryName, regPort, regSport, updatePeriod);
        Thread localRegistryWorker = new Thread(registryDaemon);
        localRegistryWorker.setDaemon(true);
        localRegistryWorker.start();
    }

}
