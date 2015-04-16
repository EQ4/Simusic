/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package run;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import rmi.registry.Registry;
import rmi.registry.RegistryDaemon;
import rmi.agents.Monitor;

/**
 *
 * @author Martin
 */
public class Main {

    public static Monitor mainMonitor;
    public static Thread localRegistryWorker;

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
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Monitor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //Start main monitor frame
                mainMonitor = new Monitor();
            }
        });
    }

    public static void startLocalRegistryDaemon(String ipAddress, String registryName, int regPort, int regSport, int updatePeriod) throws RemoteException {
        RegistryDaemon registryDaemon = new RegistryDaemon(ipAddress, registryName, regPort, regSport, updatePeriod);
        localRegistryWorker = new Thread(registryDaemon);
        localRegistryWorker.setDaemon(true);
        localRegistryWorker.start();
    }

}
