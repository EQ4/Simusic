/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.io.File;
/**
 *
 * @author Martin
 */
public class Performer extends Agent {
    File agentFolder;
    
    @Override
    protected void setup() {
        Object[] agentArgs = getArguments();
        agentFolder = new File((String)agentArgs[0]);
        
        //Add behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    System.out.println(getLocalName() + ": I received '" + incomingMessage.getContent() + "'");
                }
            }
        });

        System.out.println(getLocalName() + ": I am ready! My dir is " + agentFolder.getPath());
    }
}
