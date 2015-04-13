/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Martin
 */
public class Services {

    public static String[] splitMessage(ACLMessage msg) {
        if (msg == null) {
            return null;
        }
        return msg.getContent().split(";");
    }

    public static ACLMessage SendMessage(String recipient, String message) {
        ACLMessage response = new ACLMessage(ACLMessage.INFORM);
        response.setContent(message);
        response.addReceiver(new AID(recipient, AID.ISGUID));
        return response;
    }

    public static void wait(int milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void halt(String message, Object object) {
        try {
            System.out.println(" @ The following component has requested halt: " + object.toString());
            System.out.println(" @ Reason: '" + message + "'");
            System.out.println(" @ Press Enter to continue....");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            buffer.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
