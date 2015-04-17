/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.registry;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;
import rmi.monitor.AgentDummy;
import rmi.interfaces.AgentInterface;
import run.Main;

/**
 *
 * @author Martin
 */
public class RegistryFrame extends javax.swing.JFrame implements Runnable {

    public boolean lock;
    public ArrayList<AgentInterface> agentConnections;
    public ArrayList<AgentDummy> agentDummies;

    public String registryIPAddress;
    public String registryName;
    public String registryFullAddress;
    public int registryPort;
    public int registryServicePort;
    public RegistryDaemon registryDaemon;
    public java.rmi.registry.Registry rmiRegistryLocation;

    public RegistryFrame(String registryIPAddress, String registryName, int registryPort, int registryServicePort) throws RemoteException {
        initComponents();

        //Maybe implement locks?
        this.agentConnections = new ArrayList<>();
        this.agentDummies = new ArrayList<>();

        this.registryIPAddress = registryIPAddress;
        this.registryName = registryName;
        this.registryPort = registryPort;
        this.registryServicePort = registryServicePort;
        this.registryFullAddress = "rmi://" + registryIPAddress + ":" + registryPort + "/" + registryName;

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void startDaemon() {
        Thread registryDaemon = new Thread(this);
        registryDaemon.setDaemon(true);
        registryDaemon.start();
    }

    @Override
    public void run() {
        try {
            registryDaemon = new RegistryDaemon(this);
            rmiRegistryLocation = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
            Naming.rebind(registryFullAddress, registryDaemon);

            //Done
            registryLog.append("--- SiMusic ---\nRegistry created: "
                    + "\n    - ip: " + registryIPAddress
                    + "\n    - port " + registryPort
                    + "\n    - serv. port " + registryServicePort
                    + "\n    - serv. name: " + registryName
                    + "\n    - address: " + registryFullAddress
                    + "\n");
            statusTextField.setText("Daemon running");
        } catch (ExportException e) {
            if (e.getMessage().contains("already in use")) {
                alert("Port is already in use!");
            } else {
                e.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void shutDown() {
        stopButton.setEnabled(false);
        statusTextField.setText("Stopping daemon...");

        //Broadcast shutdown message
        for (AgentInterface agent : agentConnections) {
            try {
                agent.disconnect();
            } catch (Exception e) {
                //Shouldn't be a problem since we're shutting down
            }
        }

        try {
            rmiRegistryLocation.unbind(registryName);
            registryDaemon = null;
        } catch (Exception e) {
            statusTextField.setText("Error.");
            e.printStackTrace();
        }

        changeStatus("Daemon stopped");
        registryLog.append("Daemon has been stopped.\n");
    }

    void changeStatus(String newStatus) {
        statusTextField.setText(newStatus);
    }

    /**
     * Creates new form RegistryDaemon
     */
    public RegistryFrame() {
        initComponents();

        DefaultCaret logCaret = (DefaultCaret) this.registryLog.getCaret();
        logCaret.setUpdatePolicy(ALWAYS_UPDATE);
    }

    public void log(String message) {
        registryLog.append(message + "\n");
    }

    private void alert(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        registryLog = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        statusTextField = new javax.swing.JTextField();
        stopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SiMusic Registry Daemon");

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("SiMusic Registry Daemon");

        registryLog.setEditable(false);
        registryLog.setColumns(20);
        registryLog.setRows(5);
        jScrollPane1.setViewportView(registryLog);

        jLabel2.setText("Registry log");

        jLabel4.setText("Status");

        statusTextField.setEditable(false);
        statusTextField.setText("Starting...");

        stopButton.setText("Stop daemon");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addContainerGap(334, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(stopButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(31, 31, 31))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(stopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        shutDown();
    }//GEN-LAST:event_stopButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea registryLog;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
