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
package rmi.agents.registry;

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
import rmi.agents.monitor.AgentDummy;
import rmi.interfaces.AgentInterface;
import rmi.agents.monitor.AgentDummyLink;
import run.Main;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
 */
public class RegistryFrame extends javax.swing.JFrame implements Runnable {

    static final int DISCONNECT_REQUEST_WAIT_TIME = 500;
    static final int DISCONNECT_REQUEST_ATTEMPTS = 5;
    
    /**
     *
     */
    public boolean lock;

    /**
     *
     */
    public ArrayList<AgentInterface> agentConnections;

    /**
     *
     */
    public ArrayList<AgentDummy> agentDummies;

    /**
     *
     */
    public ArrayList<AgentDummyLink> agentDummyLinks;
    
    /**
     *
     */
    public String registryIPAddress;

    /**
     *
     */
    public String registryName;

    /**
     *
     */
    public String registryFullAddress;

    /**
     *
     */
    public int registryPort;

    /**
     *
     */
    public int registryServicePort;

    /**
     *
     */
    public RegistryDaemon registryDaemon;

    /**
     *
     */
    public java.rmi.registry.Registry rmiRegistryLocation;
    
    /**
     *
     * @param registryIPAddress
     * @param registryName
     * @param registryPort
     * @param registryServicePort
     * @throws RemoteException
     */
    public RegistryFrame(String registryIPAddress, String registryName, int registryPort, int registryServicePort) throws RemoteException {
        initComponents();
        Main.windowsOpened++;

        //Maybe implement locks?
        this.agentConnections = new ArrayList<>();
        this.agentDummies = new ArrayList<>();
        this.agentDummyLinks = new ArrayList<>();
        
        this.registryIPAddress = registryIPAddress;
        this.registryName = registryName;
        this.registryPort = registryPort;
        this.registryServicePort = registryServicePort;
        this.registryFullAddress = "rmi://" + registryIPAddress + ":" + registryPort + "/" + registryName;
        
        this.registryLabel.setText(registryLabel.getText() + " '" + registryName + "'");
        
        this.setVisible(true);

        //Log field auto scroll
        DefaultCaret logCaret = (DefaultCaret) registryLog.getCaret();
        logCaret.setUpdatePolicy(ALWAYS_UPDATE);
        
    }
    
    /**
     *
     */
    public void startDaemon() {
        Thread daemonThread = new Thread(this);
        daemonThread.setDaemon(true);
        daemonThread.start();
    }
    
    @Override
    public void run() {
        try {
            registryDaemon = new RegistryDaemon(this);
            rmiRegistryLocation = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
            Naming.rebind(registryFullAddress, registryDaemon);

            //Done
            registryLog.append("--- SiMusic Registry Log ---\nRegistry information: "
                    + "\n    - name: " + registryName
                    + "\n    - ip: " + registryIPAddress
                    + "\n    - port " + registryPort
                    + "\n    - s. port " + registryServicePort
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
    
    private void shutdown() {
        stopButton.setEnabled(false);
        statusTextField.setText("Stopping daemon...");

        //Broadcast shutdown
        for (AgentInterface agent : agentConnections) {
            try {
                for (int i = 0; i < DISCONNECT_REQUEST_ATTEMPTS; i++) {
                    if (agent.shutdown()) {
                        break;
                    }
                    Main.wait(DISCONNECT_REQUEST_WAIT_TIME);
                }
            } catch (Exception e) {
                //Shouldn't be a problem since we're shutting down
            }
        }

        //Remove RMI naming
        try {
            rmiRegistryLocation.unbind(registryName);
            registryDaemon = null;
        } catch (Exception e) {
            System.out.println("Registry RMI naming already unbound.");
        }

        //Log shutdown
        changeStatus("Daemon stopped");
        log("Daemon has been stopped.", false);
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
    
    /**
     *
     * @param message
     * @param precise
     */
    public void log(String message, boolean precise) {
        registryLog.append(Main.getCurrentTimestamp(precise) + message + "\n");
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

        registryLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        registryLog = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        statusTextField = new javax.swing.JTextField();
        stopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("SiMusic Registry Daemon");
        setMinimumSize(new java.awt.Dimension(550, 650));
        setPreferredSize(new java.awt.Dimension(550, 650));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        registryLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        registryLabel.setText("SiMusic Registry Daemon");

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
                        .addComponent(jLabel2)
                        .addContainerGap(430, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(stopButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(31, 31, 31))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(registryLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(registryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(stopButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        shutdown();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        int dialogResult = JOptionPane.showConfirmDialog(this, "Closing the registry will also terminate any registered agents.\nAre you sure you want to stop registry '" + registryName + "'?", "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            shutdown();
            
            Main.closeWindow(this);
        }
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel registryLabel;
    private javax.swing.JTextArea registryLog;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JButton stopButton;
    // End of variables declaration//GEN-END:variables
}
