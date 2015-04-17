/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

import rmi.interfaces.AgentInterface;
import rmi.interfaces.RegistryInterface;

/**
 *
 * @author Martin
 */
public class AgentControlPanel extends javax.swing.JFrame implements Runnable {

    int agentID;

    public AgentControlPanel(AgentDummy dummy) {

        initComponents();

        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

        nameLabel.setText(dummy.name + ", " + dummy.agentType.toString());
        addressField.setText(dummy.address + ":" + dummy.port);
        if (dummy.masterMonitorID != null) {
            masterMonitorField.setText(dummy.masterMonitorID.toString());
        }

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    @Override
    public void run() {

    }

    ;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameLabel = new javax.swing.JLabel();
        addressField = new javax.swing.JLabel();
        masterMonitorField = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        nameLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        nameLabel.setText("Agent Name");

        addressField.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        addressField.setText("Agent address");

        masterMonitorField.setText("No master monitor");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(281, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addressField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(masterMonitorField)
                        .addGap(119, 119, 119))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addressField)
                    .addComponent(masterMonitorField))
                .addGap(28, 28, 28)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(357, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel addressField;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel masterMonitorField;
    private javax.swing.JLabel nameLabel;
    // End of variables declaration//GEN-END:variables
}
