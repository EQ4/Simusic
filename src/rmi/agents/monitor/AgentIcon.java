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
package rmi.agents.monitor;

/**
 *
 * @author Martin Minovski <martin at minovski.net>
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
