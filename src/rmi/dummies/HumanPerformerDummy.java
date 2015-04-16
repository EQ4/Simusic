/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.dummies;

import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author Martin
 */
public class HumanPerformerDummy extends AgentDummy {
    
    public HumanPerformerDummy(String name, int id, InetAddress address) {
        super(name, id, address);
    }
    
    @Override
    public String getIconFilename() {
        return "humanperformer";
    }
}
