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
import java.util.logging.Level;
import java.util.logging.Logger;

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
                Object[] agentArgs = new Object[]{folders[i].getPath()};
                AgentController a = c.createNewAgent(folders[i].getName(), "agents.Performer", agentArgs);
                a.start();
                performerGUIDs[i] = a.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Add behaviour
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                try {
                    System.out.println("Studio: Sending messages to performers...");
                    for (String performerGUID : performerGUIDs) {
                        //Send a test message to the performer
                        send(agents.Services.SendMessage(performerGUID, "Hi from the studio!"));
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Studio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
