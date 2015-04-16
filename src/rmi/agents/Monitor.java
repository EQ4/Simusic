/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.agents;

import rmi.dummies.MonitorDummy;
import rmi.dummies.AgentDummy;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import static rmi.registry.Registry.AgentType;
import javax.swing.text.DefaultCaret;
import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;
import rmi.registry.Registry;
import rmi.interfaces.MonitorInterface;
import run.Main;

/**
 *
 * @author Martin
 */
public class Monitor extends javax.swing.JFrame {

    Integer id;
    MonitorInterface registryConnection;
    static Random rand = new Random();

    public Monitor() {
        initComponents();
        this.setVisible(true);
        DefaultCaret logCaret = (DefaultCaret) this.logField.getCaret();
        logCaret.setUpdatePolicy(ALWAYS_UPDATE);

        workingPane.getViewport().setBackground(new java.awt.Color(240, 248, 255));
    }

    public boolean pingRegistry() throws RemoteException {
        return registryConnection.ping(id);
    }

    public boolean isAssignedID() throws RemoteException {
        return (id != null);
    }

    public boolean checkConnection() throws RemoteException {
        boolean truthValue = pingRegistry() && isAssignedID();
        if (truthValue) {
            agentsMenu.setEnabled(true);
        } else {
            agentsMenu.setEnabled(false);
            statusTextField.setText("Disconnected");
            monitorIdTextField.setText("");
        }
        return truthValue;
    }

    private void startNewRegistry() throws RemoteException {
        String randomName = "SimusicRegistry" + rand.nextInt(Registry.MAX_REGISTRY_ID);
        String registryName = (String) JOptionPane.showInputDialog(
                this,
                "Enter registry name:\n"
                + "If name is \n    " + randomName + "\n then the RMI service URL will be\n    "
                + "//localhost/" + randomName + "\n \n",
                "Start new registry",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                randomName);
        if ((registryName == null) || registryName.isEmpty()) {
            return;
        }
        Main.startLocalRegistryDaemon(registryName);
        log("Local registry created: " + registryName);

        //Also connect
        
        connectToRegistry("//localhost/" + registryName);
    }

    private void connectToRegistry(String registryURL) throws RemoteException {
        log("Connecting to " + registryURL);
        System.setSecurityManager(new SecurityManager());
        try {
            registryConnection = (MonitorInterface) Naming.lookup(registryURL);
        } catch (Exception e) {
            System.out.println("Monitor to registry connection exception: " + e.getMessage());
            e.printStackTrace();
        }

        id = registryConnection.connect(AgentType.Monitor);

        if (checkConnection()) {
            log("Connected to " + registryURL + "!");
            statusTextField.setText("Connected!");
            monitorIdTextField.setText(id.toString());
        }
    }

    private void connectToRegistry() throws RemoteException {
        String registryURL = (String) JOptionPane.showInputDialog(
                this,
                "Enter registry URL",
                "Connect to existing registry",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");
        if ((registryURL == null) || registryURL.isEmpty()) {
            return;
        }

        connectToRegistry(registryURL);
    }

    private void addAiPerformer() throws RemoteException {
        createAgent("ai1", AgentType.AIPerformer);
    }

    private void addHumanPerformer() throws RemoteException {
        createAgent("h1", AgentType.HumanPerformer);
    }

    private void createAgent(String name, AgentType agentType) throws RemoteException {
        AgentDummy newAgentDummy;

        try {
            switch (agentType) {
                case AIPerformer:
                    newAgentDummy = new MonitorDummy(name, -1, Inet4Address.getLocalHost());
                    break;
                case HumanPerformer:
                    break;
                case Monitor:
                    newAgentDummy = new MonitorDummy(name, -1, InetAddress.getLocalHost());
                    break;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        workingPane.revalidate();
        workingPane.repaint();

        revalidate();
        repaint();

        //agentDummies.add(newAgentDummy);
    }

    private void showAbout() {
        alert("Simusic is awesome!");
    }

    private void alert(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void log(String message) {
        logField.append(message + "\n");
    }

    private void test() throws RemoteException {
        alert("Registry says " + registryConnection.sayHello());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        logField = new javax.swing.JTextArea();
        workingPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        statusTextField = new javax.swing.JTextField();
        monitorIdTextField = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        startLocalRegistryMenuItem = new javax.swing.JMenuItem();
        connectToRemoteRegistryMenuItem = new javax.swing.JMenuItem();
        agentsMenu = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        addAiPerformerMenuItem = new javax.swing.JMenuItem();
        addHumanPerformerMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        testMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        jMenuItem1.setText("jMenuItem1");

        jMenu2.setText("jMenu2");

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SiMusic");
        setMinimumSize(new java.awt.Dimension(800, 600));

        logField.setEditable(false);
        logField.setColumns(20);
        logField.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        logField.setRows(5);
        jScrollPane1.setViewportView(logField);

        workingPane.setBackground(new java.awt.Color(255, 255, 255));
        workingPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setText("Status");

        jLabel2.setText("Monitor ID");

        statusTextField.setEditable(false);
        statusTextField.setText("Disconnected");

        monitorIdTextField.setEditable(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(monitorIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(statusTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(monitorIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jMenu1.setText("Registry");

        startLocalRegistryMenuItem.setText("Start local registry daemon");
        startLocalRegistryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startLocalRegistryMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(startLocalRegistryMenuItem);

        connectToRemoteRegistryMenuItem.setText("Connect to existing registry");
        connectToRemoteRegistryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectToRemoteRegistryMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(connectToRemoteRegistryMenuItem);

        jMenuBar1.add(jMenu1);

        agentsMenu.setText("Agents");
        agentsMenu.setEnabled(false);

        jMenu4.setText("Add...");

        addAiPerformerMenuItem.setText("AI Performer");
        addAiPerformerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAiPerformerMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(addAiPerformerMenuItem);

        addHumanPerformerMenuItem.setText("Human Performer");
        addHumanPerformerMenuItem.setToolTipText("");
        addHumanPerformerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHumanPerformerMenuItemActionPerformed(evt);
            }
        });
        jMenu4.add(addHumanPerformerMenuItem);

        agentsMenu.add(jMenu4);

        jMenuBar1.add(agentsMenu);

        helpMenu.setText("Help");

        testMenuItem.setText("Test!");
        testMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(testMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(workingPane)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(workingPane, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addAiPerformerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAiPerformerMenuItemActionPerformed
        try {
            addAiPerformer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addAiPerformerMenuItemActionPerformed

    private void addHumanPerformerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHumanPerformerMenuItemActionPerformed
        try {
            addHumanPerformer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_addHumanPerformerMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        showAbout();
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void startLocalRegistryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startLocalRegistryMenuItemActionPerformed
        try {
            startNewRegistry();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_startLocalRegistryMenuItemActionPerformed

    private void connectToRemoteRegistryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectToRemoteRegistryMenuItemActionPerformed
        try {
            connectToRegistry();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_connectToRemoteRegistryMenuItemActionPerformed

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed
        try {
            test();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_testMenuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addAiPerformerMenuItem;
    private javax.swing.JMenuItem addHumanPerformerMenuItem;
    private javax.swing.JMenu agentsMenu;
    private javax.swing.JMenuItem connectToRemoteRegistryMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea logField;
    private javax.swing.JTextField monitorIdTextField;
    private javax.swing.JMenuItem startLocalRegistryMenuItem;
    private javax.swing.JTextField statusTextField;
    private javax.swing.JMenuItem testMenuItem;
    private javax.swing.JScrollPane workingPane;
    // End of variables declaration//GEN-END:variables
}
