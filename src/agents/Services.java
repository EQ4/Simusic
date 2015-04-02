/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Martin
 */
public class Services {
    
    public static String[] splitMessage(ACLMessage msg)
    {
        if (msg  == null) return null;
        return msg.getContent().split(";");
    }
    
    public static ACLMessage SendMessage(String recipient, String message) {
        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setContent(message);
        response.addReceiver(new AID(recipient, AID.ISGUID));
        return response;
    }
}
