/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.Services;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.wrapper.*;
import java.io.File;
import java.io.FileFilter;
import test.RunTests;

/**
 *
 * @author Martin
 */
public class Studio extends Agent {

    String[] performerGUIDs;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        FileFilter directoryFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File mainFolder = new File((String) args[0]);
        File[] folders = mainFolder.listFiles(directoryFilter);

        performerGUIDs = new String[folders.length];
        AgentContainer c = getContainerController();
        for (int i = 0; i < folders.length; i++) {
            try {
                String maxMarkovLevel = RunTests.maxMarkovHarmonyLevel + "";
                Object[] agentArgs = new Object[]{folders[i].getPath(), (String)getName(), (String)maxMarkovLevel};
                AgentController a = c.createNewAgent(folders[i].getName(), "agents.Performer", agentArgs);
                a.start();
                performerGUIDs[i] = a.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Add message behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage incomingMessage = blockingReceive();
                if (incomingMessage != null) {
                    System.out.println(getLocalName() + ": I received '" + incomingMessage.getContent() + "' from " + incomingMessage.getSender().getLocalName());
                }
            }
        });
        
        //sendTestMessageToPerformers();
    }

    void sendTestMessageToPerformers() {
        System.out.println("Studio: Sending test messages to performers...");
        for (String performerGUID : performerGUIDs) {
            //Send a test message to the performer
            send(agents.Services.SendMessage(performerGUID, "Hi from the studio!"));
        }
    }
}
