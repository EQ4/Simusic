/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.monitor;

/**
 *
 * @author Martin
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;

import javax.swing.JLabel;
import javax.swing.JPanel;

abstract class AgentIcon extends JPanel {

    JLabel iconLabel;
    JLabel textLabel;
    MouseAdapter iconMA;
    MouseAdapter textMA;

    public int agentIDNote;
    
    public AgentIcon(ImageIcon icon, String text, int agentIDNote) {
        iconLabel = new JLabel(icon);
        textLabel = new JLabel(text);
        iconMA = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
                iconClicked();
            }
        };
        textMA = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                super.mouseClicked(me);
            }
        };
        iconLabel.addMouseListener(iconMA);
        textLabel.addMouseListener(textMA);
        add(iconLabel);
        add(textLabel);
        
        this.agentIDNote = agentIDNote;
    }

    abstract void iconClicked();

    public JLabel getIconLabel() {
        return iconLabel;
    }

    public JLabel getTextLabel() {
        return textLabel;
    }
}
